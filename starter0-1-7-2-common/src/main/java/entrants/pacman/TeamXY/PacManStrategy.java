package entrants.pacman.TeamXY;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import entrants.ghosts.TeamXY.GhostMemory;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/*Strategy for staying at the same position*/
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
	/*@brief returns the initial probability that will be used on initialization of PacMan*/
	public double getStrategyInitialProbability() {
		// TODO Auto-generated method stub
		return 0;// 0 because this strategy greatly reduces PacMans fitness and we are not even sure it has any benefits
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
/*Strategy to eat the nearest still available PowerPill.
 *Positions of still available Power Pills are stored in the memory.*/
class EatNearestPowerPillStrategy implements Strategy
{
	public EatNearestPowerPillStrategy(){}

	@Override
	/*@brief Goes to next powerpill.*/
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		
		
		if(GHOST_DISTANCE_TO_POWERPILL.enumUsed == false && memory.getStillAvailablePowerPills().size() > 0)
		{
			 ArrayList<Integer> powerPills =  memory.getStillAvailablePowerPills();
			return StaticFunctions.getMoveToNearestObject(game, current, StaticFunctions.convertIntegerListToArray(memory.getStillAvailablePowerPills()));
			
		}
		//if GHOST_DISTANCE_TO_POWERPILL enum is used, reuse the cached path
		if(GHOST_DISTANCE_TO_POWERPILL.enumUsed && memory.getStillAvailablePowerPills().size() > 0)
		{
			return game.getNextMoveTowardsTarget(current,GHOST_DISTANCE_TO_POWERPILL.m_shortestPathPacmanToNextPowerPill[0],DM.PATH);
		}
		return null;
	}
	
	@Override
	/*@brief Checks whether there are any power pills left.
	 * @returns true if there are power pills left, else false.
	 * */
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
/*Strategy to eat currently visible ghosts*/
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
	/*@brief Checks if there are visible and edible ghost
	 * @returns true if there visible and edible ghosts, else false.
	 * */
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
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
/*Strategy to eat nearest still available pill. 
 * Positions of still available pills are stored in the memory.
 * */
class EatNearestAvailablePillStrategy implements Strategy
{
	public EatNearestAvailablePillStrategy(){}

	@Override
	/*@brief Goes to nearest pill.
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

/*Strategy to run away from ghosts by changing directions at junctions as fast as possible.*/
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
		if(move == null) //not a corner
		{
			if(game.isJunction(current)){
				Random rand = new Random();
				possibleMovesList.remove(game.getPacmanLastMoveMade().opposite()); //don't run back
				if (NUMBER_SEEN_GHOSTS.ghostCounter > 1) //if at least 2 ghosts, change direction as fast as possible
					possibleMovesList.remove(game.getPacmanLastMoveMade());
				move = possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
			}else 
			{
				if (NUMBER_SEEN_GHOSTS.ghostCounter > 0) { //run in opposite direction of ghost if there is only one ghost
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
	/*
	 *@brief Checks if this strategy would return null. This can happen on T-Junctions.
	 *@returns false if this strategy would return null, else true.*/
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

/*Another strategy to run away from ghosts
 * */
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
		
		if(move == null)// not a corner
		{
			if (NUMBER_SEEN_GHOSTS.ghostCounter != 0) {//seeing at least one ghost
				MOVE moveTowardsGhost = StaticFunctions.getMoveToNearestObject(game, current, ghostPosList);
				if (moveTowardsGhost != null) {
					if (possibleMovesList.contains(moveTowardsGhost.opposite()))//run in opposite direction of ghost if possible
						move = moveTowardsGhost.opposite();
				}
			} 
			if(game.isJunction(current) && move == null){ // if junction and still no move found (on T-Junctions for example)
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

/*Strategy to run towards a ghost. If no ghost is visible, run towards a ghost from memory.*/
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
	/*@brief checks if there are currently any visible and edible ghosts.
	 * This strategy won't be used if there are no visible and edible ghosts, because
	 * after training it caused the PacMans to simply run into not edible ghosts.
	 * @returns true if there are visible and edible ghosts, else false.*/
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
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
/*
 * Strategy to patrol around a center point. After reaching a point too far from the center point, return to the center point and start anew.
 * */
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
		
		if(game.getEuclideanDistance(current, center) < radius) //still not too far from center point
		{
			move = StaticFunctions.CornerRoutine(game, current, possibleMovesList);
			if (move == null) {
				if(game.isJunction(current)){
					Random rand = new Random();
					possibleMovesList.remove(game.getPacmanLastMoveMade().opposite());
					move = possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
				} else if(possibleMovesList.contains(game.getPacmanLastMoveMade())){
					move = game.getPacmanLastMoveMade();
				}
			}
		} else
		{ // too far from center point -> return to center
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

/*Strategy to got to furthest away power pill. This might be a good idea to run away while getting near another power pill.*/
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
	/*@brief Checks if there are still power pills left.
	 * This stratgey won't be used if no power pills are left.
	 * @returns true if power pills are left, else false*/
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

/*Strategy to got to furthest away pill. This might be a good idea to run away while getting near another pill.*/
class EatFurthestAwayPill implements Strategy
{
	public EatFurthestAwayPill(){}

	@Override
	/*@brief Goes to farthest edible pill.
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
		return 0; //usually not used, pacman should learn when to use this
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
}
/*strategy to run around an object*/
class RunCircle implements Strategy
{
	public RunCircle(){}

	@Override
	/*@brief Runs around an object.
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