package entrants.pacman.username;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import static pacman.game.Constants.EDIBLE_TIME;
import static pacman.game.Constants.EDIBLE_TIME_REDUCTION;
import static pacman.game.Constants.LEVEL_RESET_REDUCTION;

import java.util.ArrayList;

import pacman.game.Game;

interface stateEnum {
	public String getCurrentStateString(Game game, int current, Memory memory);
}
////////////////////ENUMS DESCRIBING THE GLOBAL STATE ///////////////////////////////////
enum POWERPILLS_LEFT implements stateEnum{
	   noPowerPillLeft, powerPillsLeft;
	   public String getCurrentStateString(Game game, int current, Memory memory)
	   {
		   ArrayList<Integer> powerPills =  memory.getStillAvailablePowerPills();
		   String returnValue;
		   switch (powerPills.size()) 
			 {
			 case 0:  returnValue = noPowerPillLeft.name();
             		  break;
	         default:  returnValue =  powerPillsLeft.name();
	                  break;
			 }
		   return returnValue;
	   }
}


////////////////////ENUMS DESCRIBING THE LOCAL ENVIROMENT ///////////////////////////////////
enum KIND_OF_LEVEL_TILE implements stateEnum{
	   deadEnd, hallWay,threeWayJunction,fourWayJunction;
	   public String getCurrentStateString(Game game, int current, Memory memory){
		   int moveCounter = 0;
		   for(MOVE move : game.getPossibleMoves(current))
		   {
	   			moveCounter++;
	   		}
		   if(moveCounter == 1)
			   return deadEnd.name();
		   if(moveCounter == 2)
			   return hallWay.name();
		   if(moveCounter == 3)
			   return threeWayJunction.name();
		   if(moveCounter == 4)
			   return fourWayJunction.name();
		   return deadEnd.name();
		  }
}
////////////////////ENUMS DESCRIBING WHAT MS.PACMAN SEES ///////////////////////////////////
enum NUMBER_SEEN_GHOSTS implements stateEnum{
	   seeingNoGhost, seeingOneGhost, seeingTwoGhost, seeingThreeGhost, seeingFourGhost;
	 public static int ghostCounter = 0;
	 public String getCurrentStateString(Game game, int current, Memory memory){
		 ghostCounter = 0;
		   for(GHOST ghost : GHOST.values())
		   {
	  			if(game.getGhostCurrentNodeIndex(ghost) != -1)
	  			{
	  				ghostCounter++;
	  			}
	  		}
		   if(ghostCounter == 0)
			   return NUMBER_SEEN_GHOSTS.seeingNoGhost.name();
		   if(ghostCounter == 1)
			   return NUMBER_SEEN_GHOSTS.seeingOneGhost.name();
		   if(ghostCounter == 2)
			   return NUMBER_SEEN_GHOSTS.seeingTwoGhost.name();
		   if(ghostCounter == 3)
			   return NUMBER_SEEN_GHOSTS.seeingThreeGhost.name();
		   if(ghostCounter == 4)
			   return NUMBER_SEEN_GHOSTS.seeingFourGhost.name();
		   return NUMBER_SEEN_GHOSTS.seeingNoGhost.name();
	  }
}
enum NUMBER_SEEN_EDIBLE_GHOSTS implements stateEnum{
	   seeingNoEdibleGhost, seeingOneEdibleGhost, seeingTwoEdibleGhosts, seeingThreeEdibleGhosts, seeingFourEdibleGhosts;
	 public String getCurrentStateString(Game game, int current, Memory memory){
		  int ghostCounter = 0;
		   for(GHOST ghost : GHOST.values())
		   {
	  			if(game.getGhostEdibleTime(ghost) > 0)
	  			{
	  				ghostCounter++;
	  			}
	  		}
		   	 String returnValue;
			 switch (ghostCounter) 
			 {
	         case 0:  returnValue = seeingNoEdibleGhost.name();
             		  break;
	         case 1:  returnValue = seeingOneEdibleGhost.name();
	                  break;
	         case 2:   returnValue =  seeingTwoEdibleGhosts.name();
	                  break;
	         case 3:  returnValue =  seeingThreeEdibleGhosts.name();
	                  break;
	         default:  returnValue =  seeingFourEdibleGhosts.name();
	                  break;
			 }
			 return returnValue; 
	  }
}
////////////////////ENUMS DESCRIBING RELATIVE DISTANCES///////////////////////////////////
enum GHOST_DISTANCE_TO_POWERPILL implements stateEnum{
	   ghostNearerToPowerPill, pacmanNearerToPowerPill;
	static int[] m_shortestPathPacmanToNextPowerPill; //since we compute the path we can just as well save it in case the strategies need it.
	static int[] m_shortestPathGhostToNextPowerPill; //since we compute the path we can just as well save it in case the strategies need it.
	static boolean enumUsed = false;
	
