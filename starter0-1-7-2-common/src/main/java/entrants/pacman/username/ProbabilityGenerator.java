package entrants.pacman.username;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import pacman.game.Game;

public class ProbabilityGenerator {
	public ProbabilityGenerator(int numberOfStrategies) {
		m_numberOfStrategies = numberOfStrategies;
	}

	private int m_numberOfStrategies = 0;
	private ArrayList<ProbabilityByState> m_probability_by_state_list = new ArrayList<ProbabilityByState>();
	private Class<? extends Enum<? extends stateEnum>>[] m_listOfUsedEnums;
	private int stateCounterSum = -1;
	
	public void resetStaticStateVars()
	{
		for( Class<? extends Enum<? extends stateEnum>> enumType : m_listOfUsedEnums)
		{
			Enum<? extends stateEnum> e = enumType.getEnumConstants()[0];
			((stateEnum) e).resetStaticVars();
		}
	}
	
	public void resetProbByStateCounters()
	{
		for(int i=0; i <  m_probability_by_state_list.size(); i++)
		{
			m_probability_by_state_list.get(i).counter = 0;
		}
	}
	
	public int getStateCounterSum()
	{
		stateCounterSum = 0;
		for(ProbabilityByState p : m_probability_by_state_list)
		{
			stateCounterSum += p.counter;
		}
		return stateCounterSum;
		
	}
	
	public void setProbabilityByStateList(ArrayList<ProbabilityByState> probability_by_state_list){
		m_probability_by_state_list = probability_by_state_list;
	}
	
	public final ArrayList<ProbabilityByState> getProbabilityByStateList(){
		return m_probability_by_state_list;
	}

	private String getCurrentStateString(Game game, int current, Memory memory)
	{
		String stateString = "";
		for( Class<? extends Enum<? extends stateEnum>> enumType : m_listOfUsedEnums)
		{
			Enum<? extends stateEnum> e = enumType.getEnumConstants()[0];
			stateString += "_" + ((stateEnum) e).getCurrentStateString(game, current, memory);
		}
		return stateString;
	}
	
	private final PROBABILITY getCurrentProbability(Game game, int current, Memory memory){
		   String currentStateString = this.getCurrentStateString(game, current, memory);
		   //System.out.println(currentStateString);
		   for(ProbabilityByState prob_by_state : getProbabilityByStateList())
		   {
			   if(prob_by_state.m_stateString.equals(currentStateString)){
				   return prob_by_state.getProbability();
			   }
		   }
		   return new PROBABILITY(0);
	   }
	
	private void _createNProbabilitiesPerPossibleState(String lastStateString, int n,
			Class<? extends Enum<?>>... listOfStateEnums) {
		Class<? extends Enum<?>> firstArgument;
		Class<? extends Enum<?>>[] restArguments = listOfStateEnums;
		assert listOfStateEnums.length > 0;

		// get first and rest of the arguments
		firstArgument = listOfStateEnums[0];
		if (listOfStateEnums.length > 1) {
			restArguments = Arrays.copyOfRange(listOfStateEnums, 1, listOfStateEnums.length);
		}

		for (Enum<?> e : firstArgument.getEnumConstants()) {
			// stop recursion and create objects if this is the last argument
			if (listOfStateEnums.length == 1) {
				String completeStateString = lastStateString + "_" + e.name();
				PROBABILITY prob = new PROBABILITY(n);
				ProbabilityByState prob_by_dep = new ProbabilityByState(completeStateString, prob);
				m_probability_by_state_list.add(prob_by_dep);
			}

			// if there are still arguments left, do recursive call with
			// rest of arguments and new stateString
			if (listOfStateEnums.length > 1) {
				String newStateString = lastStateString + "_" + e.name();
				_createNProbabilitiesPerPossibleState(newStateString, n, restArguments);
			}
		}
	}

	public void createNProbabilitiesPerPossibleState(int n, Class<? extends Enum<? extends stateEnum>>... listOfStateEnums) {
		m_listOfUsedEnums = listOfStateEnums;
		_createNProbabilitiesPerPossibleState("", n, listOfStateEnums);
	}
	
	public int geStrategyNumberToUse(Game game, int current, Memory memory){
		PROBABILITY probabilities = this.getCurrentProbability(game, current, memory);
		for(int i = 0; i < m_numberOfStrategies; i++)
		{
			 if(new Random().nextDouble() <= probabilities.getProbability(i))
				   return i;
		}
		return 0;
	}
	
	
}



