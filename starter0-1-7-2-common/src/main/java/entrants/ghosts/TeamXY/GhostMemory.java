package entrants.ghosts.TeamXY;

import java.util.ArrayList;

import entrants.pacman.TeamXY.PacManMemory;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostMemory extends PacManMemory {
	private int m_pacManLastKnownPosition;
	private MOVE m_pacManLastKnownMove;
	private ArrayList<Integer> m_seenPowerPills = new ArrayList<Integer>(); //memory of which powerpills are still edible
	
	public GhostMemory(){m_memoryInitialized = false; lastStrategyUsed = "";m_levelIndex = 0;lastStateString="";}
	
	/*@brief initializes the memory
	 * @param game the curent Game
	 * @param current the current position of the current ghost
	 * */
	private void initializeMemory(Game game, int current)
	{
		if(m_levelIndex != game.getCurrentLevel())
			m_levelIndex = game.getCurrentLevel();
		
			
		m_memoryInitialized = true;
		initPacMan(game, current);
	}
	/*@brief initializes the list of still available power pills
	 * @param game the curent Game
	 * @param current the current position of the current ghost
	 * */
	private void initializePowerPills(Game game, int current)
	{
		for (Integer integer : game.getPowerPillIndices()) {
			m_seenPowerPills.add(integer);
		}
	}
	
	/*@brief updates the memory
	 * @param game the current Game
	 * @param current the current position of the current ghost
	 * */
	public void updateMemory(Game game, int current)
	{
		m_levelChanged = (game.getCurrentLevel() != m_levelIndex) ? true : false;
		if(!m_memoryInitialized || m_levelChanged)
		{
			initializeMemory(game, current);
			initializePowerPills(game, current);
		}
		updatePacMan(game, current);
		updatePowerPills(game, current);
	}
	/*@brief updates the last known position of PacMan
	 * @param game the current Game
	 * @param current the current position of the current ghost
	 * */
	public void updatePacMan(Game game, int current)
	{
		if (game.wasPacManEaten()) {
			initPacMan(game, current);
		}
		if (game.getPacmanCurrentNodeIndex()>-1)
		{
			m_pacManLastKnownPosition = game.getPacmanCurrentNodeIndex();
			m_pacManLastKnownMove = game.getPacmanLastMoveMade();
		}
	}
	/*@brief updates the list of still available power pills
	 * @param game the current Game
	 * @param current the current position of the current ghost
	 * */
	public void updatePowerPills(Game game, int current)
	{
		int powerPillToRemove = -1;
		for (Integer powerPillIdx : m_seenPowerPills) {
			if (current == powerPillIdx && game.getActivePowerPillsIndices().length == 0) {
				powerPillToRemove = powerPillIdx;
				break;
			}
		}
		
		if (powerPillToRemove != -1) {
			m_seenPowerPills.remove(m_seenPowerPills.indexOf(powerPillToRemove));
		}
	}
	
	//INITIALIZERS
	private void initPacMan(Game game, int current)
	{
		m_pacManLastKnownPosition=game.getPacManInitialNodeIndex();
		m_pacManLastKnownMove = MOVE.NEUTRAL;
	}
	
	//GETTERS
	public int getPacManLastKnownPosition()
	{
		return m_pacManLastKnownPosition;
	}
	
	//GETTERS
	public ArrayList<Integer> getSeenPowerPills()
	{
		return m_seenPowerPills;
	}
}
