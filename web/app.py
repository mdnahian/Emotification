from flask import Flask, request, send_from_directory
from mingus.midi import midi_file_out
from mingus.containers import NoteContainer
from mingus.containers import Note

app = Flask(__name__, static_url_path='')

BASE_URL = 'http://54.163.25.89:80'

@app.route('/', methods=['POST', 'GET'])
def init():
    if request.method == 'POST':
        score = float(request.form['score'])
        text = request.form['text']
        filename = request.form['filename']+'.mid'
        
        print str(score)+" - "+text
    
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
        
        nc = NoteContainer([Note('C',octave),Note('E',octave),Note('G',octave)])
        midi_file_out.write_NoteContainer("static/sounds/"+filename, nc, 40)        
    
        return url
    else:
        return 'Invalid Request'

@app.route('/<path:path>')
def sound_file(path):
    return url_for('static', filename=path)


if __name__=="__main__":
    app.run(host='0.0.0.0', port=int(80))



