package entrants.pacman.username;

import java.util.Random;

public class ProbabilityByState {
	ProbabilityByState(String stateString, PROBABILITY prob) {
		m_probability = prob;
		m_stateString = stateString;
	}
	public PROBABILITY getProbability(){counter++;return m_probability;}

	private final PROBABILITY m_probability;
	public final String m_stateString;
	public int counter = 0;
}

class PROBABILITY {
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

	private double[] probabilites;
}
