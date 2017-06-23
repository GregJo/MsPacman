package entrants.pacman.username;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

class StaticFunctions{
	static int[] convertIntegerListToArray(ArrayList<Integer> list)
	{
		int[] listAsArray = new int[list.size()];
		int index = 0;
		for(int i : list)
		{
			listAsArray[index] = i;
			index++;
		}
		return listAsArray;
	}
	static MOVE getMoveToNearestObject(Game game, int current, ArrayList<Integer> indicesOfObjectList)
	  {
		int[] indizesAsArray = convertIntegerListToArray(indicesOfObjectList);
		return getMoveToNearestObject(game, current, indizesAsArray);
	  }
	
	static MOVE getMoveToFurthestObject(Game game, int current, ArrayList<Integer> indicesOfObjectList)
	  {
		int[] indizesAsArray = convertIntegerListToArray(indicesOfObjectList);
		return getMoveToFurthestObject(game, current, indizesAsArray);
	  }
	
	  static MOVE getMoveToNearestObject(Game game, int current, int[] indicesOfObject)
	  {
		  int[] shortestPath = getShortestPathToNearestObject(game, current, indicesOfObject);
	    	if(shortestPath.length > 0)
	    	{
	    		return game.getNextMoveTowardsTarget(current,shortestPath[0],DM.PATH);
	    	}    	
	         return null;
	   }
	  
	  static MOVE getMoveToFurthestObject(Game game, int current, int[] indicesOfObject)
	  {
		  int[] longestPath = getPathToFurthestObject(game, current, indicesOfObject);
	    	if(longestPath.length > 0)
	    	{
	    		return game.getNextMoveTowardsTarget(current,longestPath[0],DM.PATH);
	    	}    	
	         return null;
	   }
	  
	  static int[] getShortestPathToNearestObject(Game game, int current, int[] indicesOfObject)
	  {
		  int[] shortestPath = new int[0];
	    	for(int objectIndex : indicesOfObject)
	    	{
	    		int[] path = game.getShortestPath(current, objectIndex);
	    		if(path.length < shortestPath.length || shortestPath.length == 0)
	    		{
	    			shortestPath = path;
	    		}
	    	}
	    	return shortestPath;
	  }
	  
	  static int[] getPathToFurthestObject(Game game, int current, int[] indicesOfObject)
	  {
		  int[] longestPath = new int[0];
	    	for(int objectIndex : indicesOfObject)
	    	{
	    		int[] path = game.getShortestPath(current, objectIndex);
	    		if(path.length > longestPath.length || longestPath.length == 0)
	    		{
	    			longestPath = path;
	    		}
	    	}
	    	return longestPath;
	  }
	  
	  static MOVE CornerRoutine(Game game, int current, MOVE[] possibleMovesArray)
	   {
		  ArrayList<MOVE> possibleMovesList = new ArrayList<MOVE>();
		  possibleMovesList.addAll(Arrays.asList(possibleMovesArray)); 
		  return  CornerRoutine(game, current, possibleMovesList);
	   }
	  static MOVE CornerRoutine(Game game, int current, ArrayList<MOVE> possibleMovesList)
	   {
		 if(game.isJunction(current))
			  return null;
	   if (!possibleMovesList.contains(game.getPacmanLastMoveMade())) 
	   {
	    MOVE cornerMove = null;
	    for (MOVE move : possibleMovesList) {
	     if (move != game.getPacmanLastMoveMade().opposite()) {
	      cornerMove = move;
	     }
	    }
	    return cornerMove;
	   }
	   return null;
	   }
	  static boolean isMovePossibleAtNode(Game game, int nodeIndex, MOVE move)
	  {
		  boolean found = false;
		  for( MOVE m : game.getPossibleMoves(nodeIndex))
		  {
			  if(m == move)
				  return true;
		  }
		  return false;
	  }
	  static MOVE getMoveFromPacmanPointOfView(Game game, MOVE relativeMove)
	  {
		 MOVE lastMove =  game.getPacmanLastMoveMade();
		 if(relativeMove == MOVE.UP)
			 return lastMove;
		 if(relativeMove == MOVE.DOWN)
			 return lastMove.opposite();
		 if(lastMove == MOVE.UP)
			 return relativeMove;
		 if(lastMove == MOVE.DOWN)
			 return relativeMove.opposite();
		 if(lastMove == MOVE.LEFT)
		 {
			 if(relativeMove == MOVE.LEFT)
				 return MOVE.DOWN;
			 if(relativeMove == MOVE.RIGHT)
				 return MOVE.UP;
		 }
		 if(lastMove == MOVE.RIGHT)
		 {
			 if(relativeMove == MOVE.LEFT)
				 return MOVE.UP;
			 if(relativeMove == MOVE.RIGHT)
				 return MOVE.DOWN;
		 }			 
		  return null;
	  }
	  
}
interface Strategy {
	default public MOVE getStrategyMove(Game game, int current, Memory memory)
	{
		MOVE move = _getStrategyMove(game, current, memory);
		updateMemoryBeforeReturn(game, current, memory);
		memory.lastStrategyUsed = getStrategyName();
		return move;
		
	};
	default public void updateMemoryBeforeReturn(Game game, int current, Memory memory)
	{
		
	}
	public MOVE _getStrategyMove(Game game, int current, Memory memory);
	public String getStrategyName();
	
}

