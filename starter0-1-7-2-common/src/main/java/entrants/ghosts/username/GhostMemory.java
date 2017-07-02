package entrants.ghosts.username;

import java.util.ArrayList;
import java.util.Arrays;

import entrants.pacman.username.MyPacMan;
import entrants.pacman.username.PacManMemory;
import entrants.pacman.username.StaticFunctions;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostMemory extends PacManMemory {
	private int m_pacManLastKnownPosition;
	private MOVE m_pacManLastKnownMove;
	private ArrayList<Integer> m_seenPowerPills = new ArrayList<Integer>(); //memory of which powerpills are still edible
	
	public GhostMemory(){m_memoryInitialized = false; lastStrategyUsed = "";m_levelIndex = 0;lastStateString="";}
	
	private void initializeMemory(Game game, int current)
	{
		if(m_levelIndex != game.getCurrentLevel())
			m_levelIndex = game.getCurrentLevel();
		
			
		m_memoryInitialized = true;
		initPacMan(game, current);
	}
	
	private void initializePowerPills(Game game, int current)
	{
		for (Integer integer : game.getPowerPillIndices()) {
			m_seenPowerPills.add(integer);
		}
		
		System.out.println("No. of initial m_seenPowerPills: " + m_seenPowerPills.size());
		System.out.println("\nNo. of initial m_seenPowerPills:");
		for (int i = 0; i < m_seenPowerPills.size(); i++) {
			System.out.println("PowerPill No." + i + ": " + m_seenPowerPills.get(i));
		}
	}
	
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
			System.out.println("\nNo. of m_seenPowerPills:");
			for (int i = 0; i < m_seenPowerPills.size(); i++) {
				System.out.println("PowerPill No." + i + ": " + m_seenPowerPills.get(i));
			}
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
