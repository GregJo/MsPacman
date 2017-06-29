package entrants.pacman.username;

import java.io.Serializable;
import java.util.Random;

public class ProbabilityByState	implements Serializable{
	ProbabilityByState(String stateString, PROBABILITY prob) {
		m_probability = prob;
		m_stateString = stateString;
	}
	public PROBABILITY getProbabilityObject(boolean increaseCounter){if(increaseCounter)counter++;return m_probability;}

	public int getNumberOfProbabilities(){return m_probability.getNumberOfProbabilities();}
	public void setProbability(int numberOfProbability, double newProbability){m_probability.setProbability(numberOfProbability, newProbability);}
	public double getProbability(int numberOfProbability){return m_probability.getProbability(numberOfProbability);}
	public void normalizeProbabilities(){m_probability.normalizeProbability();}
	
	private final PROBABILITY m_probability;
	public final String m_stateString;
	public int counter = 0;
}


