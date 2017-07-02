package entrants.ghosts.TeamXY;

import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import entrants.pacman.TeamXY.GeneticAlgorithm;
import entrants.pacman.TeamXY.ProbabilityByState;
import entrants.pacman.TeamXY.ProbabilityGenerator;
import entrants.pacman.TeamXY.Strategy;


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
	   probabilityGenerator.createNProbabilitiesPerPossibleState(strategyList,
	   																POWERPILLS_LEFT.class,
																	GHOST_DISTANCE_TO_POWERPILL.class
	   																);
	   probabilityGenerator.resetStaticStateVars();
	   
	   //load strategy probabilities of differently trained PacMan that will be used on death
	   differentGhost = new ArrayList<>();
	   differentGhost.add(GeneticAlgorithm.loadPacManProbabilities(System.getProperty("user.dir")+"/src/main/java/entrants/ghosts/username/trainedGhosts"));
	   this.setProbabilities(differentGhost.get(new Random().nextInt(differentGhost.size())));
	   
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