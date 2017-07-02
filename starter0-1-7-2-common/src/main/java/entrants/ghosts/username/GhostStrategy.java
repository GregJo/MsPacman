package entrants.ghosts.username;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import entrants.pacman.username.PacManMemory;
import entrants.pacman.username.StaticFunctions;
import entrants.pacman.username.Strategy;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;
import pacman.game.GameView;

/*Strategy for hunting PacMan by going to PacMans's last known position*/
class HuntPacMan implements Strategy {

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		MOVE move = null;
		
		if (game.getGhostLairTime(ghost) == 0) {
			if (game.getPacmanCurrentNodeIndex() > -1 && game.isJunction(current)) {
				move = StaticFunctions.getMoveToNearestObject(game, game.getGhostCurrentNodeIndex(ghost),
						new int[] { game.getPacmanCurrentNodeIndex() });
			} else if (game.isJunction(current)) {
				move = StaticFunctions.getMoveToNearestObject(game, game.getGhostCurrentNodeIndex(ghost),
						new int[] { memory.getPacManLastKnownPosition() });
			}

		}

		return move;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "HuntPacMan";
	}

	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}

}

/*Strategy for avoiding other ghosts to spread out into more directions instead of having multiple ghosts at the  same place.*/
class AvoidOtherGhost implements Strategy {

	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		MOVE move = null;
		Random rand = new Random();

		ArrayList<MOVE> possibleMovesList = new ArrayList<>();
		possibleMovesList.addAll(Arrays.asList(game.getPossibleMoves(current)));
		if (possibleMovesList.contains(game.getGhostLastMoveMade(ghost).opposite())) {
			possibleMovesList.remove(game.getGhostLastMoveMade(ghost).opposite());
		}

		int otherGhostPosition = -1;

		if (game.isJunction(current)) {
			if (game.getGhostLairTime(ghost) == 0) {
				for (GHOST otherGhost : GHOST.values()) {
					if (otherGhost == ghost)
						continue;
					if (game.getGhostCurrentNodeIndex(otherGhost) > -1) {//seeing other ghost
						otherGhostPosition = game.getGhostCurrentNodeIndex(otherGhost);

						if (possibleMovesList.contains(game.getGhostLastMoveMade(ghost)))
							possibleMovesList.remove(possibleMovesList.indexOf(game.getGhostLastMoveMade(ghost)));//change direction as fast as possible
						move = possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
						break;
					}
				}
			}
		}

		return move;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "AvoidOtherGhost";
	}

}
/*Strategy for running away from PacMan*/
class RunAwayFromPacMan implements Strategy {
	
	GHOST _ghost = null;
	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		// TODO Auto-generated method stub
		MOVE move = null;
		Random rand = new Random();
		_ghost = ghost;
		ArrayList<MOVE> possibleMovesList = new ArrayList<>();
		possibleMovesList.addAll(Arrays.asList(game.getPossibleMoves(current)));
		if (possibleMovesList.contains(game.getGhostLastMoveMade(ghost).opposite())) {
			possibleMovesList.remove(game.getGhostLastMoveMade(ghost).opposite());
		}

		if (game.getGhostLairTime(ghost) == 0) {
			if (game.isJunction(current)) {
				if (game.getPacmanCurrentNodeIndex() > -1) {
					MOVE awayFromPacMan = StaticFunctions.getMoveToNearestObject(game, current,
							new int[] { game.getPacmanCurrentNodeIndex() });
					if (awayFromPacMan != null && possibleMovesList.contains(awayFromPacMan.opposite()))
						move = StaticFunctions
								.getMoveToNearestObject(game, current, new int[] { game.getPacmanCurrentNodeIndex() })
								.opposite();
					else {
						if (possibleMovesList.contains(game.getGhostLastMoveMade(ghost)))
							possibleMovesList.remove(possibleMovesList.indexOf(game.getGhostLastMoveMade(ghost)));
						move = possibleMovesList.get(rand.nextInt(possibleMovesList.size()));
					}
				}
			}
		}

