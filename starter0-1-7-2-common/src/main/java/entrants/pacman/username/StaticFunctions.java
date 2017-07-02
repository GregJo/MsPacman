package entrants.pacman.username;

import java.util.ArrayList;
import java.util.Arrays;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

/*Class that holds all kinds of helper functions*/
public class StaticFunctions{
	/*@brief converts an ArrayList<Integer> to int[]
	 * @param list the list to convert
	 * @returns the converted list*/
	public static int[] convertIntegerListToArray(ArrayList<Integer> list)
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
	
	/*@brief Gets the next move that is necessary to move to the nearest objects of all given object positions.
	 * @param game The current Game
	 * @param current the current position of PacMan/Ghosts
	 * @param indicesOfObjectList positions of objects that should be used. 
	 * @returns the next move that is necessary to move to the nearest objects of all given object positions in indicesOfObjectList*/
	public static MOVE getMoveToNearestObject(Game game, int current, ArrayList<Integer> indicesOfObjectList)
	  {
		int[] indizesAsArray = convertIntegerListToArray(indicesOfObjectList);
		return getMoveToNearestObject(game, current, indizesAsArray);
	  }
	
	/*@brief Gets the next move that is necessary to move to the farthest objects of all given object positions.
	 * @param game The current Game
	 * @param current the current position of PacMan/Ghosts
	 * @param indicesOfObjectList positions of objects that should be used. 
	 * @returns the next move that is necessary to move to the farthest objects of all given object positions in indicesOfObjectList*/
	public static MOVE getMoveToFurthestObject(Game game, int current, ArrayList<Integer> indicesOfObjectList)
	  {
		int[] indizesAsArray = convertIntegerListToArray(indicesOfObjectList);
		return getMoveToFurthestObject(game, current, indizesAsArray);
	  }
	
	/*@brief Gets the next move that is necessary to move to the nearest objects of all given object positions.
	 * @param game The current Game
	 * @param current the current position of PacMan/Ghosts
	 * @param indicesOfObjectList positions of objects that should be used. 
	 * @returns the next move that is necessary to move to the nearest objects of all given object positions in indicesOfObjectList*/
	public static MOVE getMoveToNearestObject(Game game, int current, int[] indicesOfObject)
	  {
		  int[] shortestPath = getShortestPathToNearestObject(game, current, indicesOfObject);
	    	if(shortestPath.length > 0)
	    	{
	    		return game.getNextMoveTowardsTarget(current,shortestPath[0],DM.PATH);
	    	}    	
	         return null;
	   }
	  
	/*@brief Gets the next move that is necessary to move to the farthest objects of all given object positions.
	 * @param game The current Game
	 * @param current the current position of PacMan/Ghosts
	 * @param indicesOfObjectList positions of objects that should be used. 
	 * @returns the next move that is necessary to move to the farthest objects of all given object positions in indicesOfObjectList*/
	  public static MOVE getMoveToFurthestObject(Game game, int current, int[] indicesOfObject)
	  {
		  int[] longestPath = getPathToFurthestObject(game, current, indicesOfObject);
	    	if(longestPath.length > 0)
	    	{
	    		return game.getNextMoveTowardsTarget(current,longestPath[0],DM.PATH);
	    	}    	
	         return null;
	   }
	  /*@brief Gets the shortest path to the nearest objects of all given object positions.
		 * @param game The current Game
		 * @param current the current position of PacMan/Ghosts
		 * @param indicesOfObjectList positions of objects that should be used. 
		 * @returns the shortest path to the nearest objects of all given object positions in indicesOfObjectList*/
	  public static int[] getShortestPathToNearestObject(Game game, int current, int[] indicesOfObject)
	  {
		  int[] shortestPath = new int[0];
	    
		  	int[] path;
	    	for(int objectIndex : indicesOfObject)
	    	{
	    		path = game.getShortestPath(current, objectIndex);
	    		if(path.length < shortestPath.length || shortestPath.length == 0)
	    		{
	    			if(path.length > 0)
	    				shortestPath = path;
	    		}
	    	}
	    
	    	return shortestPath;
	  }
	  
