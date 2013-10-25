package edu.iupui.dependencyNegation;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import edu.iupui.negEx.CallKit;
import edu.iupui.stanfordDependencyParser.StanfordDependencyParser;
import edu.stanford.nlp.trees.TypedDependency;

public class  DependencyNegationAnalyzer{
	private static boolean isPrepWithout = false;
	private static boolean isPrepOther = false;
	private static boolean isConjunction = false;
	private static boolean isNSubject = false;
	private static boolean isSuggest = false;

	public static boolean isPrepWithout() {
		return isPrepWithout;
	}


	public static boolean isNSubject() {
		return isNSubject;
	}


	public static void setNSubject(boolean isNSubject) {
		DependencyNegationAnalyzer.isNSubject = isNSubject;
	}


	public static boolean isSuggest() {
		return isSuggest;
	}


	public static void setSuggest(boolean isSuggest) {
		DependencyNegationAnalyzer.isSuggest = isSuggest;
	}


	public static void setPrepWithout(boolean isWithout) {
		isPrepWithout = isWithout;
	}


	public static boolean isPrepOther() {
		return isPrepOther;
	}


	public static void setPrepOther(boolean isOther) {
		isPrepOther = isOther;
	}


	public static boolean isConjunction() {
		return isConjunction;
	}


	public static void setConjunction(boolean isConj) {
		isConjunction = isConj;
	}


	public static void setNominalSubject(boolean isNSubj) {
		isNSubject = isNSubj;
	}

	public static boolean isNominalSubject() {
		return isNSubject;
	}

