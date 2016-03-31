/**
 * An class to compute the target items's predicted rating for the target user (item-based CF) using Resnick Algorightm.
 * 
 * Derek Organ
 * 08/02/2012
 */

package alg.ib.predictor;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import java.util.Collection;
import alg.ib.neighbourhood.NeighbourhoodItem;
import similarity.SimilarityMap;
import profile.Profile;


public class ResnickItem implements PredictorItem {

	/**
	 * constructor - creates a new SimpleAveragePredictor object
	 */
	public ResnickItem()
	{
		
	}
	
	public Double getPrediction(final Integer itemId, final Integer userId, final Map<Integer,Profile> itemProfileMap, final Map<Integer,Profile> userProfileMap, final NeighbourhoodItem neighbourhood, final SimilarityMap simMap)
	{
		System.out.println("here in ResnickItem getPrediction file: => we are here");
		double above = 0;
		double below = 0;
		
		ArrayList<Integer> neighbours = neighbourhood.getNeighbours(itemId, userId, userProfileMap, simMap); // get the neighbours
		
		// Get the mean rating for the item we are trying to get a prediction for
		Profile i = itemProfileMap.get(itemId);// i is profile type with id and Map<Integer,Double> (<userID, rating>)
		double i_mean = i.getMeanValue();// i_mean is the mean value of the ratings if item i (for all existing ratings if i of all users)
		
		//System.out.println("here in resnickitem print the iterm id=> "+itemId);//yurong 
		//i.getwhole();//yurong
		
		for(int j = 0; j < neighbours.size(); j++) // iterate over each neighbour
		{
			Double n_rating = itemProfileMap.get(neighbours.get(j)).getValue(userId); // get the neighbour's rating for the target user
			if(n_rating == null)
			{
				System.out.println("Error - rating is null!"); // this error should never occur since all neighbours by definition have rated the target item!
				System.exit(1);
			}
			double n_mean = itemProfileMap.get(neighbours.get(j)).getMeanValue(); // get the mean value of this neighbour
			double n_diff = n_rating - n_mean;
			
			double sim = simMap.getSimilarity(itemId,neighbours.get(j));
			
			above += n_diff * sim;
			below += Math.abs(sim); 
		}
		
		if(neighbours.size() > 11){
//			System.out.println("here in Resnickitem, the i_mean value is "+i_mean
//					+" ,,the above value is "+above+" ,,the below value is "+below);//yurong
			return new Double(i_mean + (above / below)>5.0?5.0:i_mean + (above / below));}
		else
			return null;
	}
	
}
