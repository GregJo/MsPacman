package entrants.pacman.username;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.ArrayList;
import java.util.Arrays;


/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
   private PacManMemory memory = new PacManMemory();
   private ProbabilityGenerator probabilityGenerator;
   private ArrayList<Strategy> strategyList;
   private ArrayList<ArrayList<ProbabilityByState>> differentPacMans;
   public double fitness = 0;
   public double ticks = 0;
   public double score = 0;
   public int ghostsEaten = 0;
   public double dyingPenalty = 0;

   @SuppressWarnings("unchecked")
public MyPacMan()
   {
	   //Strategies PacMan should use
	   strategyList = new ArrayList<>(
			   Arrays.asList(
					  // new WaitStrategy(),
					   new EatNearestPowerPillStrategy(),
					   new EatGhostStrategy(),
					   new EatNearestAvailablePillStrategy(),
					   new EatFurthestAwayPowerPill(),
					  new EatFurthestAwayPill(),
					   new RunCircle(),
					   new GetRidOfGhost(),
					   new RandomPatrolInRadiusAroundCenter(),
					   new RunTowardsNearestKnownGhost(),
					   new RunFromNearestGhost()
			   )
		);
	   int numberStrategies = strategyList.size();
	   probabilityGenerator = new ProbabilityGenerator(numberStrategies);
	   
	   //Create all possible states (permutation of all possible enum values)
	   probabilityGenerator.createNProbabilitiesPerPossibleState(strategyList,
			   POWERPILLS_LEFT.class,
			//   KIND_OF_LEVEL_TILE.class,
			   NUMBER_SEEN_GHOSTS.class,
			 NUMBER_SEEN_EDIBLE_GHOSTS.class,
			   GHOST_DISTANCE_TO_POWERPILL.class,
			   POWER_PILL_ACTIVATED.class
			  // LIVES_LEFT.class  
	   );
	   probabilityGenerator.resetStaticStateVars(); //some states uses static variables that should be reset
	   
	   //load strategy probabilities of differently trained PacMan that will be used on death
	   differentPacMans = new ArrayList<>();
	   differentPacMans.add(GeneticAlgorithm.loadPacManProbabilities(System.getProperty("user.dir")+"/src/main/java/entrants/pacman/username/PacifistPacMan"));
	   differentPacMans.add(GeneticAlgorithm.loadPacManProbabilities(System.getProperty("user.dir")+"/src/main/java/entrants/pacman/username/MildlyAgressivePacMan"));
	   differentPacMans.add(GeneticAlgorithm.loadPacManProbabilities(System.getProperty("user.dir")+"/src/main/java/entrants/pacman/username/StrangePacMan"));
	   this.setProbabilities(differentPacMans.get(0));
	   
   }
   
   /*@brief Sets the probabilities for all states that should be used for this PacMan
    * @param probability_by_state_list the list of ProbabilityByState objects to set
    * */
   public void setProbabilities(ArrayList<ProbabilityByState> probability_by_state_list)
   {
	   probabilityGenerator.setProbabilityByStateList(probability_by_state_list);
	   probabilityGenerator.resetProbByStateCounters(); // reset state occurrence counters (needed for training)
   }
   
   /*@brief Gets all current strategy probabilities of this PacMan for all states
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
   public void setProbabilityForStrategy(int numberOfProbabilityByStateObject, int numberOfStrategy, double newProbability)
   {
	   probabilityGenerator.getProbabilityByStateList().get(numberOfProbabilityByStateObject).getProbabilityObject(false).setProbability(numberOfStrategy, newProbability);
   }
   /*@brief Gets the probability for a specific strategy and a specific state
    * @param numberOfProbabilityByStateObject The index of the ProbabilityByState Object containing all strategy probabilities for the specific state
    * @param numberOfStrategy The index of the strategy which probability should be returned
    * @returns The probability of the strategy numberOfStrategy in the state with index numberOfProbabilityByStateObject
    * */
   public double getProbabilityForStrategy(int numberOfProbabilityByStateObject, int numberOfStrategy)
   {
	   return probabilityGenerator.getProbabilityByStateList().get(numberOfProbabilityByStateObject).getProbabilityObject(false).getProbability(numberOfStrategy);
   }
   
   
   /*@brief Gets the sum of all state occurrence counters. Used for training.
    * @returns The sum of all state occurrence counters
    * */
   public int getStateCounterSum()
	{
		return probabilityGenerator.getStateCounterSum();
	}
 
   /*@brief Returns a move for PacMan depending on his current state
    * @returns The Move to make at this time step
    * */
    public MOVE getMove(Game game, long timeDue) {
    	
    	//change PacMan's probabilities
    	if(game.wasPacManEaten())
    	{
    		if(game.getPacmanNumberOfLivesRemaining() == 2)
    			 this.setProbabilities(differentPacMans.get(1));//Mildly Agressive PacMan
    		if(game.getPacmanNumberOfLivesRemaining() == 1)
   			 	this.setProbabilities(differentPacMans.get(2));//Strange PacMan
    	}
    	 	
    	int current = game.getPacmanCurrentNodeIndex();
    	memory.updateMemory(game, current);
    	
    	//get number of strategy to use
    	int rouletteStrategyNumber = probabilityGenerator.geStrategyNumberToUse(game, current, memory, strategyList);
    	 
    	//get move of strategy with index rouletteStrategyNumber
    	MOVE move = strategyList.get(rouletteStrategyNumber).getStrategyMove(game, current, memory);
    	
    	
    	//calculate fitness. Used only for training.
    	 ticks = (game.getTotalTime() == 0) ? 1 :  game.getTotalTime();
    	 score = game.getScore();
    	 ghostsEaten += 30*game.getNumGhostsEaten();
    	 if(game.wasPacManEaten())
    		 dyingPenalty += 100/ticks;
    	 fitness = score/ticks + ghostsEaten - dyingPenalty;
    	
    	return move;
    }
}