	 public String getCurrentStateString(Game game, int current, Memory memory)
	 {
		 //find visible ghosts into list
		ArrayList<Integer> positionGhosts = new ArrayList<Integer>();
		 for(GHOST ghost : GHOST.values())
		   {
	  			if(game.getGhostCurrentNodeIndex(ghost) != -1)
	  			{
	  				positionGhosts.add(game.getGhostCurrentNodeIndex(ghost));
	  			}
	  		}
		 
		 //get shortest path to next powerpill for pacman and one of the visible Ghosts 
		 ArrayList<Integer> powerPills =  memory.getStillAvailablePowerPills();
		 
	    int[]  shortestPathPacman = StaticFunctions.getShortestPathToNearestObject(game, current, StaticFunctions.convertIntegerListToArray(memory.getStillAvailablePowerPills()));
	    int[]  shortestPathGhost = StaticFunctions.getShortestPathToNearestObject(game, current, StaticFunctions.convertIntegerListToArray(positionGhosts));
	    
	    //save path in case strategies can use them
	    enumUsed = true;
	    m_shortestPathPacmanToNextPowerPill = shortestPathPacman;
	    m_shortestPathGhostToNextPowerPill = shortestPathGhost;
	    
		 if(positionGhosts.size() == 0) //no visible ghosts right now
			 return pacmanNearerToPowerPill.name();
	    
	    //return whether pacman is nearer to powerpill than Ghost or not
	    if(shortestPathGhost.length < shortestPathPacman.length)
	    	return ghostNearerToPowerPill.name();
	    else
	    	return pacmanNearerToPowerPill.name();
	 }
}

////////////////////ENUMS DESCRIBING STATE OF MS.PACMAN ///////////////////////////////////
enum POWER_PILL_ACTIVATED implements stateEnum{
	   powerPillActive, powerPillNotActive;
	 static int powerPillTime = 0;
	 public String getCurrentStateString(Game game, int current, Memory memory)
	 {
		 if(game.wasPacManEaten())
			 powerPillTime = 0;
		 if(powerPillTime > 0)
	    		powerPillTime--;
	    	
	    if(game.wasPowerPillEaten())
	    {
	    	int level = game.getCurrentLevel();
	       	powerPillTime = (int) (EDIBLE_TIME * (Math.pow(EDIBLE_TIME_REDUCTION, level % LEVEL_RESET_REDUCTION)));
	    }
	    return (powerPillTime > 0) ? POWER_PILL_ACTIVATED.powerPillActive.name() : POWER_PILL_ACTIVATED.powerPillNotActive.name();
	    	  
	 }
}
enum LIVES_LEFT implements stateEnum{
	   oneLiveLeft, twoLivesLeft, threeLivesLeft;
	
	 public String getCurrentStateString(Game game, int current, Memory memory)
	 {
		 int lives = game.getPacmanNumberOfLivesRemaining();
		 String returnValue;
		 switch (lives) 
		 {
         case 1:  returnValue = oneLiveLeft.name();
                  break;
         case 2:   returnValue =  twoLivesLeft.name();
                  break;
         case 3:  returnValue =  threeLivesLeft.name();
                  break;
         default:  returnValue =  oneLiveLeft.name();
                  break;
		 }
		 return returnValue;
	 }
}

