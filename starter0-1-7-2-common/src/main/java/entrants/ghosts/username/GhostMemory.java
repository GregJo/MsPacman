package entrants.ghosts.username;

import entrants.pacman.username.MyPacMan;
import entrants.pacman.username.PacManMemory;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostMemory extends PacManMemory {
	private int pacManLastKnownPosition;
	private MOVE pacManLastKnownMove;
	
	public GhostMemory(){m_memoryInitialized = false; lastStrategyUsed = "";m_levelIndex = 0;lastStateString="";}
	
	private void initializeMemory(Game game, int current)
	{
		if(m_levelIndex != game.getCurrentLevel())
			m_levelIndex = game.getCurrentLevel();
		
			
		m_memoryInitialized = true;
		initPacMan(game, current);
	}
	
	public void updateMemory(Game game, int current)
	{
		m_levelChanged = (game.getCurrentLevel() != m_levelIndex) ? true : false;
		if(!m_memoryInitialized || m_levelChanged)
			initializeMemory(game, current);
		updatePacMan(game, current);
	}
	
	public void updatePacMan(Game game, int current)
	{
		if (game.wasPacManEaten()) {
			initPacMan(game, current);
		}
		if (game.getPacmanCurrentNodeIndex()>-1)
		{
			pacManLastKnownPosition = game.getPacmanCurrentNodeIndex();
			pacManLastKnownMove = game.getPacmanLastMoveMade();
		}
	}
	
	//INITIALIZERS
	private void initPacMan(Game game, int current)
	{
		pacManLastKnownPosition=game.getPacManInitialNodeIndex();
		pacManLastKnownMove = MOVE.NEUTRAL;
	}
	
	//GETTERS
	public int getPacManLastKnownPosition()
	{
		return pacManLastKnownPosition;
	}
}
