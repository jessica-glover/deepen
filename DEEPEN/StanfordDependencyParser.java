package edu.iupui.stanfordDependencyParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.StringReader;
import java.lang.invoke.ConstantCallSite;

import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;  
import edu.stanford.nlp.ling.HasWord;  
import edu.stanford.nlp.ling.Sentence;  
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class StanfordDependencyParser {

	//	private static String ProductionChain = "";

	private static List<String> ProductionChainCollection = new ArrayList<String>();

	public static List<String> getProductionChainCollection() {
		return ProductionChainCollection;
	}

	public static void setProductionChain(String productionChain) {
		ProductionChainCollection.add(productionChain);
		//		ProductionChain += productionChain + "DELIMITERTEXT";
	}

	private static String[][] sdpForSentence = {
		{"det", "SURVEY-3", "THE-1"},
		{"nn", "SURVEY-3", "PANCREATIC-2"},
		{"nsubj", "DEMONSTRATES-4", "SURVEY-3"},
		{"root", "ROOT-0", "DEMONSTRATES-4"},
		{"det", "EVIDENCE-6", "NO-5"},
		{"det", "SURVEY-6", "NO-5"},
		{"dobj", "DEMONSTRATES-4", "EVIDENCE-6"},
		{"nn", "CHANGES-11", "PSEUDOCYSTIC-8"},
		{"nn", "CHANGES-11", "OR-9"},
		{"nn", "CHANGES-11", "INFLAMMATORY-10"},
		{"prep_for", "EVIDENCE-6", "CHANGES-11"},
		{"prep_for", "EVIDENCE-6", "CHANGES1-11"},
		{"prep_for", "EVIDENCE-6", "CHANGES2-11"}
	};


	public static String[][] getSdpForSentence() {
		return sdpForSentence;
	}

	public static void setSdpForSentence(String[][] sdpForSentence) {
		StanfordDependencyParser.sdpForSentence = sdpForSentence;
	}

	public static List<TypedDependency> Parser(String sentence) {
		// This option shows parsing a list of correctly tokenized words
		String[] sent = sentence.split(" ");
		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

		List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
		Tree parse = lp.apply(rawWords);
		//		parse.pennPrint();
		//		System.out.println();

		// This option shows loading and using an explicit tokenizer
		//    String sent2 = "This is another sentence.";
		String sent2 = sentence;
		TokenizerFactory<CoreLabel> tokenizerFactory = 
				PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords2 = 
				tokenizerFactory.getTokenizer(new StringReader(sent2)).tokenize();
		parse = lp.apply(rawWords2);

		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		//		System.out.println(tdl);
		//		System.out.println();

		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		//		tp.printTree(parse);

		return tdl;
	}

	public static void demoDP(LexicalizedParser lp, String filename) {
		// This option shows loading and sentence-segment and tokenizing
		// a file using DocumentPreprocessor
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// You could also create a tokenier here (as below) and pass it
		// to DocumentPreprocessor
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree parse = lp.apply(sentence);
			parse.pennPrint();
			System.out.println();

			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			Collection tdl = gs.typedDependenciesCCprocessed(true);
			System.out.println(tdl);
			System.out.println();
		}
	}

	public static void GenerateProductionChain(String[][] sdpForSentence1,
			String[] negationTokens) {

		sdpForSentence = sdpForSentence1;
		ResetProductionChainCollection();
		String startProductionChain = "";
		String productionChain = "";
		String tempProductionChain = "";
		startProductionChain = "(" + DisplayTokens(negationTokens) + ")";

		// For all the Negation terms found in the Sentence, construct a production chain.
		for(int negationTokenIndex = 0;negationTokenIndex < negationTokens.length; negationTokenIndex++)
		{
			//			System.out.println("Current Negation Token : " + negationTokens[negationTokenIndex]);

			// Fetch the Tokens from the First Level.
			String[] firstLevelTokens = GetFirstLevelTokens(negationTokens[negationTokenIndex]);			

			productionChain = startProductionChain + " (" + DisplayTokens(firstLevelTokens) + ") ";

			for(int firstLevelTokensIndex = 0;firstLevelTokensIndex < firstLevelTokens.length; firstLevelTokensIndex++)
			{	
				//				System.out.println("Current First Level Token : " + firstLevelTokens[firstLevelTokensIndex]);

				// Fetch the Tokens from the Second Level.
				String[] secondLevelTokens = GetSecondLevelTokens(firstLevelTokens[firstLevelTokensIndex]);
				productionChain += "(" + DisplayTokens(secondLevelTokens) + ") ";
				tempProductionChain = productionChain;
				for(int secondLevelTokensIndex = 0;secondLevelTokensIndex < secondLevelTokens.length; secondLevelTokensIndex++)
				{
					//					System.out.println("Current Second Level Token : " + secondLevelTokens[secondLevelTokensIndex]);

					// Fetch the Tokens from the Third Level.
					String[] thirdLevelTokens = GetThirdLevelTokens(secondLevelTokens[secondLevelTokensIndex]);					

					if("" != thirdLevelTokens[0])
					{
						productionChain = tempProductionChain + "(" + DisplayTokens(thirdLevelTokens) + ") ";
						setProductionChain(productionChain);
					}
					else
					{
						productionChain = tempProductionChain;
						setProductionChain(productionChain);
					}
				}				
			}
		}
	}

	private static void ResetProductionChainCollection() {
		ProductionChainCollection = new ArrayList<String>();		
	}

	private static String DisplayTokens(String[] tokens) {

		String tempToken = "";

		for(int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++)
		{
			//			System.out.println("Token : " + tokens[tokenIndex]);
			tempToken += tokens[tokenIndex] + " ";
		}

		//		System.out.println(" ");

		return tempToken.trim();
	}

	public static String[] GetFirstLevelTokens(String negationToken) {

		String firstLevelTokens = "";
		String productionChainElement;
		for(int sdpForSentenceIndex = 0; sdpForSentenceIndex < sdpForSentence.length; sdpForSentenceIndex++)
		{
			productionChainElement = sdpForSentence[sdpForSentenceIndex][2].split("-")[0];
			if(negationToken.equalsIgnoreCase(productionChainElement))
			{				
				firstLevelTokens += sdpForSentence[sdpForSentenceIndex][1].split("-")[0] + " ";
				RemoveChainItem(sdpForSentenceIndex);
				//				DisplaySDPForSentence();
				sdpForSentenceIndex = 0;
			}
		}

		return firstLevelTokens.split(" ");
	}

	public static String[] GetSecondLevelTokens(String firstLevelToken) {

		String secondLevelTokens = "";
		String productionChainElement;
		for(int sdpForSentenceIndex = 0; sdpForSentenceIndex < sdpForSentence.length; sdpForSentenceIndex++)
		{
			productionChainElement = sdpForSentence[sdpForSentenceIndex][1].split("-")[0];
			if(firstLevelToken.equalsIgnoreCase(productionChainElement))
			{				
				secondLevelTokens += sdpForSentence[sdpForSentenceIndex][2].split("-")[0] + " ";
				RemoveChainItem(sdpForSentenceIndex);
				//				DisplaySDPForSentence();
				sdpForSentenceIndex = 0;
			}
		}

		return secondLevelTokens.split(" ");
	}

	public static String[] GetThirdLevelTokens(String secondLevelToken) {

		String thirdLevelTokens = "";
		String productionChainElement;
		for(int sdpForSentenceIndex = 0; sdpForSentenceIndex < sdpForSentence.length; sdpForSentenceIndex++)
		{
			productionChainElement = sdpForSentence[sdpForSentenceIndex][1].split("-")[0];
			if(secondLevelToken.equalsIgnoreCase(productionChainElement))
			{				
				thirdLevelTokens += sdpForSentence[sdpForSentenceIndex][2].split("-")[0] + " ";
				RemoveChainItem(sdpForSentenceIndex);
				//				DisplaySDPForSentence();
				sdpForSentenceIndex = 0;
			}
		}

		return thirdLevelTokens.split(" ");
	}

	private static void RemoveChainItem(int sdpForSentenceIndex) {

		String[][] tempSdpForSentence = new String[sdpForSentence.length - 1][3];

		for(int tempSdpForSentenceIndex = 0, index = 0; tempSdpForSentenceIndex < sdpForSentence.length; tempSdpForSentenceIndex++ )
		{
			if(sdpForSentenceIndex != tempSdpForSentenceIndex)
			{
				tempSdpForSentence[index][0] = sdpForSentence[tempSdpForSentenceIndex][0];
				tempSdpForSentence[index][1] = sdpForSentence[tempSdpForSentenceIndex][1];
				tempSdpForSentence[index++][2] = sdpForSentence[tempSdpForSentenceIndex][2];				
			}
		}

		sdpForSentence = tempSdpForSentence;		
	}

	private static void DisplaySDPForSentence() {
		for (int sdpOutputIndex=0; sdpOutputIndex < sdpForSentence.length; sdpOutputIndex++)
		{
			System.out.println(sdpForSentence[sdpOutputIndex][0] + " " + sdpForSentence[sdpOutputIndex][1] + " " +sdpForSentence[sdpOutputIndex][2]);
		}	

		System.out.println(" ");
	}
}
