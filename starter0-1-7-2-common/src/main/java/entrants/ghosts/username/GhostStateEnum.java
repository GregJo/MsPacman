package entrants.ghosts.username;

import static pacman.game.Constants.EDIBLE_TIME;
import static pacman.game.Constants.EDIBLE_TIME_REDUCTION;
import static pacman.game.Constants.LEVEL_RESET_REDUCTION;

import java.util.ArrayList;

import entrants.pacman.username.Memory;
import entrants.pacman.username.StateEnum;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

////////////////////ENUMS DESCRIBING THE GLOBAL STATE ///////////////////////////////////
enum POWERPILLS_LEFT implements StateEnum {
	noPowerPillLeft, powerPillsLeft;
	public String getCurrentStateString(Game game, int current, Memory memory) {
		ArrayList<Integer> powerPills = memory.getStillAvailablePowerPills();
		String returnValue;
		switch (powerPills.size()) {
		case 0:
			returnValue = noPowerPillLeft.name();
			break;
		default:
			returnValue = powerPillsLeft.name();
			break;
		}
		return returnValue;
	}

	@Override
	public void resetStaticVars() {
		// TODO Auto-generated method stub

	}
}

//////////////////// ENUMS DESCRIBING THE LOCAL ENVIROMENT
//////////////////// ///////////////////////////////////
enum KIND_OF_LEVEL_TILE implements StateEnum {
	deadEnd, hallWay, threeWayJunction, fourWayJunction;
	public String getCurrentStateString(Game game, int current, Memory memory) {
		int moveCounter = 0;
		for (MOVE move : game.getPossibleMoves(current)) {
			moveCounter++;
		}
		if (moveCounter == 1)
			return deadEnd.name();
		if (moveCounter == 2)
			return hallWay.name();
		if (moveCounter == 3)
			return threeWayJunction.name();
		if (moveCounter == 4)
			return fourWayJunction.name();
		return deadEnd.name();
	}

	@Override
	public void resetStaticVars() {
		// TODO Auto-generated method stub

	}
}

//////////////////// ENUMS DESCRIBING STATE OF MS.PACMAN
//////////////////// ///////////////////////////////////
enum POWER_PILL_ACTIVATED implements StateEnum {
	powerPillActive, powerPillNotActive;
	static int powerPillTime = 0;

	public String getCurrentStateString(Game game, int current, Memory memory) {
		if (game.wasPacManEaten())
			powerPillTime = 0;
		if (powerPillTime > 0)
			powerPillTime--;

		if (game.wasPowerPillEaten()) {
			int level = game.getCurrentLevel();
			powerPillTime = (int) (EDIBLE_TIME * (Math.pow(EDIBLE_TIME_REDUCTION, level % LEVEL_RESET_REDUCTION)));
		}
		return (powerPillTime > 0) ? POWER_PILL_ACTIVATED.powerPillActive.name()
				: POWER_PILL_ACTIVATED.powerPillNotActive.name();

	}

	@Override
	public void resetStaticVars() {
		powerPillTime = 0;

	}
}

//////////////////// ENUMS DESCRIBING WHAT MS.PACMAN SEES
//////////////////// ///////////////////////////////////
enum NUMBER_SEEN_GHOSTS implements StateEnum {
	seeingNoGhost, seeingOneGhost, seeingTwoGhost, seeingThreeGhost, seeingFourGhost;
	public static int ghostCounter = 0;

	public String getCurrentStateString(Game game, int current, Memory memory) {
		ghostCounter = 0;
		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostCurrentNodeIndex(ghost) != -1) {
				ghostCounter++;
			}
		}
		if (ghostCounter == 0)
			return NUMBER_SEEN_GHOSTS.seeingNoGhost.name();
		if (ghostCounter == 1)
			return NUMBER_SEEN_GHOSTS.seeingOneGhost.name();
		if (ghostCounter == 2)
			return NUMBER_SEEN_GHOSTS.seeingTwoGhost.name();
		if (ghostCounter == 3)
			return NUMBER_SEEN_GHOSTS.seeingThreeGhost.name();
		if (ghostCounter == 4)
			return NUMBER_SEEN_GHOSTS.seeingFourGhost.name();
		return NUMBER_SEEN_GHOSTS.seeingNoGhost.name();
	}

	@Override
	public void resetStaticVars() {
		// TODO Auto-generated method stub

	}
}