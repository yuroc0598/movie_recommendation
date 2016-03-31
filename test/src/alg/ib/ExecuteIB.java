/**
 * Used to evaluate the user-based CF algorithm
 * 
 * Michael O'Mahony
 * 20/01/2011
 */

package alg.ib;
import java.io.*;
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;

import alg.ib.neighbourhood.*;
import alg.ib.predictor.*;
import similarity.metric.*;
import util.evaluator.Evaluator;
import util.reader.DatasetReader;

public class ExecuteIB
{
	
	public static void usage(){
		System.out.print("Usage:\n"+
				"Please attach the right arguments:\n"+
				"the first argument is user ID\n"+
				"the second argument is item ID\n"+
				"the third argument is number of neighbors\n"
				);
		
		
		
		
		
	}
	public static void main(String[] args)
	{
		
		
		if(args.length!=3){usage();System.exit(1);}
		//yurong modified
		int myUserId=Integer.valueOf(args[0]);
		int myItemId=Integer.valueOf(args[1]);
		int neighbor=Integer.valueOf(args[2]);
		System.out.println("now we are writing user ID and item ID to testing files=>");
		
		try{
		File myfile =new File("r.test");
		
		//if file doesnt exists, then create it
		if(!myfile.exists()){
			myfile.createNewFile();
			System.out.println("now we are creating the testing files=>");
		}
		
		//true = append file
		FileWriter fileWritter = new FileWriter(myfile.getName(),true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(myUserId+" "+myItemId);
	        bufferWritter.newLine();
	        bufferWritter.close();
	    
        System.out.println("Writeing process: Done");
        }
		catch (IOException e) {
			System.out.println("error with writing requested userId and itemId to file!");System.exit(1);
		}
		
		
		
		
		System.out.println("now we are using iterm based recommendation=>");
		
		// configure the item-based CF algorithm - set the predictor, neighbourhood and similarity metric ...
		PredictorItem predictor = new ResnickItem();// to choose resnickitem or meanvalue predictor
		NeighbourhoodItem neighbourhood = new NearestNeighbourhoodItem(neighbor);// number of neighbors can be tuned here yurong 
		SimilarityMetric metric = new Cosine(3); // not sure about the meaning of 3
		//SimilarityMetric metric = new Pearson(80);
		
		// set the paths and filenames of the item file, train file and test file ...
		String itemFile = "FRT dataset" + File.separator + "r.item";
		String trainFile = "FRT dataset" + File.separator + "r.train";
		String testFile = "FRT dataset" + File.separator + "r.test";
		//String noActualRatingFile="FRT dataset" + File.separator + "r.test";
		
		// set the path and filename of the output file ...
		String outputFile = "results" + File.separator + "predictions.txt";
		
		////////////////////////////////////////////////
		// Evaluates the CF algorithm (do not change!!):
		// - the RMSE (if actual ratings are available) and coverage are output to screen
		// - the output file is created
		DatasetReader reader = new DatasetReader(itemFile, trainFile, testFile);
		ItemBasedCF ibcf = new ItemBasedCF(predictor, neighbourhood, metric, reader);
		//Evaluator eval = new Evaluator(ibcf, reader.getTestData(),1);
		Evaluator eval = new Evaluator(ibcf, reader.getTestData());
		//yurong out put the required rating of user i to item j according to args
		
		eval.writeResults(outputFile);
		Double RMSE = eval.getRMSE();
		//yurong modified the following two lines
		if(RMSE==null){ System.out.println("I don't know why RMSE is null neither");}
		else {System.out.println("RMSE: " + RMSE);}
		//if(RMSE != null) System.out.println("RMSE: " + RMSE); //yurong modified, uncomment this line to restore
		double coverage = eval.getCoverage();
		System.out.println("coverage: " + coverage + "%");
	}
}
	