class WaitStrategy implements Strategy
{
	public WaitStrategy(){}

	@Override
	/*@brief simulates waiting by going back and forth*/
	public MOVE _getStrategyMove(Game game, int current, Memory memory) {
		return game.getPacmanLastMoveMade().opposite(); //if no not possible move was found (= pacman is at fourway junction) just run back and forth
	}

	@Override
	public String getStrategyName() {
		return "Wait";
	}
}
class EatNearestPowerPillStrategy implements Strategy
{
	public EatNearestPowerPillStrategy(){}

	@Override
	/*@brief Goes to next powerpill.
	 * !Attention Currently expects that GHOST_DISTANCE_TO_POWERPILL State enum is used*/
	public MOVE _getStrategyMove(Game game, int current, Memory memory) {
		if(GHOST_DISTANCE_TO_POWERPILL.enumUsed && game.getNumberOfActivePowerPills() > 0)
		{
			return game.getNextMoveTowardsTarget(current,GHOST_DISTANCE_TO_POWERPILL.m_shortestPathPacmanToNextPowerPill[0],DM.PATH);
		}
		return null;
	}

	@Override
	public String getStrategyName() {
		return "EatNearestPowerPill";
	}
}
class EatGhostStrategy implements Strategy
{
	public EatGhostStrategy(){}

	@Override
	/*@brief Goes to next edible ghost.
	*/
	public MOVE _getStrategyMove(Game game, int current, Memory memory) {
		ArrayList<Integer> ghostPosList = new ArrayList<Integer>();
		 for(GHOST ghost : GHOST.values())
		   {
	  			if(game.getGhostEdibleTime(ghost) > 0)
	  			{
	  				ghostPosList.add(game.getGhostCurrentNodeIndex(ghost));
	  			}
	  		}
		 return StaticFunctions.getMoveToNearestObject(game, current, ghostPosList);
	}

	@Override
	public String getStrategyName() {
		return "EatGhost";
	}
}
class EatNearestAvailablePillStrategy implements Strategy
{
	public EatNearestAvailablePillStrategy(){}

	@Override
	/*@brief Goes to next edible ghost.
	*/
	public MOVE _getStrategyMove(Game game, int current, Memory memory) 
	{
		return StaticFunctions.getMoveToNearestObject(game, current, memory.getStillAvailablePills());
	}

	@Override
	public String getStrategyName() {
		return "EatNearestAvailablePill";
	}
}
class EatFurthestAwayPowerPill implements Strategy
{
	public EatFurthestAwayPowerPill(){}

	@Override
	/*@brief Goes to furthest edible powerPill.
	*/
	public MOVE _getStrategyMove(Game game, int current, Memory memory) 
	{
		//check if pill still exists
		if(pillPosLastTime != -1)
		{
			boolean found = false;
			for(int pill : memory.getStillAvailablePowerPills())
			{
				
				if(pill == pillPosLastTime)
				{
					found = true;
					break;
				}
			}
			if(found == false)
				pillPosLastTime = -1;
		}
		
		//pacman didnt change strategies and already chose a pill to eat
		if(memory.lastStrategyUsed.equals(getStrategyName()) && pillPosLastTime  != -1)
		{
			int[] index = new int[1];
			index[0] = pillPosLastTime;
			return StaticFunctions.getMoveToNearestObject(game, current, index);
		}
		int[] indizesAsArray = StaticFunctions.convertIntegerListToArray(memory.getStillAvailablePowerPills());
		int[] longestPath = StaticFunctions.getPathToFurthestObject(game, current, indizesAsArray);
		if(longestPath.length > 0)
    	{
			pillPosLastTime = longestPath[longestPath.length - 1];
    		return game.getNextMoveTowardsTarget(current,longestPath[0],DM.PATH);
    	}    	
         return null;
	}

