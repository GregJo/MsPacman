package entrants.pacman.username;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

public interface Strategy {
	
	public class initialProbability {
	    public static double RANDOM = -1;
	}
	
	default public MOVE getStrategyMove(Game game, int current, Memory memory)
	{
		MOVE move = _getStrategyMove(game, current, memory);
		updateMemoryBeforeReturn(game, current, memory);
		memory.lastStrategyUsed = getStrategyName();
		
		if(move == null)
		{
			int a = 5;
		}
		
		return move;
		
	};
	default public void updateMemoryBeforeReturn(Game game, int current, Memory memory)
	{
		
	}
	
	default public boolean requirementsMet(Game game, int current, Memory memory)
	{
		return true;	
	}
	
	default public double getStrategyInitialProbability(){return initialProbability.RANDOM;};
	public MOVE _getStrategyMove(Game game, int current, Memory memory);
	public String getStrategyName();
	
}