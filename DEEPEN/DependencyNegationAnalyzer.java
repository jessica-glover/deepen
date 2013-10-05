

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import edu.stanford.nlp.trees.TypedDependency;

public class  DependencyNegationAnalyzer{

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
				inputString = "pancreatitis" +"\t" + "the pateint does not have pancreatitis or hpb malignancy.";

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
		Boolean isConceptPresentInPrepOther = false; 
		try {			
			CallKit ck = new CallKit();
			String result = ck.CallKitMethod(inputString);
			String negationToken = "";

			if(result.equals("Negated"))
			{
				negationToken = ck.getNegationToken();
				String[] negationTokens = negationToken.split(" ");

				List<TypedDependency> sdpOutput = StanfordDependencyParser.Parser(inputString.split("\t")[1]); 

				String[][] sdpForSentence = new String[sdpOutput.size()][3];
				String sdpTemp = "";

				String[] dependencyParts = null;
				boolean isPrepWithout = false;
				boolean isPrepOther = false;

				for (int sdpOutputIndex=0; sdpOutputIndex < sdpOutput.size(); sdpOutputIndex++)
				{
					System.out.println(sdpOutput.get(sdpOutputIndex));
					sdpTemp = sdpOutput.get(sdpOutputIndex).toString().replaceAll("," ," ");
					sdpTemp = sdpTemp.replaceAll("\\(", " ");
					sdpTemp = sdpTemp.replaceAll("\\)", " ");
					sdpTemp = sdpTemp.replaceAll("  ", " ");
					dependencyParts = sdpTemp.split(" ");
					sdpForSentence[sdpOutputIndex] = dependencyParts;

					if(sdpTemp.split(" ")[0].equals("prep_without"))
					{
						isPrepWithout = true;
					}

					if(sdpTemp.split(" ")[0].equals("prep_in") || 
							sdpTemp.split(" ")[0].equals("prep_with") || 
							sdpTemp.split(" ")[0].equals("prep_within"))
					{
						isPrepOther = true;
					}					
				}

				boolean conceptPresent = false;
				String productionChain = "";
				String productionChainToFile = "";

				if(isPrepWithout)
				{
					StanfordDependencyParser.GetProductionChainForPrepWithout(sdpForSentence);
					isPrepOther = false;
				}
				else
					if(isPrepOther)
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
				
				if(isPrepWithout || !isConceptPresentInPrepOther)
				{
					isConceptPresentInPrepOther = false;
					List<String> productionChainCollection =  StanfordDependencyParser.getProductionChainCollection();

					for(int productionChainCollectionIndex = 0; 
							productionChainCollectionIndex < productionChainCollection.size(); 
							productionChainCollectionIndex++)
					{
						productionChain = productionChainCollection.get(productionChainCollectionIndex);
						productionChainToFile += productionChain + " "; 
						System.out.println("Prod Chain : " + productionChain);

						if(IsConceptPresent(productionChain, conceptTerm))
						{
							conceptPresent = true;
							break;
						}
					}	

					if(conceptPresent)
					{
//						outputString = inputString + "\t" + negationToken + "\t" + productionChainToFile + "\t" + "Negation Confirmed by SDP";					
						outputString = "Negation Confirmed by SDP";
					}
					else
					{
//						outputString = inputString + "\t" + negationToken + "\t" + productionChainToFile + "\t" + "Affirmed by SDP";
						outputString = "Affirmed by SDP";
					}

					productionChain = "";
				}
				else 
					if(isConceptPresentInPrepOther)
					{
//						outputString = inputString + "\t" + negationToken + "\t" + "PREPOSITION FOUND" + "\t" + "Affirmed by " + sdpTemp.split(" ")[0];
						outputString = "Affirmed by SDP_Prep"; //+ sdpTemp.split(" ")[0];
					}
					else
					{
//						outputString = inputString + "\t" + negationToken + "\t" + "PREPOSITION NOT FOUND" + "\t" + "Negation Confirmed by " + sdpTemp.split(" ")[0];
						outputString = "Negation Confirmed by SDP_Prep";// + sdpTemp.split(" ")[0];
					}
			}
			else
			{
//				outputString = inputString + "\t" + "" + "\t" + "" + "\t" + "Affirmed";
				outputString = "Affirmed";
			}
		} catch (Exception e) {
			e.printStackTrace();
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