/**
 * A class to evaluate a collaborative filtering algorithm
 * 
 * Michael O'Mahony
 * 20/01/2011
 */

package util.evaluator;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import alg.CFAlgorithm;

import util.RatingsPair;
import util.UserItemPair;

public class Evaluator 
{	
	private Map<UserItemPair,RatingsPair> results;
//	private Map<UserItemPair,Double> myresults;// a map to store all test data predictions
//	Integer Flag=0;
	
	/**
	 * constructor - creates a new Evaluator object
	 * @param alf - the CF algorithm
	 * @param testData - a map containing the test data
	 */
	public Evaluator(final CFAlgorithm alg, final Map<UserItemPair,Double> testData) // put the predicted value to results
	{
		results = new HashMap<UserItemPair,RatingsPair>(); // instantiate the results hash map

		for(Iterator<Map.Entry<UserItemPair,Double>> it = testData.entrySet().iterator(); it.hasNext(); ) // iterate over test data and make predictions for all user-item pairs
		{
			Map.Entry<UserItemPair,Double> entry = (Map.Entry<UserItemPair,Double>)it.next();
			UserItemPair pair = entry.getKey();
			Double actualRating = entry.getValue();
			Double predictedRating = alg.getPrediction(pair.getUserId(), pair.getItemId());
			//System.out.println("here in Evaluator file: => the predicted rating is "+predictedRating);//yurong
			results.put(pair, new RatingsPair(actualRating, predictedRating));

			//System.out.println(pair.getUserId() + " " +  pair.getItemId() + " " + actualRating + " " + predictedRating);
		}
	}
//	public Evaluator(final CFAlgorithm alg, final Map<Integer,Integer> noActualData, final Integer flag) // put the predicted value to results
//	{
//		Flag=flag;
//		myresults = new HashMap<UserItemPair,Double>(); // instantiate the results hash map
//
//		for(Iterator<Map.Entry<Integer,Integer>> it = noActualData.entrySet().iterator(); it.hasNext(); ) // iterate over test data and make predictions for all user-item pairs
//		{
//			Map.Entry<Integer,Integer> entry = (Map.Entry<Integer,Integer>)it.next();
//			Integer userId = entry.getKey();
//			Integer itemId = entry.getValue();
//			//Double actualRating = entry.getValue();
//			Double predictedRating = alg.getPrediction(userId, itemId);
//			myresults.put(new UserItemPair(userId,itemId), predictedRating);
//
//			//System.out.println(pair.getUserId() + " " +  pair.getItemId() + " " + actualRating + " " + predictedRating);
//		}
//	}
	/**
	 * @returns the coverage (as a percentage)
	 */
	public double getCoverage()
	{
		int counter = 0;
		for(RatingsPair ratings: results.values())
			if(ratings.getPredictedRating() != null)
				counter++;
		
		return (results.size() > 0) ? counter * 100.0 / results.size() : 0;
	}
	
	/**
	 * @returns the root mean square error (RMSE) or null if the actual ratings are not available
	 */
	public Double getRMSE()
	{
		int counter = 0;
		double squareError = 0;
		for(RatingsPair ratings: results.values())
		{	
			if(ratings.getActualRating() == null) // actual ratings not available, exit loop
				break;
				
			if(ratings.getPredictedRating() != null) // a predicted rating has been computed
			{
				squareError += Math.pow(ratings.getActualRating().doubleValue() - ratings.getPredictedRating().doubleValue(), 2);
				counter++;
			}
		}

		if(counter == 0)
			return null;
		else
			return new Double(Math.sqrt(squareError / counter));	
	}
	
	/**
	 * @returns the mean absolute error (MAE) or null if the actual ratings are not available
	 */
	public Double getMAE()
	{
		int counter = 0;
		double error = 0;
		for(RatingsPair ratings: results.values())
		{	
			if(ratings.getActualRating() == null) // actual ratings not available, exit loop
				break;
				
			if(ratings.getPredictedRating() != null) // a predicted rating has been computed
			{
				error += Math.abs(ratings.getActualRating().doubleValue() - ratings.getPredictedRating().doubleValue());
				counter++;
			}
		}

		if(counter == 0)
			return null;
		else
			return new Double(error / counter);	
	}
	
	/**
	 * @param filename - the path and filename of the output file
	 */
	public void writeResults(final String outputFile)
	{
		
//		if(Flag==0){
		try
        {
			int exist=0;
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile)); // open output file for writing
		
			for(Iterator<Map.Entry<UserItemPair,RatingsPair>> it = results.entrySet().iterator(); it.hasNext(); ) // iterate over all predictions
			{
				Map.Entry<UserItemPair,RatingsPair> entry = (Map.Entry<UserItemPair,RatingsPair>)it.next();
				UserItemPair pair = entry.getKey();
				RatingsPair ratings = entry.getValue();

				if(ratings.getPredictedRating() != null) // a predicted rating has been computed
				{
					pw.print(pair.toString() + " ");
					if(ratings.getActualRating() != null) // print the actual rating if available
						pw.print(ratings.getActualRating() + " ");
					pw.println(ratings.getPredictedRating());
				}
				//yurong, output the required user-item rating if match
				if (entry.getKey().getUserId()==64672 && entry.getKey().getItemId()==5347){
					exist=1;
					System.out.println("here we print the required rating of user-item: actual: "+entry.getValue().getActualRating()+"predicted: "+entry.getValue().getPredictedRating());
					}
				
				
			}
			if(exist==0){System.out.println("In evaluator.java: this user-item pair doesn't exist");}
			
			pw.close(); // close output file
        }
		catch(IOException e)
		{
			System.out.println("Error writing to output file ...\n");
			e.printStackTrace();
			System.exit(1);
		}
	//}
		//if use myresult yurong
//		else{
//			
//			try
//	        {
//				int exist=0;
//				PrintWriter pw = new PrintWriter(new FileWriter(outputFile)); // open output file for writing
//			
//				for(Iterator<Map.Entry<UserItemPair,Double>> it = myresults.entrySet().iterator(); it.hasNext(); ) // iterate over all predictions
//				{
//					Map.Entry<UserItemPair,Double> entry = (Map.Entry<UserItemPair,Double>)it.next();
//					Integer userId = entry.getKey().getUserId();
//					Integer itemId = entry.getKey().getItemId();
//					Double predictedRating=entry.getValue();
//
//					if(predictedRating != null) // a predicted rating has been computed
//					{
//						pw.print(userId +" "+itemId+" ");
//						pw.println(predictedRating);
//					}
//					//yurong, output the required user-item rating if match
//					if (entry.getKey().getUserId()==64672 && entry.getKey().getItemId()==5347){
//						exist=1;
//						System.out.println("here in myresults we print the required rating of user-item: =>"+predictedRating);
//						}
//					
//					
//				}
//				pw.close(); // close output file
//				if(exist==0){System.out.println("this user-item pair doesn't exist");System.exit(1);}
//				
//
//	        }
//			catch(IOException e)
//			{
//				System.out.println("Error writing to output file ...\n");
//				e.printStackTrace();
//				System.exit(1);
//			}
//			
//			
//			
//		}
		
		
		
	}
}
