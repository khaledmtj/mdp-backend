from flask import Flask, jsonify, request
import flask
from mdp_processing import ImageProcessing
import sys

app = Flask(__name__)

@app.route('/')
def home():
	return jsonify("Hello rudy")

@app.route('/data',methods=['POST'])
def data_req():
        sys.stdout.write('data_req: 1')
        req = flask.request.form["sample"]
        sys.stdout.write('data_req: 2')
##        resp = "Server received: " + req
##        json = {"textDetected": resp}
        imProc = ImageProcessing('processor')
        text = imProc.handleRotation(req)
        sys.stdout.write('data_req: 3')

        return jsonify(text)


# @app.route('/data/<string:name>')
# def get_data(name):
# 	return jsonify(name)




if __name__ == '__main__':
    app.run(debug = True)
