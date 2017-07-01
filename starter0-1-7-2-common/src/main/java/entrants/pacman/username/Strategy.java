package entrants.pacman.username;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Ghost;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import entrants.ghosts.username.GhostMemory;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public interface Strategy {
	
	public class initialProbability {
	    public static double RANDOM = -1;
	}
	
	default public MOVE getStrategyMove(Game game, int current, PacManMemory memory)
	{
		MOVE move = _getStrategyMove(game, current, memory);
		updateMemoryBeforeReturn(game, current, memory);
		memory.lastStrategyUsed = getStrategyName();
	
		return move;
		
	};
	default public MOVE getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory)
	{
		MOVE move = _getStrategyMove(game, ghost, current, memory);
		updateMemoryBeforeReturn(game, ghost, current, memory);
		memory.lastStrategyUsed = getStrategyName();
		
		return move;
		
	};
	default public void updateMemoryBeforeReturn(Game game, int current, PacManMemory memory)
	{
		
	}
	
	default public void updateMemoryBeforeReturn(Game game, GHOST ghost, int current, GhostMemory memory)
	{
		
	}
	
	default public boolean requirementsMet(Game game, int current, PacManMemory memory)
	{
		return true;	
	}
	
	default public double getStrategyInitialProbability(){return initialProbability.RANDOM;};
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory);
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory);
	public String getStrategyName();
	
}