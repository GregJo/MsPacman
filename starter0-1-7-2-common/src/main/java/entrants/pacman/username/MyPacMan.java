package entrants.pacman.username;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
   private Memory memory = new Memory();
   private ProbabilityGenerator probabilityGenerator;
   private ArrayList<PacManStrategy> strategyList;
   public double fitness = 0;
   public double ticks = 0;
   public double score = 0;

   @SuppressWarnings("unchecked")
public MyPacMan()
   {
	   strategyList = new ArrayList<>(
			   Arrays.asList(
					   new WaitStrategy(),
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
	   probabilityGenerator.createNProbabilitiesPerPossibleState(strategyList,
			   POWERPILLS_LEFT.class,
			   KIND_OF_LEVEL_TILE.class,
			   NUMBER_SEEN_GHOSTS.class,
			 NUMBER_SEEN_EDIBLE_GHOSTS.class,
			   GHOST_DISTANCE_TO_POWERPILL.class,
			   POWER_PILL_ACTIVATED.class
			  // LIVES_LEFT.class  
	   );
	   probabilityGenerator.resetStaticStateVars();
   }
   
   /*@brief Sets the probabiblities that should be used for this pacman
    * @param probability_by_state_list the list of probabilities to set
    * */
   public void setProbabilities(ArrayList<ProbabilityByState> probability_by_state_list)
   {
	   probabilityGenerator.setProbabilityByStateList(probability_by_state_list);
	   probabilityGenerator.resetProbByStateCounters();
   }
   public ArrayList<ProbabilityByState> getProbabilities()
   {
	   return probabilityGenerator.getProbabilityByStateList();
   }
   
   public void setProbabilityForStrategy(int numberOfStrategy, int numberOfprobability, double newProbability)
   {
	   probabilityGenerator.getProbabilityByStateList().get(numberOfStrategy).getProbabilityObject(false).setProbability(numberOfprobability, newProbability);
   }
   
   public double getProbabilityForStrategy(int numberOfStrategy, int numberOfprobability)
   {
	   return probabilityGenerator.getProbabilityByStateList().get(numberOfStrategy).getProbabilityObject(false).getProbability(numberOfprobability);
   }
   
   public int getStateCounterSum()
	{
		return probabilityGenerator.getStateCounterSum();
	}
 
   
    public MOVE getMove(Game game, long timeDue) {
    	 	
    	int current = game.getPacmanCurrentNodeIndex();
    	memory.updateMemory(game, current);
    	
    	int rouletteStrategyNumber = probabilityGenerator.geStrategyNumberToUse(game, current, memory, strategyList);
    	 MOVE move = strategyList.get(rouletteStrategyNumber).getStrategyMove(game, current, memory);
    	 ticks = (game.getTotalTime() == 0) ? 1 :  game.getTotalTime();
    	 score = game.getScore();
    	 fitness = score/ticks;
    	
    	return move;
    }
}