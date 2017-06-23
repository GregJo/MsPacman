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
//					   new EatNearestAvailablePillStrategy(),
//					   new GetRidOfGhost()
					   new RunFromNearestGhost()
//					   new RunTowardsNearestKnownGhost()
//					   new RandomPatrolInRadiusAroundCenter()
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
    	
//    	int[] powerPillIndizes =  game.getActivePowerPillsIndices();
//    	int[] pillIndizes =  game.getActivePillsIndices();   	
    	
//    	updateMoveMemory(game);
//    	deleteEatenPillsFromMemory(game, current);
//    	updateEdibleGhostsList(game, current);
//    	updateVisibleGhostsList(game, current);
//    	updateGhostMemory(game, current);
    	
    	int rouletteStrategyNumber = probabilityGenerator.geStrategyNumberToUse(game, current, memory);
    	 MOVE move = strategyList.get(rouletteStrategyNumber).getStrategyMove(game, current, memory);
    	 return move;
   
//    	
//    	if(rollDice(current_probabilities, 0)){
//  
//    		totalTime = game.getTotalTime();
//    		return doOppositeFromLastTime();
//    	}
//    	if(rollDice(current_probabilities, 1)){
//    		totalTime = game.getTotalTime();
//    		return runAwayComplex(game, current);
//    	}
//    	
//    	totalTime = game.getTotalTime();
//    	return runAwayRandom(game, current);
    	
    	
//    	if(edibleGhostsList.size() > 0)
//    	{
//    		int[] edibleGhostIndizes = new int[edibleGhostsList.size()];
//    		int index = 0;
//    		for(GHOST edibleGhost : edibleGhostsList)
//    		{
//    			int ghostPosition = (game.getGhostCurrentNodeIndex(edibleGhost) == -1) ? ghostMemory[getIndexOfGhost(edibleGhost)] : game.getGhostCurrentNodeIndex(edibleGhost);
//    			edibleGhostIndizes[index] = ghostPosition;
//    			index++;
//    		}
//    		AddNewPillsToMemory(game, current);
//    		lastStrategy = STRATEGY.EatGhost;
//    		
//    		System.out.println("");
//			System.out.println("");
//    		for(int p : edibleGhostIndizes)
//    		{
//    			System.out.println("edibleGhost index: "+p);
//    		}
//    		return goToNextObject(game, current, edibleGhostIndizes);
//    		
//    	}
//    	
//    	if( visibleGhostsList.size() > 0 || ghostMemory.length > 0)
//    	{
//    		lastStrategy = STRATEGY.RunAway;
//    		MOVE moveToBeMade = runAway(game,current);
//    		if(moveToBeMade != MOVE.NEUTRAL)
//    			return moveToBeMade;
//    	}
//    	
//    	if(pillIndizes.length > 0)
//    	{
//    		MOVE move = goToNextObject(game, current, pillIndizes);
//    		AddNewPillsToMemory(game, current);
//    		lastStrategy = STRATEGY.EatVisiblePill;
//    		return move;
//    	}
//    	if(pillMemory.size() > 0)
//    	{
//    		int[] indizes = new int[1];
//    		indizes[0] = pillMemory.get(pillMemory.size()-1);
//    		MOVE move = goToNextObject(game, current, indizes);
//    		AddNewPillsToMemory(game, current);
//    		lastStrategy = STRATEGY.EatPillFromMemory;
//    		return move;
//    	}
//    	lastStrategy = STRATEGY.Random;
//   	 MOVE[] moves = game.getPossibleMoves(current,game.getPacmanLastMoveMade());
//   	 AddNewPillsToMemory(game, current);
//     return moves[random.nextInt(moves.length)];
    
    }
    
    
//    private void deleteEatenPillsFromMemory(Game game, int current )
//    {
//    	//check if pill was eaten in last move -> remove eaten pill from memory
//    	if(game.getPillIndex(current) != -1)
//    	{
//    		Iterator<Integer> iterator = pillMemory.iterator();
//    		while (iterator.hasNext())
//    		{
//    			Integer pill  = iterator.next();
//    			if(pill.intValue() == current)
//    			{
//    				iterator.remove();
//    			}
//    		}
//    	}
//    	
//    	//add new pills
//    	int[] activePills = game.getActivePillsIndices();
//    	for(int activePill : activePills)
//    	{
//    		pillMemory.add(new Integer(activePill));
//    	}
//    }
//    private void updateMoveMemory(Game game){
//    	moveMemory.add(game.getPacmanLastMoveMade());
//    }
//    private void AddNewPillsToMemory(Game game, int current )
//    { 	
//    	if(game.wasPacManEaten()){
//    		pillMemory = new ArrayList<Integer>();
//    	}
//    	//add new pills
//    	int[] activePills = game.getActivePillsIndices();
//    	for(int activePill : activePills)
//    	{
//    		pillMemory.add(new Integer(activePill));
//    	}
//    }
    
  
    
