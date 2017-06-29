package entrants.pacman.username;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Random;

import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;

public class GeneticAlgorithm {

	public static void notMain(boolean runExperiment, MyPacMan pacMan) {
        Executor executor = new Executor(true, true);
        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
        
        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
      
        if(runExperiment)
        	executor.runExperiment(pacMan, new MASController(controllers), 1, "", 4000);
        else
        	executor.runGameTimed(pacMan, new MASController(controllers), true);
    }
	
	public static ArrayList<MyPacMan> resetPacMans(ArrayList<MyPacMan> pacmansToReset)
	{
		ArrayList<MyPacMan> newPacMans = new ArrayList<>();
		for(MyPacMan currentPacMan : pacmansToReset)
		{
			MyPacMan newPacMan = new MyPacMan();
			newPacMan.setProbabilities(currentPacMan.getProbabilities());
			newPacMans.add(newPacMan);
		}
		return newPacMans;
	}
	
	public static void calculateFitness(int numberGamesPerPacMan, ArrayList<MyPacMan> pacMans) 
	{
    	for(int i = 0; i < pacMans.size(); i++)
    	{
    		int fitness = 0;
    		MyPacMan current_pacMan = pacMans.get(i);
    		for(int j=0; j < numberGamesPerPacMan; j++)
    		{
    			//System.out.println("Pacman #"+i+", game #"+j+" started");
    			GeneticAlgorithm.notMain(false, current_pacMan);
    			
    			//System.out.println("fitness for this game: "+newFitness);
    			fitness += current_pacMan.fitness; 
    			ArrayList<ProbabilityByState> probs = current_pacMan.getProbabilities();
    			current_pacMan = new MyPacMan();
    			current_pacMan.setProbabilities(probs);
    		}
    		fitness /= numberGamesPerPacMan;
    		//System.out.println("Pacman #"+i+" finished. Fitness: "+fitness);
    		//all_fitness[i] = fitness;
    		current_pacMan.fitness = fitness;
    	}
	} 
   

	
	public static int calculateFitnessSumOfGeneration(String listSavePath, int run, ArrayList<MyPacMan> generation) {
		int fitnessSum = 0;int counter = 0;
		for (MyPacMan myPacMan : generation) 
		{
			fitnessSum += myPacMan.fitness;
			if(myPacMan.fitness > 1000)
			{
				ArrayList<MyPacMan> goodPacMans = new ArrayList<>();
				goodPacMans.add(myPacMan);
				String saveName = listSavePath + "fitness_"+myPacMan.fitness+"_run_"+run+"counter_"+counter;
				GeneticAlgorithm.savePacManList(goodPacMans, saveName);
				counter++;
			}		
		}
		
		return fitnessSum;
	}
	
