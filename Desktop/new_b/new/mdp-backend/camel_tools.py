import camel_tools.tokenizers.word
import re
import sys
from camel_tools.disambig.mle import MLEDisambiguator

from camel_tools.tokenizers.word import simple_word_tokenize
from camel_tools.disambig.mle import MLEDisambiguator

# instantiate the Maximum Likelihood Disambiguator
mle = MLEDisambiguator.pretrained()

# The disambiguator expects pre-tokenized text
sentence = simple_word_tokenize('نجح بايدن في الااتخاب')

disambig = mle.disambiguate(sentence)

diacritized = [d.analyses[0].analysis['diac'] for d in disambig]
pos_tags = [d.analyses[0].analysis['pos'] for d in disambig]
lemmas = [d.analyses[0].analysis['lex'] for d in disambig]

# Print the combined feature values extracted above
for triplet in zip(diacritized, pos_tags, lemmas):
    print(triplet)
    sys.stdout.write(triplet)
