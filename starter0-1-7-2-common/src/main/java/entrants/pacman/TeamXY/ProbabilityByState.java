package entrants.pacman.TeamXY;

import java.io.Serializable;
import java.util.Random;

/*Object to map states to probabilities for each strategy.
 * The Object contains a unique stateString that identifies the state that has to occur for the probabilities to be used*/
public class ProbabilityByState	implements Serializable{
	ProbabilityByState(String stateString, PROBABILITY prob) {
		m_probability = prob;
		m_stateString = stateString;
	}
	
	/*@brief Gets the probabilities of all strategies
	* @param increaseCounter true if the state occurrence counter should be increased. 
	* 	The counter should be increased when playing games to measure how often a state occurred. It shouldn't be increased when in training or anything else.
	* @returns the probabilities of all strategies
	* */
	public PROBABILITY getProbabilityObject(boolean increaseCounter){if(increaseCounter)counter++;return m_probability;}

	/*@brief Returns the number of probabilities which is also the number of strategies used
	* @returns The number of probabilities which is also the number of strategies used
	* */
	public int getNumberOfProbabilities(){return m_probability.getNumberOfProbabilities();}
	
	/*@brief Sets the probability of a specific strategy
	* @param numberOfStrategy the index of the strategy which probability should be returned
	* @param newProbability the new Proabbility that should be set for the strategy with index numberOfStrategy
	* */
	public void setProbability(int numberOfProbability, double newProbability){m_probability.setProbability(numberOfProbability, newProbability);}
	
	/*@brief Returns the probability of a specific strategy
	* @param numberOfStrategy the index of the strategy which probability should be returned
	* @returns the probability of the strategy with index numberOfStrategy
	* */
	public double getProbability(int numberOfProbability){return m_probability.getProbability(numberOfProbability);}
	
	/*@brief Normalizes all probabilities of this object to ensure that the sum of all probabilities is always 1 (100%)
	   * */
	public void normalizeProbabilities(){m_probability.normalizeProbability();}
	
	private final PROBABILITY m_probability; //the object holding all probabilities
	public final String m_stateString; 
	public int counter = 0; //counter that is used to count number of occurrences of this state
}