	  /*@brief Gets the shortest path to the farthest objects of all given object positions.
		 * @param game The current Game
		 * @param current the current position of PacMan/Ghosts
		 * @param indicesOfObjectList positions of objects that should be used. 
		 * @returns the shortest path to the farthest objects of all given object positions in indicesOfObjectList*/
	  public static int[] getPathToFurthestObject(Game game, int current, int[] indicesOfObject)
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
	  
	  /*@brief Gets the move that should be made if current position is at a corner.
		 * @param game The current Game
		 * @param current the current position of PacMan/Ghosts
		 * @param possibleMovesList list of possible moves that should be used 
		 * @returns the move that should be made if current position is at a corner or null if not a corner*/
	  public static MOVE CornerRoutine(Game game, int current, ArrayList<MOVE> possibleMovesList)
	  {
		  return CornerRoutine(game, current, possibleMovesList, game.getPacmanLastMoveMade());
	  }
	  
	  /*@brief Gets the move that should be made if current position is at a corner.
		 * @param game The current Game
		 * @param current the current position of PacMan/Ghosts
		 * @param possibleMovesList list of possible moves that should be used 
		 * @returns the move that should be made if current position is at a corner or null if not a corner*/
	  public static MOVE CornerRoutine(Game game, int current, MOVE[] possibleMovesArray)
	   {
		  ArrayList<MOVE> possibleMovesList = new ArrayList<MOVE>();
		  possibleMovesList.addAll(Arrays.asList(possibleMovesArray)); 
		  return  CornerRoutine(game, current, possibleMovesList, game.getPacmanLastMoveMade());
	   }
	  
	  /*@brief Gets the move that should be made if current position is at a corner.
		 * @param game The current Game
		 * @param current the current position of PacMan/Ghosts
		 * @param possibleMovesList list of possible moves that should be used 
		 * @param lastMove the last move that was made. Useful for simulations.
		 * @returns the move that should be made if current position is at a corner or null if not a corner*/
	  public static MOVE CornerRoutine(Game game, int current, MOVE[] possibleMovesArray, MOVE lastMove)
	   {
		  ArrayList<MOVE> possibleMovesList = new ArrayList<MOVE>();
		  possibleMovesList.addAll(Arrays.asList(possibleMovesArray)); 
		  return  CornerRoutine(game, current, possibleMovesList, lastMove);
	   }
	 
	  /*@brief Gets the move that should be made if current position is at a corner.
		 * @param game The current Game
		 * @param current the current position of PacMan/Ghosts
		 * @param possibleMovesList list of possible moves that should be used 
		 * @param lastMove the last move that was made. Useful for simulations.
		 * @returns the move that should be made if current position is at a corner or null if not a corner*/
	  public static MOVE CornerRoutine(Game game, int current, ArrayList<MOVE> possibleMovesList, MOVE lastMove)
	   {
		 if(game.isJunction(current))//can't be corner if it's a junction (for T-junctions)
			  return null;
	   if (!possibleMovesList.contains(lastMove))  //if last move is not possible it should be a corner 
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
	  
	  /*@brief Checks whether a specific move is possible at a position or not
	   * @param game The current Game
	   * @param nodeIndex the position that should be checked
	   * @param move the specific move that should be checked  
	   * @returns true if move is possible at position nodeIndex, else false.*/
	  public static boolean isMovePossibleAtNode(Game game, int nodeIndex, MOVE move)
	  {
		  for( MOVE m : game.getPossibleMoves(nodeIndex))
		  {
			  if(m == move)
				  return true;
		  }
		  return false;
	  }
	  
	  /*@brief Returns a move in a direction out of the players (PacMan or Ghosts) point of view 
	   * @param game The current Game
	   * @param nodeIndex the position that should be checked
	   * @param move the specific move that should be checked  
	   * @returns true if move is possible at position nodeIndex, else false.*/
	  public static MOVE getMoveFromPacmanPointOfView(Game game, MOVE relativeMove)
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
	  
	  public static MOVE getMoveFromPacmanPointOfView(Game game, MOVE relativeMove, MOVE lastMove)
	  {
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