	/**
	 * Main invoking method.
	 * @param args
	 */
	public static void main(String[] args)
	{
//		String fileName = "~/test.txt";

		//		PrintWriter fileWriter = null;
		try {
			//			fileWriter = new PrintWriter("C:/Documents and Settings/anand/Desktop/Work_Stuff/Regenstrief_Work/Analysis/Cyst_Identification_Work/Validation_Output.txt");
//			BufferedReader fileReader = new BufferedReader(new FileReader(fileName));			
			String inputString = "";
			String outputString = "";
//			while(null != (inputString = fileReader.readLine()))
			{
				inputString = "pseudocyst" + "\t" + "There is no significant interval increase in size of pancreatic pseudocyst compared to prior exam.";

//				inputString = "pseudocyst" +"\t" + "No organized fluid collections, abscess formation, or significant pseudocyst formation is present.";
				//				fileWriter.write(inputString);
				outputString = AnalyzeDependencyNegation(inputString);
				
				//				fileWriter.write(outputString);
				//				fileWriter.write("\n");	
			}
			System.out.println(outputString);
//			fileReader.close();
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String AnalyzeDependencyNegation(String inputString) 
	{		
		String outputString = "";
		String conceptTerm = inputString.split("\t")[0];		
		String sentence = inputString.split("\t")[1];

		try {
			CallKit ck = new CallKit();
			String result = ck.CallKitMethod(inputString);
			String negationToken = "";

			if(result.equals("Negated"))
			{
				negationToken = ck.getNegationToken();
				String[] negationTokens = negationToken.split(" ");
				List<TypedDependency> sdpOutput = StanfordDependencyParser.Parser(sentence);
				String[][] sdpForSentence = new String[sdpOutput.size()][3];
				String sdpTemp = "";
				String[] dependencyParts = null;

				setPrepWithout(false);
				setPrepOther(false);				
				setConjunction(false);
				setSuggest(false);

				for (int sdpOutputIndex=0; sdpOutputIndex < sdpOutput.size(); sdpOutputIndex++)
				{	
					System.out.println(sdpTemp);
					sdpTemp = sdpOutput.get(sdpOutputIndex).toString().replaceAll("," ," ");
					sdpTemp = sdpTemp.replaceAll("\\(", " ");
					sdpTemp = sdpTemp.replaceAll("\\)", " ");
					sdpTemp = sdpTemp.replaceAll("  ", " ");
					dependencyParts = sdpTemp.split(" ");
					sdpForSentence[sdpOutputIndex] = dependencyParts;

					if(sdpTemp.split(" ")[0].equals("prep_without"))
					{
						setPrepWithout(true);
					}

					if(sdpTemp.split(" ")[0].equals("prep_in") || 
							sdpTemp.split(" ")[0].equals("prep_with") || 
							sdpTemp.split(" ")[0].equals("prep_within"))
					{
						setPrepOther(true);						
					}

					if(sdpTemp.split(" ")[0].equals("conj_and"))
					{
						setConjunction(true);
					}

					if(sdpTemp.split(" ")[0].equals("nsubj"))
					{
						setNominalSubject(true);
					}

					if(sdpTemp.split(" ")[1].split("-")[0].equals("suggest") || 
							sdpTemp.split(" ")[2].split("-")[0].equals("suggest") ||
							sdpTemp.split(" ")[1].split("-")[0].equals("suggests") || 
							sdpTemp.split(" ")[2].split("-")[0].equals("suggests"))
					{
						setSuggest(true);
					}		
				}

				if(isConjunction())
				{
					String[] sentenceParts = sentence.split("and");

					for(int sentencePartIndex = 0; sentencePartIndex < sentenceParts.length; sentencePartIndex++)
					{
						result = ck.CallKitMethod(conceptTerm + "\t" + sentenceParts[sentencePartIndex]);

						if(result.equals("Negated"))
						{
							negationToken = ck.getNegationToken();
							negationTokens = negationToken.split(" ");
							System.out.println("sentenceParts :" + sentenceParts[sentencePartIndex]);
							sdpOutput = StanfordDependencyParser.Parser(sentenceParts[sentencePartIndex]);
							sdpForSentence = new String[sdpOutput.size()][3];

							setPrepWithout(false);
							setPrepOther(false);							
							setNominalSubject(false);
							setSuggest(false);

							for (int sdpOutputIndex=0; sdpOutputIndex < sdpOutput.size(); sdpOutputIndex++)
							{
								sdpTemp = sdpOutput.get(sdpOutputIndex).toString().replaceAll("," ," ");
								sdpTemp = sdpTemp.replaceAll("\\(", " ");
								sdpTemp = sdpTemp.replaceAll("\\)", " ");
								sdpTemp = sdpTemp.replaceAll("  ", " ");
								dependencyParts = sdpTemp.split(" ");
								sdpForSentence[sdpOutputIndex] = dependencyParts;

								if(sdpTemp.split(" ")[0].equals("prep_without"))
								{
									setPrepWithout(true);
								}

								if(sdpTemp.split(" ")[0].equals("prep_in") || 
										sdpTemp.split(" ")[0].equals("prep_with") || 
										sdpTemp.split(" ")[0].equals("prep_within"))
								{
									setPrepOther(true);						
								}

								if(sdpTemp.split(" ")[0].equals("nsubj"))
								{
									setNominalSubject(true);
								}		

								if(sdpTemp.split(" ")[1].equals("suggests") || 
										sdpTemp.split(" ")[2].equals("suggests") ||
										sdpTemp.split(" ")[1].equals("suggests") || 
										sdpTemp.split(" ")[2].equals("suggests"))
								{
									setSuggest(true);
								}								
							}

							outputString = ProcessSentence(sdpForSentence, conceptTerm, negationTokens);

							if(0 == outputString.compareTo("Negation Confirmed by SDP") || 
									0 == outputString.compareTo("Negation"))
							{
								break;
							}
						}
						else
						{
							outputString = "Affirmed";
//							outputString = "Affirmed";
						}
					}					
				}
				else
				{
					outputString = ProcessSentence(sdpForSentence, conceptTerm, negationTokens);
				}
			}
			else
			{
				outputString = "Affirmed" ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputString;
					}
	private static String ProcessSentence(String[][] sdpForSentence, String conceptTerm, String[] negationTokens)
	{		
		Boolean isConceptPresentInPrepOther = false; 
		boolean conceptPresent = false;
		String productionChain = "";
		String outputString = "";

		if(isPrepWithout())
		{
			StanfordDependencyParser.GetProductionChainForPrepWithout(sdpForSentence);
			setPrepOther(false);
		}
		else
			if(isPrepOther())
			{
				isConceptPresentInPrepOther = StanfordDependencyParser.GetProductionChainForPrepOther(sdpForSentence, conceptTerm);						

				if(!isConceptPresentInPrepOther)
				{
					StanfordDependencyParser.GenerateProductionChain(sdpForSentence, negationTokens);
				}
			}
			else
			{
				StanfordDependencyParser.GenerateProductionChain(sdpForSentence, negationTokens);
			}

		if(isNominalSubject())
		{
			StanfordDependencyParser.GenerateProductionChainForNSubj(sdpForSentence);
		}

		if(isSuggest())
		{
			StanfordDependencyParser.GenerateProductionChainForSuggest(sdpForSentence, negationTokens);
		}

		if(isPrepWithout() || !isConceptPresentInPrepOther)
		{
			isConceptPresentInPrepOther = false;
			List<String> productionChainCollection =  StanfordDependencyParser.getProductionChainCollection();

			for(int productionChainCollectionIndex = 0; 
					productionChainCollectionIndex < productionChainCollection.size(); 
					productionChainCollectionIndex++)
			{
				productionChain = productionChainCollection.get(productionChainCollectionIndex);
				System.out.println("Prod Chain : " + productionChain);

				if(IsConceptPresent(productionChain, conceptTerm))
				{
					conceptPresent = true;
					break;
				}
			}	

			if(conceptPresent)
			{
				outputString = "Negation Confirmed by SDP";
			}
			else
			{
				outputString = "Affirmed by SDP";
			}

			productionChain = "";
		}
		else 
			if(isConceptPresentInPrepOther)
			{
				outputString = "Affirmed by SDP";
			}
			else
			{
				outputString = "Negation Confirmed by SDP";
			}

		return outputString;
	}

	/*
	 * Method to check if the Concept is present in the Production Chain.
	 */
	
	private static boolean IsConceptPresent(String productionChain, String conceptTerm) {

		String[] conceptTermParts = conceptTerm.split(" ");
		boolean conceptFound = false;
		String productionChainParts = productionChain.replaceAll("[\\)\\(]", " ");
		productionChainParts = productionChainParts.replaceAll("  ", " ");
		String[] allLevelParts = productionChainParts.trim().split(" ");
		String conceptTermPart = "";

		for(int conceptTermPartsIndex = 0; conceptTermPartsIndex < conceptTermParts.length; conceptTermPartsIndex++)
		{
			conceptTermPart = conceptTermParts[conceptTermPartsIndex];
			for(int allLevelPartsIndex = 0; allLevelPartsIndex < allLevelParts.length; allLevelPartsIndex++)
			{	  
				if(allLevelParts[allLevelPartsIndex].trim().equals(conceptTermPart))
				{
					conceptFound = true;
					break;
				}
			}

			if(conceptFound)
			{
				break;
			}
		}

		return conceptFound;
	}	
}