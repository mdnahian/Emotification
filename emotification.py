from vaderSentiment import vaderSentiment

class Sentiment():

	def __init__(self, *args, **kwargs):
		self.appName = "Emotification"
		self.version = 1.0
		self.author = "Md Islam"
	
	def analyze(self, text):
		v = vaderSentiment.sentiment(text)		

		if v['neg'] < v['pos']:
			score = 1 - v['neg']
		elif v['neg'] > v['pos']:
			score = v['pos']
		else:
			score = 0.500
		
		return score
