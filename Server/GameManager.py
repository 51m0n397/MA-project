import threading
import uuid
import time
from Enum import enum
import copy


Error = enum('NOT_FOUND', 'NOT_WAITING', 'NOT_PLAYING', 'NOT_AUTHORIZED')
GameState = enum('WAITING', 'PLAYING', 'OVER')
PlayerState = enum('ALIVE', 'DEAD')
BonusType = enum('FAST', 'BIG', 'FREEZE')

games = {}
threadLock = threading.Lock()
timeout = 30
EXIT = threading.Event()

class Player:
    def __init__(self, user):
        self.name = user['name']
        self.id = user['sub']
        self.score = 0
        self.state = PlayerState.ALIVE
        self.bonus = None

    def dict(self):
        player = copy.deepcopy(self.__dict__)
        del player['id']
        return player


class Game:
    def __init__(self, player1):
        self.id = uuid.uuid4().hex
        self.player1 = Player(player1)
        self.player2 = None
        self.state = GameState.WAITING
        self.timestamp = time.time()

    def dict(self):
        game = copy.deepcopy(self.__dict__)
        game['player1'] = self.player1.dict()
        game['player2'] = None if self.player2 == None else self.player2.dict()
        return game


class GamesManager(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)

    def run(self):
        while not EXIT.is_set():
            EXIT.wait(5)
            threadLock.acquire()
            for game in games.copy().values():
                if time.time() > game.timestamp + timeout:
                    print("deleting stale game: " + game.id)
                    del games[game.id]
            threadLock.release()
    
    def stop(self):
        EXIT.set()


def newGame(player1):
    game = Game(player1)
    threadLock.acquire()
    games[game.id] = game
    threadLock.release()
    return game.dict()


def getPendingGames(player):
    threadLock.acquire()
    pendingGames = [v.dict() for v in games.values() if v.state == GameState.WAITING and v.player1.id != player['sub']]
    threadLock.release()
    return pendingGames


def getGame(game_id, user):
    game = Error.NOT_FOUND
    threadLock.acquire()
    if game_id in games.keys():
        game = Error.NOT_AUTHORIZED
        if (games[game_id].player1 != None and games[game_id].player1.id == user['sub']) or (games[game_id].player2 != None and games[game_id].player2.id == user['sub']):
            game = games[game_id].dict()
    threadLock.release()
    return game


def joinGame(game_id, player2):
    game = Error.NOT_FOUND
    threadLock.acquire()
    if game_id in games.keys():
        game = games[game_id]
        if game.state == GameState.WAITING:
            if game.player1.id == player2['sub']:
                game = Error.NOT_AUTHORIZED
            else:
                game.player2 = Player(player2)
                game.state = GameState.PLAYING
                game.timestamp = time.time()
                game = game.dict()
        else:
            game = Error.NOT_WAITING
    threadLock.release()
    return game


def updateGame(game_id, user, score, state, bonus):
    game = Error.NOT_FOUND
    threadLock.acquire()
    if game_id in games.keys():
        game = games[game_id]
        player = None

        if user['sub'] == game.player1.id:
            player = game.player1
        elif user['sub'] == game.player2.id:
            player = game.player2

        player.bonus = bonus
        
        if player == None:
            game = Error.NOT_AUTHORIZED
        elif game.state == GameState.PLAYING:
            if state == PlayerState.ALIVE and player.state == PlayerState.ALIVE:
                if score > player.score:
                    player.score = score
            if state == PlayerState.DEAD and player.state == PlayerState.ALIVE:
                player.state = PlayerState.DEAD
                if score > player.score:
                    player.score = score
            if game.player1.state == PlayerState.DEAD and game.player2.state == PlayerState.DEAD:
                game.state = GameState.OVER
            game.timestamp = time.time()
            game = game.dict()
        else:
            game = Error.NOT_PLAYING
    threadLock.release()
    return game