from flask import Flask,jsonify
from flask_restful import Resource, Api, abort, reqparse
from GameManager import *
import signal

app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('player_name', type=str, required=True)

putParser = reqparse.RequestParser()
putParser.add_argument('player', type=int, required=True)
putParser.add_argument('score', type=int, required=True)
putParser.add_argument('state', type=int, required=True)

class GamesList(Resource):
    # Returns the list of pending games
    def get(self):
        return getPendingGames()

    # Creates a new game
    def post(self):
        args = parser.parse_args()
        return jsonify(newGame(args['player_name']))

class GameId(Resource):
    # Returns the game with id game_id
    def get(self, game_id):
        game = getGame(game_id)
        if game == Error.NOT_FOUND:
            return abort(404, message="game {} doesn't exist".format(game_id))
        return game
            
            
    # Joins the game with id game_id
    def post(self, game_id):
        args = parser.parse_args()
        game = joinGame(game_id, args['player_name'])
        if game == Error.NOT_FOUND:
            return abort(404, message="game {} doesn't exist".format(game_id))
        if game == Error.NOT_WAITING:
            return abort(403, message="cannot join a game that is not in the WAITING state")
        return game

    # Updates the game with id game_id
    def put(self, game_id):
        args = putParser.parse_args()
        if args['player'] != 1 and args['player'] != 2:
            return abort(400, message="argument 'player' can only be 1 or 2")
        game = updateGame(game_id, args['player'], args['score'], args['state'])
        if game == Error.NOT_FOUND:
            return abort(404, message="game {} doesn't exist".format(game_id))
        if game == Error.NOT_PLAYING:
            return abort(403, message="cannot update a game that is not in the PLAYING state")
        return game

api.add_resource(GamesList,'/')
api.add_resource(GameId,'/<game_id>')

manager = GamesManager()

if __name__ == '__main__':
    print('starting...api')
    manager.start()
    try:
        app.run(host='0.0.0.0', threaded=True)
    finally:
        manager.stop()