	@Override
	public String getStrategyName() {
		return "EatFurthestAwayPowerPill";
	}
	private static int pillPosLastTime = -1;
}
class EatFurthestAwayPill implements Strategy
{
	public EatFurthestAwayPill(){}

	@Override
	/*@brief Goes to furthest edible pill.
	*/
	public MOVE _getStrategyMove(Game game, int current, Memory memory) 
	{
		//check if pill still exists
		if(pillPosLastTime != -1)
		{
			boolean found = false;
			for(int pill : memory.getStillAvailablePills())
			{
				
				if(pill == pillPosLastTime)
				{
					found = true;
					break;
				}
			}
			if(found == false)
				pillPosLastTime = -1;
		}
		
		//pacman didnt change strategies and already chose a pill to eat
		if(memory.lastStrategyUsed.equals(getStrategyName()) && pillPosLastTime  != -1)
		{
			int[] index = new int[1];
			index[0] = pillPosLastTime;
			return StaticFunctions.getMoveToNearestObject(game, current, index);
		}
		int[] indizesAsArray = StaticFunctions.convertIntegerListToArray(memory.getStillAvailablePills());
		int[] longestPath = StaticFunctions.getPathToFurthestObject(game, current, indizesAsArray);
		if(longestPath.length > 0)
    	{
			pillPosLastTime = longestPath[longestPath.length - 1];
    		return game.getNextMoveTowardsTarget(current,longestPath[0],DM.PATH);
    	}    	
         return null;
	}

	@Override
	public String getStrategyName() {
		return "EatFurthestAwayPill";
	}
	private static int pillPosLastTime = -1;
}
class RunCircle implements Strategy
{
	public RunCircle(){}

	@Override
	/*@brief Goes to furthest edible pill.
	*/
	public MOVE _getStrategyMove(Game game, int current, Memory memory) 
	{

		//pacman didnt change strategies and already chose to run a circle
		if(memory.lastStrategyUsed.equals(getStrategyName()))
		{
			MOVE cornerMove = StaticFunctions.CornerRoutine(game, current, game.getPossibleMoves(current));
			if(cornerMove != null)
			 return cornerMove;
			if(game.isJunction(current))
				return StaticFunctions.getMoveFromPacmanPointOfView(game, moveLastTime);
			return game.getPacmanLastMoveMade();
		}
			
		//first select random direction
		// then follow that direction until a wild junction appears
		// At the junction decide if pacman wants to run in clockwise or counterclowise direction
		 int moveNumber = rand.nextInt(game.getPossibleMoves(current).length);
		 MOVE direction = game.getPossibleMoves(current)[moveNumber];
		 int simulatedCurrent =  game.getNeighbour(current, direction);
		 while(!game.isJunction(simulatedCurrent))
		 {
			MOVE cornerMove = StaticFunctions.CornerRoutine(game, simulatedCurrent, game.getPossibleMoves(simulatedCurrent));
			if(cornerMove != null)
			{
				simulatedCurrent = game.getNeighbour(simulatedCurrent, cornerMove);
				continue;
			}
			simulatedCurrent = game.getNeighbour(simulatedCurrent, direction);	
		 }
		 int clockWiseDirection = rand.nextInt(2);
		 moveLastTime = (clockWiseDirection == 0) ? MOVE.LEFT : MOVE.RIGHT;
		
		 
		 //check if planned move is even possible
		 if(!StaticFunctions.isMovePossibleAtNode(game, simulatedCurrent, moveLastTime))
			 moveLastTime = moveLastTime.opposite();
		 return direction;
	}

	@Override
	public String getStrategyName() {
		return "RunCircle";
	}
	private static MOVE moveLastTime = null;
	private Random rand  = new Random();
}