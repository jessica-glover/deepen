DEpEndency ParsEr Negation (DEEPEN) is a negation identification algorithm that uses dependency parser in conjunction with [NegEx](https://code.google.com/p/negex/) algorithm.
DEEPEN uses a chain of nested dependency relations between a clinical condition and negation terms found by [NegEx](https://code.google.com/p/negex/) algorithm.

Dependencies:

[General NegEx Java Implementation v.2.0](https://code.google.com/p/negex/downloads/detail?name=GeneralNegEx.Java.v.2.0_10272010.zip&can=2&q=)

[Stanford Dependency Parser v2.0.1](http://nlp.stanford.edu/software/lex-parser.shtml#Download)

Input:
A sentence with indicated clinical condition (concept) from the sentence.

Output:
String that identifies the negation status of the concept in the sentence,
the code only double checks the concepts that are considered negated by the NegEx algorithm so there are 3 outputs
Affirmed: meaning the concept is not negated as found by NegEx
Affirmed confirmed by SDP: NegEx found the concept as negated but DEEPEN considers it affirmed
Negation confirmed by SDP: NegEx found the concept as negated and DEEPEN  confirms that.

If you use DEEPEN for academic research, you are highly encouraged (though not required) to cite the following paper:

Mehrabi S, Schmidt CM, Waters JA, Beesley C, Krishnan A, Kesterson J, Dexter P, Al-Haddad MA, Tierney WM, Palakal M
[An efficient pancreatic cyst identification methodology using natural language processing](http://www.ncbi.nlm.nih.gov/pubmed/23920672). Stud Health Technol Inform. 2013;192:822-6.