	public static void savePacManList(ArrayList<MyPacMan> bestPacMans, String listSavePath) 
	{
		ArrayList<ArrayList<ProbabilityByState>> probabilitiesOfAllPacMans = new ArrayList<>();
		for(MyPacMan currentPacMan : bestPacMans)
		{
			probabilitiesOfAllPacMans.add(currentPacMan.getProbabilities());
		}
		FileOutputStream fout = null;
    	ObjectOutputStream oos = null;
		try {
			fout = new FileOutputStream(listSavePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		try {
			oos = new ObjectOutputStream(fout);
			oos.writeObject(probabilitiesOfAllPacMans);
			fout.close();
	    	oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public static ArrayList<MyPacMan> mutate(ArrayList<MyPacMan> pacmans, double mutationRate, double mutationStepSizeUpperLimit)
	 {
		 for(int i =0; i < pacmans.size(); i++)
		 {
			 pacmans.set(i, mutate(pacmans.get(i), mutationRate, mutationStepSizeUpperLimit));
		 }
		 return pacmans;
	 }
		
	
	 public static MyPacMan mutate(MyPacMan pacman, double mutationRate, double mutationStepSizeUpperLimit)
	    {
	    	ArrayList<ProbabilityByState> probs = pacman.getProbabilities();
	    	Random rand = new Random();
	    	for (int i = 0; i < probs.size(); i++) {
	    		ProbabilityByState prob = probs.get(i);
	    		for(int j=0; j < prob.getProbability().getNumberOfProbabilities(); j++)
	    		{
	    			if(rand.nextDouble() <= mutationRate)
	        		{
	        			double stepSize = rand.nextDouble()*mutationStepSizeUpperLimit;
	    				//double stepSize = mutationStepSizeUpperLimit;
	        			if(rand.nextDouble() <= 0.5)
	        				stepSize = -stepSize;
	        			double newProbability = prob.getProbability().getProbability(j)+stepSize;
	        			prob.getProbability().setProbability(j, newProbability);
	        			
//	        			System.out.println("\n Pacman Mutated!");
//	        			System.out.println("Mutated state: "+prob.m_stateString);
//	        			System.out.println("Strategy #"+j +" changed by "+stepSize);
	        		}
	    		}
	    		prob.getProbability().normalizeProbability();
			}
	    	pacman.setProbabilities(probs);
	    	return pacman;
	    }
	
	
	 public static ArrayList<MyPacMan> nFittestPacMans(MyPacMan[] pacMans, int n)
	    {
	    	Arrays.sort(pacMans, new Comparator<MyPacMan>(){
				@Override
				public int compare(MyPacMan a, MyPacMan b) {
	    	    	if(a.fitness < b.fitness)
	    	    		return -1;
	    	    	if(a.fitness > b.fitness)
	    	    		return 1;
	    	    	return 0;
				}
	    	});
	    	ArrayList<MyPacMan> list =  new ArrayList<>(Arrays.asList(Arrays.copyOfRange(pacMans, pacMans.length-n, pacMans.length)));
	    	Collections.reverse(list);
	    	return list;
	    }
	
	 public static ArrayList<MyPacMan> createNewGeneration(int n)
		{
		 	ArrayList<MyPacMan> pacMans = createNPacMans(n);
		 	return pacMans;
		}

	private static ArrayList<MyPacMan> createNPacMans(int n)
	{
		ArrayList<MyPacMan> pacMans = new ArrayList<>();
		for(int i=0; i < n; i++)
			pacMans.add(new MyPacMan());
		return pacMans;
	}
	
	public static MyPacMan childPacMan(ArrayList<MyPacMan> nFittestPacMans, Random rand, int i, int j) {
		ArrayList<ProbabilityByState> probs1 = nFittestPacMans.get(i).getProbabilities();
		ArrayList<ProbabilityByState> probs2 = nFittestPacMans.get(j).getProbabilities();
		ArrayList<ProbabilityByState> finalProbs = nFittestPacMans.get(j).getProbabilities();
		for (int k = 0; k < probs1.size(); k++) {
			if (probs1.get(k).counter != 0 && probs2.get(k).counter != 0) {
				int stateCounterSum_first = nFittestPacMans.get(i).getStateCounterSum();
				int stateCounterSum_second = nFittestPacMans.get(j).getStateCounterSum();
				
				double influenceOfThisState_first = probs1.get(k).counter/stateCounterSum_first;
				double influenceOfThisState_second = probs2.get(k).counter/stateCounterSum_second;
				
				double fitnessState_first = influenceOfThisState_first * nFittestPacMans.get(i).fitness;
				double fitnessState_second = influenceOfThisState_second * nFittestPacMans.get(j).fitness;
				
				
				if(fitnessState_first > fitnessState_second)
					finalProbs.set(k, probs1.get(k));
				else
					finalProbs.set(k, probs2.get(k));
			 		
			}
			if (probs1.get(k).counter == 0 && probs2.get(k).counter != 0) {
				finalProbs.set(k, probs2.get(k));
			}
			if (probs1.get(k).counter != 0  && probs2.get(k).counter == 0) {
				finalProbs.set(k, probs1.get(k));
			}
		}
	MyPacMan current_pacMan = new MyPacMan();
	current_pacMan.setProbabilities(finalProbs);
		return current_pacMan;
	}
	
	public static double adaptMutationStepSizeLinear(int fitnessGoal, double mutationStepSizeMax, double mutationStepSizeMin, int averageFitnessOfCurrentGeneration)
	{
		double newMutationStepSize = 0;
		int diff = Math.abs(fitnessGoal - averageFitnessOfCurrentGeneration);
		double onePercent = fitnessGoal / (mutationStepSizeMax*100);
		newMutationStepSize = (diff/onePercent)/100;
		newMutationStepSize = (newMutationStepSize < mutationStepSizeMin) ? mutationStepSizeMin : newMutationStepSize;
		return newMutationStepSize;
	}
	
	public static ArrayList<MyPacMan> loadPacManList(String path)
	{
		ArrayList<ArrayList<ProbabilityByState>> pacManList = new ArrayList<>();
		 try
		 {
			 InputStream file = new FileInputStream(path);
		     InputStream buffer = new BufferedInputStream(file);
		     ObjectInput input = new ObjectInputStream (buffer);
		
			 pacManList = ( ArrayList<ArrayList<ProbabilityByState>>)input.readObject(); 
			 file.close();
			 buffer.close();
			 input.close();
			 
		  }
		  catch(ClassNotFoundException e)
		 {
			  e.printStackTrace();
		 }
	     catch(IOException e)
		 {
	    	 e.printStackTrace();
	     }
		 ArrayList<MyPacMan> loadedPacMans = new ArrayList<>();
		 for(ArrayList<ProbabilityByState> p : pacManList)
		 {
			 MyPacMan pacMan = new MyPacMan();
			 pacMan.setProbabilities(p);
			 loadedPacMans.add(pacMan);
		 }
		 return loadedPacMans;
		 
	}
	
	@SuppressWarnings("unchecked")
	public static MyPacMan loadPacMan(String path)
	{
		ArrayList<ArrayList<ProbabilityByState>> pacManList = new ArrayList<>();
		 try
		 {
			 InputStream file = new FileInputStream(path);
		     InputStream buffer = new BufferedInputStream(file);
		     ObjectInput input = new ObjectInputStream (buffer);
		
			 pacManList = ( ArrayList<ArrayList<ProbabilityByState>>)input.readObject(); 
			 file.close();
			 buffer.close();
			 input.close();
			 
		  }
		  catch(ClassNotFoundException e)
		 {
			  e.printStackTrace();
		 }
	     catch(IOException e)
		 {
	    	 e.printStackTrace();
	     }
		 MyPacMan pacMan = new MyPacMan();
		 pacMan.setProbabilities(pacManList.get(0));
		 return pacMan;
	}
	
	public static ArrayList<MyPacMan> generateNextGeneration(ArrayList<MyPacMan> nFittestPacMans, int fitnessSum) {
		Random rand = new Random();
		ArrayList<MyPacMan> newGeneration = new ArrayList<MyPacMan>();
		// PacMan roulette
		for (int i = 0; i < nFittestPacMans.size(); i++) {
			for (int j = 0; j < nFittestPacMans.size(); j++) {
			 	if(rand.nextDouble() <= (double)nFittestPacMans.get(i).fitness/(double)fitnessSum)
			 	{
			 		MyPacMan current_pacMan = childPacMan(nFittestPacMans, rand, i, j);
			 	newGeneration.add(current_pacMan);
			 	if(newGeneration.size() == 50)
			 		break;
			 	}
			}
			if(newGeneration.size() == 50)
			 		break;
		}
		return newGeneration;
	}
}