package entrants.pacman.username;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import pacman.game.Game;

/*Object to generate all probabilities for all possible states.*/
public class ProbabilityGenerator {
	public ProbabilityGenerator(int numberOfStrategies) {
		m_numberOfStrategies = numberOfStrategies;
	}

	private int m_numberOfStrategies = 0;
	private ArrayList<ProbabilityByState> m_probability_by_state_list = new ArrayList<ProbabilityByState>();
	private Class<? extends Enum<? extends StateEnum>>[] m_listOfUsedEnums;
	private int stateCounterSum = -1;
	private int lastStrategyNumber = -1;
	
	/*@brief Resets static variables of all used state enums
	* */
	public void resetStaticStateVars()
	{
		for( Class<? extends Enum<? extends StateEnum>> enumType : m_listOfUsedEnums)
		{
			Enum<? extends StateEnum> e = enumType.getEnumConstants()[0];
			((StateEnum) e).resetStaticVars();
		}
	}
	
	/*@brief Resets all state occurrence counters in the ProbabilityByState objects
	* */
	public void resetProbByStateCounters()
	{
		for(int i=0; i <  m_probability_by_state_list.size(); i++)
		{
			m_probability_by_state_list.get(i).counter = 0;
		}
	}
	
	/*@brief Returns sum of all state occurrence counters in the ProbabilityByState objects
	 *@returns The sum of all state occurrence counters in the ProbabilityByState objects
	* */
	public int getStateCounterSum()
	{
		stateCounterSum = 0;
		for(ProbabilityByState p : m_probability_by_state_list)
		{
			stateCounterSum += p.counter;
		}
		return stateCounterSum;
		
	}
	
	/*@brief Exchanges the probabilities for all states with the given probabilities
	 *@param probability_by_state_list a list of probabilities for all states
	* */
	public void setProbabilityByStateList(ArrayList<ProbabilityByState> probability_by_state_list){
		m_probability_by_state_list = probability_by_state_list;
	}
	
	/*@brief Gets all current strategy probabilities for all states
	 * @returns All probabilities of all possible states
	 * */
	public final ArrayList<ProbabilityByState> getProbabilityByStateList(){
		return m_probability_by_state_list;
	}

	/*@brief Computes the current state.
	 * @returns A string identifying the current state.
	 * */
	private String getCurrentStateString(Game game, int current, PacManMemory memory)
	{
		String stateString = "";
		for( Class<? extends Enum<? extends StateEnum>> enumType : m_listOfUsedEnums)
		{
			Enum<? extends StateEnum> e = enumType.getEnumConstants()[0];
			stateString += "_" + ((StateEnum) e).getCurrentStateString(game, current, memory);
		}
		
			//check if state changed. Strategies are locked until the state changes.
		   memory.stateChanged = true;
		   if(stateString.equals(memory.lastStateString))
			   memory.stateChanged = false;
		   memory.lastStateString = stateString;
		
		return stateString;
	}
	
	/*@brief Gets the probabilities for the current state
	 * @param game the current Game object
	 * @param current the current position of PacMan
	 * @param memory the memory of the current PacMan
	 * @returns The probabilities of the current state
	 * */
	private final PROBABILITY getCurrentProbability(Game game, int current, PacManMemory memory){
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
	/*@brief Creates n Probabilities for all possible states by permutating the given state enums. 
	 * @param lastStateString empty string in the beginning. The function calls itself recursively with each possible state string.
	 * @param strategyList list of all strategies to use
	 * @param listOfStateEnums list of all enums to use
	 * */
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

	/*@brief Creates n Probabilities for all possible states by permutating the given state enums. 
	 * @param lastStateString empty string in the beginning. The function calls itself recursively with each possible state string.
	 * @param strategyList list of all strategies to use
	 * */
	public void createNProbabilitiesPerPossibleState(ArrayList<Strategy> strategyList, Class<? extends Enum<? extends StateEnum>>... listOfStateEnums) {
		m_listOfUsedEnums = listOfStateEnums;
		_createNProbabilitiesPerPossibleState("", strategyList, listOfStateEnums);
	}
	
	/*@brief Returns the index of the strategy that should be used in the current state. 
	 *@param game the current Game object
	 *@param current the current position of PacMan
	 *@param memory the memory of the current PacMan
	 *@param strategyList list of all strategies
	 *@returns the idnex of the strategy tha should be used in the current state.
	 * */
	public int geStrategyNumberToUse(Game game, int current, PacManMemory memory, final ArrayList<Strategy> strategyList){
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



