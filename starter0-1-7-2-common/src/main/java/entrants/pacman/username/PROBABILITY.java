package entrants.pacman.username;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class PROBABILITY implements Serializable{
	PROBABILITY(ArrayList<Strategy> strategyList) {
		int numberStrategies = strategyList.size();
		probabilites = new double[numberStrategies];
		double[] temp = new double[numberStrategies];

		// Generate n random probabilities
		double sum = 0;
		for (int i = 0; i < numberStrategies; i++) {
			double initProbability = strategyList.get(i).getStrategyInitialProbability();
			temp[i] = (initProbability == Strategy.initialProbability.RANDOM) ? new Random().nextDouble() : initProbability;
			sum += temp[i];
		}

		for (int i = 0; i < numberStrategies; i++) {
			probabilites[i] = temp[i] / sum;
		}
	}

	public double getProbability(int numberOfStrategy) {
		return probabilites[numberOfStrategy];
	}
	public void setProbability(int numberOfStrategy, double newProbability) {
		newProbability = newProbability < 0 ? 0 : newProbability;
		probabilites[numberOfStrategy] = newProbability;
	}
	
	public void normalizeProbability()
	{
		double probabilitySum = 0;
		for (double probability : probabilites) {
			probabilitySum += probability;
		}
		
		//System.out.println("Probability sum before: " + probabilitySum);
		
		for (int i = 0;i<probabilites.length;i++) {
			probabilites[i] /= probabilitySum;
			//probabilites[i] *= 100.0;
		}
		
		probabilitySum = 0;
		for (double probability : probabilites) {
			probabilitySum += probability;
		}
		//System.out.println("Probability sum after: " + probabilitySum);
	}
	public int getNumberOfProbabilities()
	{
		return probabilites.length;
	}

	private double[] probabilites;
}
