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
	
	//"Enum" to avoid magic numbers
	public class initialProbability {
	    public static double RANDOM = -1;
	}
	 /*@brief Returns the move of the strategy for the current PacMan
	   * @param game the current Game Object
	   * @param current the current position of PacMan
	   * @param memory the memory of the current PacMan
	   * @returns the move to be made 
	   * */
	default public MOVE getStrategyMove(Game game, int current, PacManMemory memory)
	{
		MOVE move = _getStrategyMove(game, current, memory); //actually get the move to return
		updateMemoryBeforeReturn(game, current, memory); // if necessary update memory before returning
		memory.lastStrategyUsed = getStrategyName();
	
		return move;
		
	};
	/*@brief Returns the move of the strategy for the current ghost
	   * @param game the current Game Object
	   * @param current the current position of the current ghost
	   * @param memory the memory of the current ghost
	   * @returns the move to be made 
	   * */
	default public MOVE getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory)
	{
		MOVE move = _getStrategyMove(game, ghost, current, memory); //actually get the move to return
		updateMemoryBeforeReturn(game, ghost, current, memory);// if necessary update memory before returning
		memory.lastStrategyUsed = getStrategyName();
		
		return move;
		
	};
	/*@brief Updates the memory
	   * @param game the current Game Object
	   * @param current the current position of PacMan
	   * @param memory the memory of the current PacMan
	   * @returns the move to be made 
	   * */
	default public void updateMemoryBeforeReturn(Game game, int current, PacManMemory memory)
	{
		
	}
	/*@brief Updates the memory
	   * @param game the current Game Object
	   * @param current the current position of the current ghost
	   * @param memory the memory of the current ghost
	   * @returns the move to be made 
	   * */
	default public void updateMemoryBeforeReturn(Game game, GHOST ghost, int current, GhostMemory memory)
	{
		
	}
	/*@brief Checks whether a strategy should/can be used right now.
	   * @param game the current Game Object
	   * @param current the current position of PacMan
	   * @param memory the memory of the current PacMan
	   * @returns true if this strategy should/can be used, false if it shouldn't/can't use this strategy right now. 
	   * */
	default public boolean requirementsMet(Game game, int current, PacManMemory memory)
	{
		return true;	
	}
	
	/*@brief Initial probability used for a strategy on PacMan creation for all states.
	 * By default a random probability will be used.
	 * @returns probability to use for a strategy or initialProbability.RANDOM if the probability should be random.
	  * */
	default public double getStrategyInitialProbability(){return initialProbability.RANDOM;};
	
	/*@brief Gets the move to be made at this time step.
	  * @param game the current Game Object
	  * @param current the current position of PacMan
	  * @param memory the memory of the current PacMan
	  * @returns The move that should be made at this time step
	  * */
	public MOVE _getStrategyMove(Game game, int current, PacManMemory memory);
	
	/*@brief Gets the move to be made at this time step.
	  * @param game the current Game Object
	  * @param current the current position of PacMan
	  * @param memory the memory of the current PacMan
	  * @returns The move that should be made at this time step
	  * */
	public MOVE _getStrategyMove(Game game, GHOST ghost, int current, GhostMemory memory);
	
	/*@brief Gets the name of this strategy.
	  * @returns The name of this strategy
	  * */
	public String getStrategyName();
	
}