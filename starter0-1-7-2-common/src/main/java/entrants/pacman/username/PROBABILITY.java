package entrants.pacman.username;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


/*Object containing probabilities for a set of strategies.
 * Sum of all probabilities should be 1 (100%).*/
public class PROBABILITY implements Serializable{
	PROBABILITY(ArrayList<Strategy> strategyList) {
		int numberStrategies = strategyList.size();
		probabilites = new double[numberStrategies];
		double[] temp = new double[numberStrategies];

		// Generate n random probabilities
		double sum = 0;
		for (int i = 0; i < numberStrategies; i++) {
			double initProbability = strategyList.get(i).getStrategyInitialProbability(); //get initial probability of strategy
			temp[i] = (initProbability == Strategy.initialProbability.RANDOM) ? new Random().nextDouble() : initProbability;
			sum += temp[i];
		}
		
		for (int i = 0; i < numberStrategies; i++) {
			probabilites[i] = temp[i] / sum; //normalize probabilities
		}
	}
	/*@brief Returns the probability of a specific strategy
	   * @param numberOfStrategy the index of the strategy which probability should be returned
	   * @returns the probability of the strategy with index numberOfStrategy
	   * */
	public final double getProbability(int numberOfStrategy) {
		return probabilites[numberOfStrategy];
	}
	
	/*@brief Sets the probability of a specific strategy
	   * @param numberOfStrategy the index of the strategy which probability should be returned
	   * @param newProbability the new Proabbility that should be set for the strategy with index numberOfStrategy
	   * */
	public void setProbability(int numberOfStrategy, double newProbability) {
		newProbability = newProbability < 0 ? 0 : newProbability;
		probabilites[numberOfStrategy] = newProbability;
	}
	/*@brief Normalizes all probabilities of this object to ensure that the sum of all probabilities is always 1 (100%)
	   * */
	public void normalizeProbability()
	{
		//get sum of all probabilities
		double probabilitySum = 0;
		for (double probability : probabilites) {
			probabilitySum += probability;
		}
		
		//normalize probabilities
		for (int i = 0;i<probabilites.length;i++) {
			probabilites[i] /= probabilitySum;
		}
	}
	/*@brief Returns the number of probabilities which is also the number of strategies used
	   * @returns The number of probabilities which is also the number of strategies used
	   * */
	public int getNumberOfProbabilities()
	{
		return probabilites.length;
	}

	private double[] probabilites;
}
