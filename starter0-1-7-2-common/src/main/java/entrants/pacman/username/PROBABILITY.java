package entrants.pacman.username;

import java.util.Random;

public class PROBABILITY {
	PROBABILITY(int numberStrategies) {
		probabilites = new double[numberStrategies];
		double[] temp = new double[numberStrategies];

		// Generate n random probabilities
		double sum = 0;
		for (int i = 0; i < numberStrategies; i++) {
			temp[i] = new Random().nextDouble();
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
		for (double probability : probabilites) {
			probability /= probabilitySum;
			probability *= 100.0;
		}
	}
	public int getNumberOfProbabilities()
	{
		return probabilites.length;
	}

	private double[] probabilites;
}
