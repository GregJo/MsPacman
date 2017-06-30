package entrants.pacman.username;

import java.util.ArrayList;
import java.util.Arrays;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

public class StaticFunctions{
	//static ArrayList<MOVE> moveArray = new ArrayList<MOVE>();
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
	  static MOVE CornerRoutine(Game game, int current, ArrayList<MOVE> possibleMovesList)
	  {
		  return CornerRoutine(game, current, possibleMovesList, game.getPacmanLastMoveMade());
	  }
	  static MOVE CornerRoutine(Game game, int current, MOVE[] possibleMovesArray)
	   {
		  ArrayList<MOVE> possibleMovesList = new ArrayList<MOVE>();
		  possibleMovesList.addAll(Arrays.asList(possibleMovesArray)); 
		  return  CornerRoutine(game, current, possibleMovesList, game.getPacmanLastMoveMade());
	   }
	  static MOVE CornerRoutine(Game game, int current, MOVE[] possibleMovesArray, MOVE lastMove)
	   {
		  ArrayList<MOVE> possibleMovesList = new ArrayList<MOVE>();
		  possibleMovesList.addAll(Arrays.asList(possibleMovesArray)); 
		  return  CornerRoutine(game, current, possibleMovesList, lastMove);
	   }
	 
	  static MOVE CornerRoutine(Game game, int current, ArrayList<MOVE> possibleMovesList, MOVE lastMove)
	   {
		 if(game.isJunction(current))
			  return null;
	   if (!possibleMovesList.contains(lastMove)) 
	   {
	    MOVE cornerMove = null;
	    for (MOVE move : possibleMovesList) {
	     if (move != lastMove.opposite()) {
	      cornerMove = move;
	     }
	    }
	    return cornerMove;
	   }
	   return null;
	   }
	  static boolean isMovePossibleAtNode(Game game, int nodeIndex, MOVE move)
	  {
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
