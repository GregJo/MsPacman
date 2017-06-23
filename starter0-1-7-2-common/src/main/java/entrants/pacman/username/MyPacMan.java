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
   private ArrayList<Strategy> strategyList;
   //private ArrayList<Integer> pillMemory = new ArrayList<Integer>();
   //private ArrayList<MOVE> moveMemory = new ArrayList<MOVE>();
   //private int[] ghostMemory = new int[4];
   //private ArrayList<GHOST> edibleGhostsList = new ArrayList<GHOST>();
   //private ArrayList<GHOST> visibleGhostsList = new ArrayList<GHOST>();


   @SuppressWarnings("unchecked")
public MyPacMan()
   {
	   strategyList = new ArrayList<>(
			   Arrays.asList(
//					   new WaitStrategy(),
//					   new EatNearestPowerPillStrategy(),
//					   new EatGhostStrategy(),
					   //new EatNearestAvailablePillStrategy(),
					   //new EatFurthestAwayPowerPill(),
					  // new EatFurthestAwayPill(),
					   new RunCircle()
			   )
		);
	   
	   int numberStrategies = strategyList.size();
	   probabilityGenerator = new ProbabilityGenerator(numberStrategies);
	   probabilityGenerator.createNProbabilitiesPerPossibleState(numberStrategies,
			   POWERPILLS_LEFT.class,
			   KIND_OF_LEVEL_TILE.class,
			   NUMBER_SEEN_GHOSTS.class,
			   NUMBER_SEEN_EDIBLE_GHOSTS.class,
			   GHOST_DISTANCE_TO_POWERPILL.class,
			   POWER_PILL_ACTIVATED.class,
			   LIVES_LEFT.class   
	   );
   }
   
   /*@brief Sets the probabiblities that should be used for this pacman
    * @param probability_by_state_list the list of probabilities to set
    * */
   public void setProbabilities(ArrayList<ProbabilityByState> probability_by_state_list)
   {
	   probabilityGenerator.setProbabilityByStateList(probability_by_state_list);
   }
   public ArrayList<ProbabilityByState> getProbabilities()
   {
	   return probabilityGenerator.getProbabilityByStateList();
   }
 
   
    public MOVE getMove(Game game, long timeDue) {
    	 	
    	int current = game.getPacmanCurrentNodeIndex();
    	memory.updateMemory(game, current);
    	
    	int rouletteStrategyNumber = probabilityGenerator.geStrategyNumberToUse(game, current, memory);
    	 MOVE move = strategyList.get(rouletteStrategyNumber).getStrategyMove(game, current, memory);
    	return move;
    }
}