		return move;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "RunAwayFromPacMan";
	}
	
	/*Ghosts should only run away if they are edible.*/
	public boolean requirementsMet(Game game, int current, GhostMemory memory)
	{
		if(this._ghost != null)
		{
			if(game.isGhostEdible(this._ghost)){
				return true;
			}
		}
		return false;
	}
}
/*Strategy for running around objects*/
class RunCircle implements Strategy {

	private MOVE moveLastTime = null;
	private Random rand = new Random();

	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		if (moveLastTime == null) {
			memory.lastStrategyUsed = "";
		}

		if (game.getGhostLairTime(ghost) == 0) {
			// ghost didn't change strategies and already chose to run a circle
			if (memory.lastStrategyUsed.equals(getStrategyName())) {

				if (game.isJunction(current))
					return StaticFunctions.getMoveFromPacmanPointOfView(game, moveLastTime,
							game.getGhostLastMoveMade(ghost));
				return game.getGhostLastMoveMade(ghost);
			}

			// first select random direction
			// then follow that direction until a wild junction appears
			// At the junction decide if ghost wants to run in clockwise or
			// counterclockwise direction
			ArrayList<MOVE> possibleMoves = new ArrayList<MOVE>(Arrays.asList(game.getPossibleMoves(current)));
			if (possibleMoves.contains(game.getGhostLastMoveMade(ghost).opposite())) {
				possibleMoves.remove(game.getGhostLastMoveMade(ghost).opposite());
			}
			int moveNumber = rand.nextInt(possibleMoves.size());
			MOVE initialDirection = possibleMoves.get(moveNumber);
			MOVE direction = initialDirection;
			int simulatedCurrent = game.getNeighbour(current, direction);
			while (!game.isJunction(simulatedCurrent)) {
				MOVE cornerMove = StaticFunctions.CornerRoutine(game, simulatedCurrent,
						game.getPossibleMoves(simulatedCurrent), direction);
				if (cornerMove != null) {
					direction = cornerMove;
					simulatedCurrent = game.getNeighbour(simulatedCurrent, cornerMove);
					continue;
				}
				simulatedCurrent = game.getNeighbour(simulatedCurrent, direction);
			}
			int clockWiseDirection = rand.nextInt(2);
			moveLastTime = (clockWiseDirection == 0) ? MOVE.LEFT : MOVE.RIGHT;
			
			// check if planned move is even possible
			if (!StaticFunctions.isMovePossibleAtNode(game, simulatedCurrent, moveLastTime))
				moveLastTime = moveLastTime.opposite();
			return initialDirection;
		}
	
		return null;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "RunCircle";
	}

}

/*Strategy for going to nearest power pill*/
class GoToNearestPowerPill implements Strategy
{
	@Override
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory) {
		
		if(GHOST_DISTANCE_TO_POWERPILL.enumUsed == false && memory.getStillAvailablePowerPills().size() > 0)
		{
			return StaticFunctions.getMoveToNearestObject(game, current, StaticFunctions.convertIntegerListToArray(memory.getSeenPowerPills()));
			
		}
		if(GHOST_DISTANCE_TO_POWERPILL.enumUsed && memory.getStillAvailablePowerPills().size() > 0)
		{
			return game.getNextMoveTowardsTarget(current,GHOST_DISTANCE_TO_POWERPILL.m_shortestPathPacmanToNextPowerPill[0],DM.PATH);
		}
		return null;
	}

	@Override
	public String getStrategyName() {
		// TODO Auto-generated method stub
		return "GoToNearestPowerPill";
	}

	@Override
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean requirementsMet(Game game, int current, GhostMemory memory)
	{
		ArrayList<Integer> powerPills =  memory.getSeenPowerPills();
		return (powerPills.size() == 0) ? false : true;
	}
}
