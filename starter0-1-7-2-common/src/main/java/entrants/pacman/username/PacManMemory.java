package entrants.pacman.username;

import java.util.ArrayList;
import java.util.Arrays;

import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/* Memory of PacMan. Stores still available pills, last known ghosts positions etc.
 * 
 * */
public class PacManMemory 
{
	//MEMBER VARIABLES
	private ArrayList<Integer> m_stillAvailablePills = new ArrayList<Integer>(); //memory of which pills are still edible
	private ArrayList<Integer> m_stillAvailablePowerPills = new ArrayList<Integer>(); //memory of which powerpills are still edible
	private ArrayList<GHOST>   m_seenGhostsMemory = new ArrayList<GHOST>(); //memory of which ghosts were seen (last known position, updated on sight)
	private ArrayList<Integer> m_ghostPositionList = new ArrayList<Integer>(); 
	
	
	protected boolean m_memoryInitialized;
	protected boolean m_levelChanged;
	protected int m_levelIndex;
	public  String lastStrategyUsed;
	public String lastStateString;
	public boolean stateChanged = true;
	
	public PacManMemory(){m_memoryInitialized = false; lastStrategyUsed = "";m_levelIndex = 0;lastStateString="";}
	
	/*@brief initializes the memory
	 * @param game the curent Game
	 * @param current the current position of PacMan
	 * */
	private void initializeMemory(Game game, int current)
	{
		if(m_levelIndex != game.getCurrentLevel())
			m_levelIndex = game.getCurrentLevel();
		
			
		m_memoryInitialized = true;
		initStillAvailablePowerPills(game, current);
		initStillAvailablePills(game, current);
		initLastSeenGhosts(game, current);
	}
	
	/*@brief updates the memory
	 * @param game the current Game
	 * @param current the current position of PacMan
	 * */
	public void updateMemory(Game game, int current)
	{
		m_levelChanged = (game.getCurrentLevel() != m_levelIndex) ? true : false;
		if(!m_memoryInitialized || m_levelChanged) //memory needs to be reinitialized if level changed
			initializeMemory(game, current);
		updateStillAvailablePowerPills(game, current);
		updateStillAvailablePills(game, current);
		updateLastSeenGhosts(game, current);
	}
	
	/*@brief updates the list of still available power pills
	 * @param game the current Game
	 * @param current the current position of PacMan
	 * */
	private void updateStillAvailablePowerPills(Game game, int current)
	{
		if(game.wasPowerPillEaten())
		{
			m_stillAvailablePowerPills.remove(new Integer(current));
		}
	}
	
	/*@brief updates the list of still available pills
	 * @param game the current Game
	 * @param current the current position of PacMan
	 * */
	private void updateStillAvailablePills(Game game, int current)
	{
		if(game.wasPillEaten())
		{
			if(m_levelChanged) //don't update if new level just started
				return;
			
			//get eaten pills
			ArrayList<Integer> pillsToRemove = new ArrayList<Integer>();
			for(int pill : m_stillAvailablePills)
			{
				Boolean stillAvail = game.isPillStillAvailable(game.getPillIndex(pill));
				if(stillAvail == null) //can't see pill position
					continue;
				if(stillAvail == false) //can see pill position but no pill there anymore
				{
					pillsToRemove.add(pill);
					
				}
			}
			//remove found pills
			for(int pillToRemove : pillsToRemove)
			{
				m_stillAvailablePills.remove(new Integer(pillToRemove));
			}
			
		}
	}
	
	/*@brief updates the list of last seen ghosts
	 * @param game the current Game
	 * @param current the current position of PacMan
	 * */
	private void updateLastSeenGhosts(Game game, int current)
	{
		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostCurrentNodeIndex(ghost)>-1 && !m_seenGhostsMemory.contains(ghost)) //add ghost if PacMan sees new ghost
			{
				m_seenGhostsMemory.add(ghost);
				if(game.wasGhostEaten(ghost))
				{
					m_ghostPositionList.set(m_seenGhostsMemory.indexOf(ghost), game.getGhostInitialNodeIndex());
				}
			}
		}
		if(game.wasPacManEaten())
		{
			initLastSeenGhosts(game, current);
		}
	}
	
	//GETTER
	public final ArrayList<Integer> getStillAvailablePowerPills()
	{
		return m_stillAvailablePowerPills;
	}
	//GETTER
	public final ArrayList<Integer> getStillAvailablePills()
	{
		return m_stillAvailablePills;
	}
	//GETTER
	public final ArrayList<Integer> getLastKnownGhostPositions(Game game)
	{
    	for(GHOST ghost : m_seenGhostsMemory)
    	{
			if (game.getGhostCurrentNodeIndex(ghost)>-1 && m_seenGhostsMemory.contains(ghost))
				m_ghostPositionList.set(m_seenGhostsMemory.indexOf(ghost), game.getGhostCurrentNodeIndex(ghost));
    	}
		return m_ghostPositionList;
	}
	
	//INITIALIZERS
	private void initStillAvailablePowerPills(Game game, int current)
	{
		m_stillAvailablePowerPills = new ArrayList<Integer>();
		int[] powerPillIndizes = game.getPowerPillIndices();
    	for(int index : powerPillIndizes)
    	{
    		m_stillAvailablePowerPills.add(index);
    	}
	}
	private void initStillAvailablePills(Game game, int current)
	{
		m_stillAvailablePills = new ArrayList<Integer>();
		int[] pillIndizes = game.getPillIndices();
    	for(int index : pillIndizes)
    	{
    		m_stillAvailablePills.add(index);
    	}
	}
	private void initLastSeenGhosts(Game game, int current)
	{
		m_seenGhostsMemory = new ArrayList<GHOST>();
		m_ghostPositionList = new ArrayList<Integer>();
    	for(GHOST ghost : GHOST.values())
    	{
    		m_ghostPositionList.add(game.getGhostInitialNodeIndex());
    	}
	}
}
