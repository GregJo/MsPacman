package entrants.pacman.username;

import java.util.ArrayList;
import java.util.Arrays;

import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Memory 
{
	//MEMBER VARIABLES
	private ArrayList<Integer> m_stillAvailablePills = new ArrayList<Integer>(); //memory of which pills are still edible
	private ArrayList<Integer> m_stillAvailablePowerPills = new ArrayList<Integer>(); //memory of which powerpills are still edible
	private ArrayList<Integer> m_seenPillMemory = new ArrayList<Integer>(); //memory of which pills were seen
	private ArrayList<GHOST>   m_seenGhostsMemory = new ArrayList<GHOST>(); //memory of which ghosts were seen (last known position, updated on sight)
	private ArrayList<Integer> m_ghostPositionList = new ArrayList<Integer>(); 
	
	
	private boolean m_memoryInitialized;
	private boolean m_levelChanged;
	private int m_levelIndex;
	public  String lastStrategyUsed;
	
	public Memory(){m_memoryInitialized = false; lastStrategyUsed = "";m_levelIndex = 0;}
	private void initializeMemory(Game game, int current)
	{
		if(m_levelIndex != game.getCurrentLevel())
			m_levelIndex = game.getCurrentLevel();
		
			
		m_memoryInitialized = true;
		initStillAvailablePowerPills(game, current);
		initStillAvailablePills(game, current);
		initLastSeenGhosts(game, current);
	}
	
	public void updateMemory(Game game, int current)
	{
		m_levelChanged = (game.getCurrentLevel() != m_levelIndex) ? true : false;
		if(!m_memoryInitialized || m_levelChanged)
			initializeMemory(game, current);
		updateStillAvailablePowerPills(game, current);
		updateStillAvailablePills(game, current);
		updateLastSeenGhosts(game, current);
	}
	
	private void updateStillAvailablePowerPills(Game game, int current)
	{
		if(game.wasPowerPillEaten())
		{
			m_stillAvailablePowerPills.remove(new Integer(current));
		}
	}
	private void updateStillAvailablePills(Game game, int current)
	{
		if(game.wasPillEaten())
		{
			if(m_levelChanged)
				return;
			
			ArrayList<Integer> pillsToRemove = new ArrayList<Integer>();
			for(int pill : m_stillAvailablePills)
			{
				Boolean stillAvail = game.isPillStillAvailable(game.getPillIndex(pill));
				if(stillAvail == null)
					continue;
				if(stillAvail == false)
				{
					pillsToRemove.add(pill);
					
				}
			}
			for(int pillToRemove : pillsToRemove)
			{
				m_stillAvailablePills.remove(new Integer(pillToRemove));
			}
			
		}
	}
	
	private void updateLastSeenGhosts(Game game, int current)
	{
		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostCurrentNodeIndex(ghost)>-1 && !m_seenGhostsMemory.contains(ghost))
				m_seenGhostsMemory.add(ghost);
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