//    private void updateEdibleGhostsList(Game game, int current)
//    {
//    	edibleGhostsList = new ArrayList<GHOST>();
//    	
//    	
//    	for(GHOST ghost : GHOST.values())
//    	{
//    		if(game.getGhostEdibleTime(ghost) > 0) {
//    			edibleGhostsList.add(ghost);
//    		}
//    	}
//    	
//    	//put ghosts in memory into edibleList for hunting fun
////    	if(powerPillTime > 0) 
////    	{
////    		int index = 0;
////    		updateGhostMemory(game, current); //update memory first to exclude ghosts eaten inthe last step
////    		for(int memoryOfGhost : ghostMemory)
////    		{
////    			if(memoryOfGhost != -1)
////    			{
////    				edibleGhostsList.add(getGhostWithIndex(index));
////    			}
////    			index++;
////    		}
////    	}
//    }
//    private void updateVisibleGhostsList(Game game, int current)
//    {
//    	visibleGhostsList = new ArrayList<GHOST>();
//    	for(GHOST ghost : GHOST.values())
//    	{
//    		if(game.getGhostCurrentNodeIndex(ghost) != -1)
//    		{
//    			visibleGhostsList.add(ghost);
//    		}
//    	}
//    }
    
//    private MOVE runAway(Game game, int current)
//    {
////		System.out.println("");
////		System.out.println("");
//    	MOVE[] possibleMoves = game.getPossibleMoves(current);
//    	for(MOVE move : possibleMoves)
//		{
//    		double currentDistanceToGhost =-1;
//			double nextDistanceToGhost = -1;
//    		int nextCurrent = game.getNeighbour(current, move);
//    		boolean badMove = false;
//    		for(GHOST ghost : visibleGhostsList)
//        	{
//    			 currentDistanceToGhost = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
//    			 nextDistanceToGhost = game.getShortestPathDistance(nextCurrent, game.getGhostCurrentNodeIndex(ghost));
//        		if(nextDistanceToGhost < currentDistanceToGhost)
//        		{
//        			badMove = true;
//        			break;
//        		}
//        	}
//    		
//    		if(visibleGhostsList.size() == 0)
//    		{
//    			
//    			for(int ghostPosition : ghostMemory)
//            	{
//    				if(ghostPosition < 1) // -1 = ghost was eaten
//    					continue;
//        			currentDistanceToGhost = game.getShortestPathDistance(current, ghostPosition);
//        			nextDistanceToGhost = game.getShortestPathDistance(nextCurrent, ghostPosition);
//            	
//        			if(nextDistanceToGhost < currentDistanceToGhost)
//            		{
//            			badMove = true;
//            			break;
//            		}
//            	}
//    		}
//    		
//    		if(!badMove){
//
////    			System.out.println("last strategy: " + lastStrategy);
////				System.out.println("LastMove: " + game.getPacmanLastMoveMade());
////    			System.out.println("Best Move: " + move);	
////    			System.out.println("currentDistanceToGhost: "+currentDistanceToGhost);
////        		System.out.println("nextDistanceToGhost: "+nextDistanceToGhost);
//    			return move;
//    		}
//		}
//    	//System.out.println("Returning "+game.getPacmanLastMoveMade());
//    	return MOVE.NEUTRAL;
//    }
    
    private boolean checkStrategy(Game game, int current, MOVE nextMove)
    {
    	int nextCurrent = game.getNeighbour(current, nextMove);
    	for(GHOST ghost : GHOST.values())
		{
    		int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
    		if(ghostIndex == -1)
    		{
    			continue;
    		}
    		
    		int distanceCurrentGhost = Math.abs(current - ghostIndex);
    		int distanceNextMoveGhost = Math.abs(nextCurrent - ghostIndex);
			if( distanceNextMoveGhost < 100 && distanceCurrentGhost > distanceNextMoveGhost)
			{
				return false;
			}
		}
    	return true;
    }
//    private void updateGhostMemory(Game game, int current)
//    {
//    	if(game.wasPacManEaten()){//reset memory
//    		roundsSinceGhostMemoryRefreshed = 100;
//    		ghostMemory = new int[0];
//    		return;
//    	}
//    	
//    	//remove ghosts that were eaten from memory
//    	if(ghostMemory.length == 4)
//    	{
//    		for(GHOST ghost : GHOST.values())
//        	{
//        		if(game.wasGhostEaten(ghost))
//        		{
//        			ghostMemory[getIndexOfGhost(ghost)] = -1; //remove ghost
//        		}
//        	}
//    		
//    		//check if no ghosts in memory
//    		boolean ghostFound = false;
//    		for(int i = 0; i < ghostMemory.length; i++)
//    		{
//    			if(ghostMemory[i] != -1)
//    			{
//    				ghostFound = true;
//    				break;
//    			}
//    		}
//    		if(!ghostFound)
//    		{
//    			ghostMemory = new int[0];
//    			roundsSinceGhostMemoryRefreshed = 100;
//    		}
//    			
//    	}
//    	
//    	//add visible ghosts to memory
//    	roundsSinceGhostMemoryRefreshed++;
//    	if(visibleGhostsList.size() > 0)
//    	{
//    		if(ghostMemory.length == 0)
//    			ghostMemory = new int[4];
//    		roundsSinceGhostMemoryRefreshed = 0;
//    		for(GHOST ghost : visibleGhostsList)
//    		{
//    			ghostMemory[getIndexOfGhost(ghost)] = game.getGhostCurrentNodeIndex(ghost);
//    		}
//    	}
//    	
//    	//reset memory
//    	if(roundsSinceGhostMemoryRefreshed > 25)
//    	{
//    		ghostMemory = new int[0];
//    	}
//    }
}