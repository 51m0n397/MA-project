from flask import Flask, jsonify
from flask_httpauth import HTTPTokenAuth
from flask_restful import Resource, Api, abort, reqparse
from GameManager import *
import signal
from google.oauth2 import id_token
from google.auth.transport import requests

CLIENT_ID = "410429140054-121fs1u1a15ijptjap8dfla1lh0gg4vs.apps.googleusercontent.com"

app = Flask(__name__)
api = Api(app)
auth = HTTPTokenAuth(scheme='Bearer')

@auth.verify_token
def verify_token(token):
    try:
        user = id_token.verify_oauth2_token(token, requests.Request(), CLIENT_ID)
        return user
    except ValueError:
        pass

@auth.error_handler
def auth_error(status):
    return abort(status, message="Token not valid")


putParser = reqparse.RequestParser()
putParser.add_argument('score', type=int, required=True)
putParser.add_argument('state', type=int, required=True)

class GamesList(Resource):
    # Returns the list of pending games
    @auth.login_required
    def get(self):
        return getPendingGames(auth.current_user())

    # Creates a new game
    @auth.login_required
    def post(self):
        return jsonify(newGame(auth.current_user()))

class GameId(Resource):
    # Returns the game with id game_id
    @auth.login_required
    def get(self, game_id):
        game = getGame(game_id, auth.current_user())
        if game == Error.NOT_FOUND:
            return abort(404, message="game {} doesn't exist".format(game_id))
        if game == Error.NOT_AUTHORIZED:
            return abort(403, message="cannot access this game")
        return game
            
            
    # Joins the game with id game_id
    @auth.login_required
    def post(self, game_id):
        game = joinGame(game_id, auth.current_user())
        if game == Error.NOT_FOUND:
            return abort(404, message="game {} doesn't exist".format(game_id))
        if game == Error.NOT_WAITING:
            return abort(403, message="cannot join a game that is not in the WAITING state")
        if game == Error.NOT_AUTHORIZED:
            return abort(403, message="cannot join a game you are hosting")
        return game

    # Updates the game with id game_id
    @auth.login_required
    def put(self, game_id):
        args = putParser.parse_args()
        game = updateGame(game_id, auth.current_user(), args['score'], args['state'])
        if game == Error.NOT_FOUND:
            return abort(404, message="game {} doesn't exist".format(game_id))
        if game == Error.NOT_PLAYING:
            return abort(403, message="cannot update a game that is not in the PLAYING state")
        if game == Error.NOT_AUTHORIZED:
            return abort(403, message="cannot access this game")
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
