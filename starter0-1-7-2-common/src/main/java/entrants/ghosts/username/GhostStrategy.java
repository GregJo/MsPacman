package entrants.ghosts.username;

import entrants.pacman.username.*;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

class initialProbability {
    public static double RANDOM = -1;
}

interface GhostStrategy {
	
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
	
	default public double getStrategyInitialProbability(){return initialProbability.RANDOM;};
	public MOVE _getStrategyMove(Game game, int current, Memory memory);
	public String getStrategyName();
	
}
