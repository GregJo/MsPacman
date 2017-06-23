package entrants.pacman.username;

import java.awt.Color;
import java.util.ArrayList;
import pacman.game.Game;
import pacman.game.GameView;

public class Memory 
{
	//MEMBER VARIABLES
	private ArrayList<Integer> m_stillAvailablePills = new ArrayList<Integer>(); //memory of which pills are still edible
	private ArrayList<Integer> m_stillAvailablePowerPills = new ArrayList<Integer>(); //memory of which powerpills are still edible
	
	private boolean m_memoryInitialized;
	private boolean m_levelChanged;
	private int m_levelIndex;
	public  String lastStrategyUsed;
	
	public Memory(){m_memoryInitialized = false; lastStrategyUsed = "";}
	private void initializeMemory(Game game, int current)
	{
		if(m_levelIndex != game.getCurrentLevel())
			m_levelIndex = game.getCurrentLevel();
		
			
		m_memoryInitialized = true;
		initStillAvailablePowerPills(game, current);
		initStillAvailablePills(game, current);
	}
	
	
	public void updateMemory(Game game, int current)
	{
		m_levelChanged = (game.getCurrentLevel() != m_levelIndex) ? true : false;
		if(!m_memoryInitialized || m_levelChanged)
			initializeMemory(game, current);
		updateStillAvailablePowerPills(game, current);
		updateStillAvailablePills(game, current);
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
}
