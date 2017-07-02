package entrants.ghosts.username;

import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import entrants.pacman.username.GeneticAlgorithm;
import entrants.pacman.username.ProbabilityByState;
import entrants.pacman.username.ProbabilityGenerator;
import entrants.pacman.username.Strategy;


/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyGhost{
	private GHOST ghost;
   private GhostMemory memory = new GhostMemory();
   private ProbabilityGenerator probabilityGenerator;
   private ArrayList<Strategy> strategyList;
   public double fitness = 0;
   public double ticks = 0;
   public double score = 0;
   public double numOfDeaths = 0;
   private ArrayList<ArrayList<ProbabilityByState>> differentGhost;

   @SuppressWarnings("unchecked")
public MyGhost(GHOST ghost)
   {
	   //Strategies Ghosts should use
	   strategyList = new ArrayList<>(
			   Arrays.asList(
					   new HuntPacMan(),
					   new RunAwayFromPacMan(),
					   new GoToNearestPowerPill(),
					   new AvoidOtherGhost(),
					   new RunCircle()
			   )
		);
	   
	   this.ghost=ghost;
	   int numberStrategies = strategyList.size();
	   probabilityGenerator = new ProbabilityGenerator(numberStrategies);
	   
	 //Create all possible states (permutation of all possible enum values)
	   probabilityGenerator.createNProbabilitiesPerPossibleState(strategyList,
	   																POWERPILLS_LEFT.class,
																	GHOST_DISTANCE_TO_POWERPILL.class
	   																);
	   probabilityGenerator.resetStaticStateVars(); //reset static variables
	   
	   //load strategy probabilities of differently trained ghosts that will be used on death
	   differentGhost = new ArrayList<>();
	   differentGhost.add(GeneticAlgorithm.loadPacManProbabilities(System.getProperty("user.dir")+"/src/main/java/entrants/ghosts/TeamXY/trainedGhosts"));
	   this.setProbabilities(differentGhost.get(new Random().nextInt(differentGhost.size())));
	   
   }
   
   /*@brief Sets the probabilities for all states that should be used for this ghost
    * @param probability_by_state_list the list of ProbabilityByState objects to set
    * */
   public void setProbabilities(ArrayList<ProbabilityByState> probability_by_state_list)
   {
	   probabilityGenerator.setProbabilityByStateList(probability_by_state_list);
	   probabilityGenerator.resetProbByStateCounters();
   }
   
   /*@brief Gets all current strategy probabilities of this ghost for all states
    * @param probability_by_state_list the list of probabilities to set
    * @returns All probabilities of all possible states
    * */
   public ArrayList<ProbabilityByState> getProbabilities()
   {
	   return probabilityGenerator.getProbabilityByStateList();
   }
   
   /*@brief Sets the probability for a specific strategy and a specific state
    * @param numberOfProbabilityByStateObject The index of the ProbabilityByState Object containing all strategy probabilities for the specific state
    * @param numberOfStrategy The index of the strategy which probability should be set
    * */
   public void setProbabilityForStrategy(int numberOfStrategy, int numberOfprobability, double newProbability)
   {
	   probabilityGenerator.getProbabilityByStateList().get(numberOfStrategy).getProbabilityObject(false).setProbability(numberOfprobability, newProbability);
   }
   
   /*@brief Gets the probability for a specific strategy and a specific state
    * @param numberOfProbabilityByStateObject The index of the ProbabilityByState Object containing all strategy probabilities for the specific state
    * @param numberOfStrategy The index of the strategy which probability should be returned
    * @returns The probability of the strategy numberOfStrategy in the state with index numberOfProbabilityByStateObject
    * */
   public double getProbabilityForStrategy(int numberOfStrategy, int numberOfprobability)
   {
	   return probabilityGenerator.getProbabilityByStateList().get(numberOfStrategy).getProbabilityObject(false).getProbability(numberOfprobability);
   }
   
   /*@brief Gets the sum of all state occurrence counters. Used for training.
    * @returns The sum of all state occurrence counters
    * */
   public int getStateCounterSum()
	{
		return probabilityGenerator.getStateCounterSum();
	}
 
   /*@brief Returns a move for this ghost depending on his current state
    * @returns The Move to make at this time step
    * */
    public MOVE getMove(Game game, long timeDue) {
    	 	
    	
    	if(game.wasGhostEaten(ghost))
    	{
    		 this.setProbabilities(differentGhost.get(new Random().nextInt(differentGhost.size())));
    	}
    	
    	int current = game.getGhostCurrentNodeIndex(ghost);
    	memory.updateMemory(game, current);
    	
    	MOVE move = null;
    	if (game.getGhostLairTime(ghost) == 0) {
	    	int rouletteStrategyNumber = probabilityGenerator.geStrategyNumberToUse(game, current, memory, strategyList);
	    	 move = strategyList.get(rouletteStrategyNumber).getStrategyMove(game, ghost, current, memory);
	    	 if (game.wasGhostEaten(ghost)) {
	    		 numOfDeaths -= 1000;
			}
	    	 fitness -= game.getScore() + numOfDeaths;
    	}
    	
    	return move;
    	 
    }
}