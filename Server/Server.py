from flask import Flask,jsonify
from flask_restful import Resource, Api, abort, reqparse
import random
import uuid
import json

def enum(*sequential, **named):
    enums = dict(zip(sequential, range(len(sequential))), **named)
    return type('Enum', (), enums)


app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('player_name', type=str, required=True)

putParser = reqparse.RequestParser()
putParser.add_argument('player', type=int, required=True)
putParser.add_argument('score', type=int, required=True)
putParser.add_argument('state', type=int, required=True)

GameState = enum('WAITING', 'PLAYING', 'OVER')
PlayerState = enum('ALIVE', 'DEAD')

class Game:
    def __init__(self, player1):
        self.id = uuid.uuid4().hex
        self.player1 = {'name': player1, 'score': 0, 'state': PlayerState.ALIVE}
        self.player2 = None
        self.state = GameState.WAITING

    def __str__(self):
        return json.dumps(newGame.__dict__)

games = {}

class GamesList(Resource):
    # Returns the list of pending games
    def get(self):
        return jsonify([v.__dict__ for v in games.values() if v.state == GameState.WAITING])

    # Creates a new game
    def post(self):
        args = parser.parse_args()
        game = Game(args['player_name'])
        games[game.id] = game
        return jsonify(game.__dict__)

class GameId(Resource):
    # Returns the game with id game_id
    def get(self, game_id):
        if game_id in games.keys():
            return jsonify(games[game_id].__dict__)
        return abort(404, message="game {} doesn't exist".format(game_id))
            
    # Joins the game with id game_id
    def post(self, game_id):
        args = parser.parse_args()
        if game_id in games.keys():
            if (games[game_id].state == GameState.WAITING):
                games[game_id].player2 = {'name': args['player_name'], 'score': 0, 'state': PlayerState.ALIVE}
                games[game_id].state = GameState.PLAYING
                return jsonify(games[game_id].__dict__)
            return abort(403, message="cannot join a game in the PLAYING state")
        return abort(404, message="game {} doesn't exist".format(game_id))

    # Updates the game with id game_id
    def put(self, game_id):
        args = putParser.parse_args()
        if game_id in games.keys():
            if games[game_id].state == GameState.PLAYING:
                if args['player'] == 1:
                    if args['state'] == PlayerState.ALIVE and games[game_id].player1['state'] == PlayerState.ALIVE:
                        if args['score'] > games[game_id].player1['score']:
                            games[game_id].player1['score'] = args['score']
                    if args['state'] == PlayerState.DEAD and games[game_id].player1['state'] == PlayerState.ALIVE:
                        games[game_id].player1['state'] = PlayerState.DEAD
                        if args['score'] > games[game_id].player1['score']:
                            games[game_id].player1['score'] = args['score']
                    if games[game_id].player1['state'] == PlayerState.DEAD and games[game_id].player2['state'] == PlayerState.DEAD:
                        games[game_id].state = GameState.OVER
                    return jsonify(games[game_id].__dict__)
                if args['player'] == 2:
                    if args['state'] == PlayerState.ALIVE and games[game_id].player2['state'] == PlayerState.ALIVE:
                        if args['score'] > games[game_id].player2['score']:
                            games[game_id].player2['score'] = args['score']
                    if args['state'] == PlayerState.DEAD and games[game_id].player2['state'] == PlayerState.ALIVE:
                        games[game_id].player2['state'] = PlayerState.DEAD
                        if args['score'] > games[game_id].player2['score']:
                            games[game_id].player2['score'] = args['score']
                    if games[game_id].player1['state'] == PlayerState.DEAD and games[game_id].player2['state'] == PlayerState.DEAD:
                        games[game_id].state = GameState.OVER
                    return jsonify(games[game_id].__dict__)
                return abort(400, message="argument 'player' can only be 1 or 2")
            return abort(403, message="cannot update a game in the WAITING state")
        return abort(404, message="game {} doesn't exist".format(game_id))

api.add_resource(GamesList,'/')
api.add_resource(GameId,'/<game_id>')

if __name__ == '__main__':
    print('starting...api')
    app.run(host='0.0.0.0')
