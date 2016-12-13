from flask import Flask, request, send_from_directory
from mingus.midi import midi_file_out
from mingus.containers import NoteContainer
from mingus.containers import Note
from emotification import Sentiment
from daemon import runner


class Application:

	def __init__(self):
		self.stdin_path = '/dev/null'
        	self.stdout_path = '/dev/tty'
       		self.stderr_path = '/dev/tty'
        	self.pidfile_path =  '/tmp/foo.pid'
        	self.pidfile_timeout = 5
	
	def run(self):
		app = Flask(__name__, static_url_path='')

		BASE_URL = 'http://138.197.0.96:80'

		@app.route('/', methods=['POST', 'GET'])
		def init():
		    if request.method == 'POST':
			# score = float(request.form['score'])
			title = request.form['title']
			text = request.form['text']
			filename = request.form['filename']+'.mid'    
			
			s = Sentiment()
			score = s.analyze(text)	

			if score > .875:
			    octave = 7
			elif score > .75:
			    octave = 6
			elif score > .625:
			    octave = 5
			elif score > .50:
			    octave = 4
			elif score > .375:
			    octave = 3
			elif score > .25:
			    octave = 2 
			elif score > .125:
			    octave = 1
			else:
			    octave = 0

			octave += 1     

			url = BASE_URL+'/sounds/'+filename      

			if(octave>3):
				nc = NoteContainer([Note('C',octave,{'volume':10}),Note('E',octave,{'volume':10}),Note('G',octave,{'volume':10})])
			else:
				nc = NoteContainer([Note('C',octave,{'volume':10}),Note('Eb',octave,{'volume':10}),Note('G',octave,{'volume':10})])        

			midi_file_out.write_NoteContainer("static/sounds/"+filename, nc, 40)        
		    
			return url
		    else:
			return 'Invalid Request'


		@app.route('/<path:path>')
		def sound_file(path):
		    return url_for('static', filename=path)


		if __name__=="__main__": 
		    app.run(host='0.0.0.0', port=int(80))


application = Application()
daemon_runner = runner.DaemonRunner(application)
daemon_runner.do_action()
