package entrants.pacman.username;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import entrants.ghosts.username.GhostMemory;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

class WaitStrategy implements Strategy
{
	public WaitStrategy(){}

	@Override
	/*@brief simulates waiting by going back and forth*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		return game.getPacmanLastMoveMade().opposite(); //if no not possible move was found (= pacman is at fourway junction) just run back and forth
	}

	@Override
	public String getStrategyName() {
		return "Wait";
	}

	@Override
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
class EatNearestPowerPillStrategy implements Strategy
{
	public EatNearestPowerPillStrategy(){}

	@Override
	/*@brief Goes to next powerpill.
	 * !Attention Currently expects that GHOST_DISTANCE_TO_POWERPILL State enum is used*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		
		
		if(GHOST_DISTANCE_TO_POWERPILL.enumUsed == false && memory.getStillAvailablePowerPills().size() > 0)
		{
			 ArrayList<Integer> powerPills =  memory.getStillAvailablePowerPills();
			return StaticFunctions.getMoveToNearestObject(game, current, StaticFunctions.convertIntegerListToArray(memory.getStillAvailablePowerPills()));
			
		}
		if(GHOST_DISTANCE_TO_POWERPILL.enumUsed && memory.getStillAvailablePowerPills().size() > 0)
		{
			return game.getNextMoveTowardsTarget(current,GHOST_DISTANCE_TO_POWERPILL.m_shortestPathPacmanToNextPowerPill[0],DM.PATH);
		}
		return null;
	}
	
	@Override
	public boolean requirementsMet(Game game, int current, PacManMemory memory)
	{
		ArrayList<Integer> powerPills =  memory.getStillAvailablePowerPills();
		return (powerPills.size() == 0) ? false : true;
	}

	@Override
	public String getStrategyName() {
		return "EatNearestPowerPill";
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
class EatGhostStrategy implements Strategy
{
	public EatGhostStrategy(){}

	@Override
	/*@brief Goes to next edible ghost.
	*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
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
	
	@Override
	public boolean requirementsMet(Game game, int current, PacManMemory memory)
	{
		int ghostCounter = 0;
	    for(GHOST ghost : GHOST.values())
	    {
	    	if(game.getGhostEdibleTime(ghost) > 0)
			{
				ghostCounter++;
			}
		}
	    return (ghostCounter == 0) ? false : true;
	}

	@Override
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
class EatNearestAvailablePillStrategy implements Strategy
{
	public EatNearestAvailablePillStrategy(){}

	@Override
	/*@brief Goes to next edible ghost.
	*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) 
	{
		return StaticFunctions.getMoveToNearestObject(game, current, memory.getStillAvailablePills());
	}

	@Override
	public String getStrategyName() {
		return "EatNearestAvailablePill";
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}

}

class GetRidOfGhost implements Strategy
{

	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		MOVE move = null;
		
		ArrayList<Integer> ghostPosList = memory.getLastKnownGhostPositions(game);
		
		ArrayList<MOVE> possibleMovesList = new ArrayList<>();
		possibleMovesList.addAll(Arrays.asList(game.getPossibleMoves(current)));
		
		move = StaticFunctions.CornerRoutine(game, current, possibleMovesList);
		if(move == null)
		{
			if(game.isJunction(current)){
				Random rand = new Random();
				possibleMovesList.remove(game.getPacmanLastMoveMade().opposite());
				if (NUMBER_SEEN_GHOSTS.ghostCounter > 1)
					possibleMovesList.remove(game.getPacmanLastMoveMade());
				move = possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
			}else 
			{
				if (NUMBER_SEEN_GHOSTS.ghostCounter > 0) {
					MOVE moveTowardsGhost = StaticFunctions.getMoveToNearestObject(game, current, ghostPosList);
					if (moveTowardsGhost != null) {
						if (possibleMovesList.contains(moveTowardsGhost.opposite()))
							move = moveTowardsGhost.opposite();
					}

				} else
					move = game.getPacmanLastMoveMade();
			}
		}
		return move;
	}
	
	@Override
	public boolean requirementsMet(Game game, int current, PacManMemory memory)
	{
		MOVE move = _getStrategyMove(game, current, memory);
		return (move == null) ? false : true;
	}
	
	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "GetRidOffGhost";
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

class RunFromNearestGhost implements Strategy
{
	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) 
	{
		MOVE move = null;
		
		ArrayList<Integer> ghostPosList = memory.getLastKnownGhostPositions(game);
		
		ArrayList<MOVE> possibleMovesList = new ArrayList<>();
		possibleMovesList.addAll(Arrays.asList(game.getPossibleMoves(current)));
		
		move = StaticFunctions.CornerRoutine(game, current, possibleMovesList);
		
		if(move == null)
		{
			if (NUMBER_SEEN_GHOSTS.ghostCounter != 0) {
				MOVE moveTowardsGhost = StaticFunctions.getMoveToNearestObject(game, current, ghostPosList);
				if (moveTowardsGhost != null) {
					if (possibleMovesList.contains(moveTowardsGhost.opposite()))
						move = moveTowardsGhost.opposite();
				}
			} 
			if(game.isJunction(current) && move == null){
				Random rand = new Random();
				possibleMovesList.remove(game.getPacmanLastMoveMade().opposite());
				move = possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
			} else if(possibleMovesList.contains(game.getPacmanLastMoveMade()))
				move = game.getPacmanLastMoveMade();
		}
		return move;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "RunFromNearestGhost";
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}

class RunTowardsNearestKnownGhost implements Strategy
{
	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		MOVE move = null;
		
		ArrayList<Integer> ghostPosList = memory.getLastKnownGhostPositions(game);
		
		ArrayList<MOVE> possibleMovesList = new ArrayList<>();
		possibleMovesList.addAll(Arrays.asList(game.getPossibleMoves(current)));
		
		if (NUMBER_SEEN_GHOSTS.ghostCounter != 0) 
		{
			move = StaticFunctions.getMoveToNearestObject(game, current, ghostPosList);
		}
		
		
		return move;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "RunTowardsNearestKnownGhost";
	}

	@Override
	public boolean requirementsMet(Game game, int current, PacManMemory memory)
	{
	    return (NUMBER_SEEN_GHOSTS.ghostCounter != 0) ? true : false;
	}
	
	@Override
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}

class RandomPatrolInRadiusAroundCenter implements Strategy
{
	private int center = Integer.MIN_VALUE;
	private final int RADIUS = 30;
	private int radius = RADIUS;
	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		if (center == Integer.MIN_VALUE) {
			center = current;
		}
		if(center==current)
		{
			radius=RADIUS;
		}
		
		MOVE move = null;
		
		ArrayList<MOVE> possibleMovesList = new ArrayList<>();
		possibleMovesList.addAll(Arrays.asList(game.getPossibleMoves(current)));
		
		//if(game.getShortestPathDistance(current, center) < radius)
		if(game.getEuclideanDistance(current, center) < radius)
		{
			move = StaticFunctions.CornerRoutine(game, current, possibleMovesList);
			//System.out.println("Move: " + move.name());
			if (move == null) {
				if(game.isJunction(current)){
					Random rand = new Random();
					possibleMovesList.remove(game.getPacmanLastMoveMade().opposite());
					move = possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
					//System.out.println("Move: " + move.name());
				} else if(possibleMovesList.contains(game.getPacmanLastMoveMade())){
					move = game.getPacmanLastMoveMade();
					//System.out.println("Move: " + move.name());
				}
			}
		} else
		{
			radius = 0;
			move = game.getNextMoveTowardsTarget(current, center, DM.PATH);
		}
		
		return move;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "RandomPatrolInRadiusAroundCenter";
	}

	@Override
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

class EatFurthestAwayPowerPill implements Strategy
{
	public EatFurthestAwayPowerPill(){}

	@Override
	/*@brief Goes to furthest edible powerPill.
	*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) 
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
	public boolean requirementsMet(Game game, int current, PacManMemory memory)
	{
		ArrayList<Integer> powerPills =  memory.getStillAvailablePowerPills();
		return (powerPills.size() == 0) ? false : true;
	}
	
	@Override
	public String getStrategyName() {
		return "EatFurthestAwayPowerPill";
	}
	private int pillPosLastTime = -1;
	@Override
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
class EatFurthestAwayPill implements Strategy
{
	public EatFurthestAwayPill(){}

	@Override
	/*@brief Goes to furthest edible pill.
	*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) 
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
	private int pillPosLastTime = -1;
	@Override
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
class RunCircle implements Strategy
{
	public RunCircle(){}

	@Override
	/*@brief Goes to furthest edible pill.
	*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) 
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
		 MOVE initialDirection = game.getPossibleMoves(current)[moveNumber];
		 MOVE direction = initialDirection;
		 int simulatedCurrent =  game.getNeighbour(current, direction);
		 while(!game.isJunction(simulatedCurrent))
		 {
			MOVE cornerMove = StaticFunctions.CornerRoutine(game, simulatedCurrent, game.getPossibleMoves(simulatedCurrent), direction);
			if(cornerMove != null)
			{
				direction = cornerMove;
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
		 return initialDirection;
	}

	@Override
	public String getStrategyName() {
		return "RunCircle";
	}
	private MOVE moveLastTime = null;
	private Random rand  = new Random();
	@Override
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}