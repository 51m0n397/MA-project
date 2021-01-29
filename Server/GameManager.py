import threading
import uuid
import time
from Enum import enum
import copy


Error = enum('NOT_FOUND', 'NOT_WAITING', 'NOT_PLAYING')
GameState = enum('WAITING', 'PLAYING', 'OVER')
PlayerState = enum('ALIVE', 'DEAD')

games = {}
threadLock = threading.Lock()
timeout = 30
EXIT = threading.Event()

class Player:
    def __init__(self, name):
        self.name = name
        self.score = 0
        self.state = PlayerState.ALIVE


class Game:
    def __init__(self, player1):
        self.id = uuid.uuid4().hex
        self.player1 = Player(player1)
        self.player2 = None
        self.state = GameState.WAITING
        self.timestamp = time.time()

    def obj(self):
        game = copy.deepcopy(self.__dict__)
        game['player1'] = self.player1.__dict__
        game['player2'] = None if self.player2 == None else self.player2.__dict__
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
                    print("delating stale game: " + game.id)
                    del games[game.id]
            threadLock.release()
    
    def stop(self):
        EXIT.set()


def newGame(player1):
    game = Game(player1)
    threadLock.acquire()
    games[game.id] = game
    threadLock.release()
    return game.obj()


def getPendingGames():
    threadLock.acquire()
    pendingGames = [v.obj() for v in games.values() if v.state == GameState.WAITING]
    threadLock.release()
    return pendingGames


def getGame(game_id):
    game = Error.NOT_FOUND
    threadLock.acquire()
    if game_id in games.keys():
        game = games[game_id].obj()
    threadLock.release()
    return game


def joinGame(game_id, player2):
    game = Error.NOT_FOUND
    threadLock.acquire()
    if game_id in games.keys():
        game = games[game_id]
        if game.state == GameState.WAITING:
            game.player2 = Player(player2)
            game.state = GameState.PLAYING
            game.timestamp = time.time()
            game = game.obj()
        else:
            game = Error.NOT_WAITING
    threadLock.release()
    return game


def updateGame(game_id, num, score, state):
    game = Error.NOT_FOUND
    threadLock.acquire()
    if game_id in games.keys():
        game = games[game_id]
        if game.state == GameState.PLAYING:
            player = game.player1
            if num == 2: player = game.player2
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
            game = game.obj()
        else:
            game = Error.NOT_PLAYING
    threadLock.release()
    return game