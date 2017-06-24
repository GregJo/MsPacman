package entrants.pacman.username;

import java.util.Random;

public class ProbabilityByState	{
	ProbabilityByState(String stateString, PROBABILITY prob) {
		m_probability = prob;
		m_stateString = stateString;
	}
	public PROBABILITY getProbability(){counter++;return m_probability;}

	private final PROBABILITY m_probability;
	public final String m_stateString;
	public int counter = 0;
}


