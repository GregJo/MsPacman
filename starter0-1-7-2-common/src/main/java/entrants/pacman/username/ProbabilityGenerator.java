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
	private Class<? extends Enum<? extends StateEnum>>[] m_listOfUsedEnums;
	private int stateCounterSum = -1;
	private int lastStrategyNumber = -1;
	
	public void resetStaticStateVars()
	{
		for( Class<? extends Enum<? extends StateEnum>> enumType : m_listOfUsedEnums)
		{
			Enum<? extends StateEnum> e = enumType.getEnumConstants()[0];
			((StateEnum) e).resetStaticVars();
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
		for( Class<? extends Enum<? extends StateEnum>> enumType : m_listOfUsedEnums)
		{
			Enum<? extends StateEnum> e = enumType.getEnumConstants()[0];
			stateString += "_" + ((StateEnum) e).getCurrentStateString(game, current, memory);
		}
		
		   memory.stateChanged = true;
		   if(stateString.equals(memory.lastStateString))
			   memory.stateChanged = false;
		   memory.lastStateString = stateString;
		
		return stateString;
	}
	
	private final PROBABILITY getCurrentProbability(Game game, int current, Memory memory){
		   String currentStateString = this.getCurrentStateString(game, current, memory);
		   //System.out.println(currentStateString);
		   for(ProbabilityByState prob_by_state : getProbabilityByStateList())
		   {
			   if(prob_by_state.m_stateString.equals(currentStateString)){
				   return prob_by_state.getProbabilityObject(true);
			   }
		   }
		   return null;
	   }
	
	private void _createNProbabilitiesPerPossibleState(String lastStateString, ArrayList<Strategy> strategyList,
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
				PROBABILITY prob = new PROBABILITY(strategyList);
				ProbabilityByState prob_by_dep = new ProbabilityByState(completeStateString, prob);
				m_probability_by_state_list.add(prob_by_dep);
			}

			// if there are still arguments left, do recursive call with
			// rest of arguments and new stateString
			if (listOfStateEnums.length > 1) {
				String newStateString = lastStateString + "_" + e.name();
				_createNProbabilitiesPerPossibleState(newStateString, strategyList, restArguments);
			}
		}
	}

	public void createNProbabilitiesPerPossibleState(ArrayList<Strategy> strategyList, Class<? extends Enum<? extends StateEnum>>... listOfStateEnums) {
		m_listOfUsedEnums = listOfStateEnums;
		_createNProbabilitiesPerPossibleState("", strategyList, listOfStateEnums);
	}
	
	public int geStrategyNumberToUse(Game game, int current, Memory memory, final ArrayList<Strategy> strategyList){
		PROBABILITY probabilities = this.getCurrentProbability(game, current, memory);
		
		if(memory.stateChanged == false)
			return this.lastStrategyNumber;
		while(true)
		{
			for(int i = 0; i < m_numberOfStrategies; i++)
			{
				 if(new Random().nextDouble() <= probabilities.getProbability(i))
				 {
					 if(strategyList.get(i).requirementsMet(game, current, memory))
					 {
						 this.lastStrategyNumber = i;
						 return i;
					 }
				 }
					   
			}
		}
	}
	
	
}



