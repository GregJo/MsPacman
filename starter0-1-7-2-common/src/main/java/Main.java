
import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;
//import examples.StarterPacManOneJunction.MyPacMan;
import entrants.pacman.username.MyPacMan;
import entrants.pacman.username.ProbabilityGenerator;
import entrants.pacman.username.ProbabilityByState;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Random;

/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    public static void notMain(String[] args, MyPacMan pacMan) {
        Executor executor = new Executor(true, true);
        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
        
        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
       
      //executor.runGameTimed(pacMan, new MASController(controllers), true);
      executor.runExperiment(pacMan, new MASController(controllers), 1, "", 4000);
       
    }
    
    public static void main(String[] args) {
    	int numberGamesPerPacMan = 15;
    	int numberOfDifferentPacMans = 15;
    	double mutationRate = 0.00;
    	double mutationStepSizeUpperLimit = 0.03;
    	final int runs = 10;
    	
    	double bestMutationRate = -1;
    	double bestFitnessGain = -1;
    	double bestFitness = -1;
    	ArrayList<MyPacMan> bestPacMans = new ArrayList<>();
    	while(mutationRate < 0.15)
    	{
    		ArrayList<MyPacMan> pacMans = createNPacMans(numberOfDifferentPacMans);
    		ArrayList<Integer> averageFitnessPerGeneration = new ArrayList<Integer>();
        	for (int i = 0; i < runs; i++) {
    			calculateFitness(numberGamesPerPacMan, pacMans);
    			ArrayList<MyPacMan> nFittestPacMans = nFittestPacMans(pacMans.toArray(new MyPacMan[pacMans.size()]), numberOfDifferentPacMans/2);
    			
    			int fitnessSum = 0;
    			for (MyPacMan myPacMan : nFittestPacMans) {
    				fitnessSum += myPacMan.fitness;
    			}
    			averageFitnessPerGeneration.add(fitnessSum/nFittestPacMans.size());
    			
    			//stop generating new generation on last run
    			if(i == runs-1)
    				break;
    			
    			pacMans = generateNextGeneration(nFittestPacMans, fitnessSum);
    			pacMans.addAll(0, nFittestPacMans);
    			for (int j = 0; j < pacMans.size(); j++) {
    				MyPacMan pacMan = new MyPacMan();
    				pacMan.setProbabilities(mutate(pacMans.get(j), mutationRate, mutationStepSizeUpperLimit).getProbabilities());
    				pacMans.set(j, pacMan);
    			}
    			
    	        //printStrategyProbabilities(nFittestPacMans);

        	}
        	double averageFitnessGain = 0;
        	for(int i=0; i < averageFitnessPerGeneration.size()-1; i++)
        	{
        		averageFitnessGain += (averageFitnessPerGeneration.get(i+1).intValue() - averageFitnessPerGeneration.get(i).intValue()); 
        	}

        	
        	System.out.println("mutationRate: "+ mutationRate);
        	System.out.println("averageFitnessGain: "+ averageFitnessGain);
        	System.out.println("averageFitness: "+ averageFitnessPerGeneration.get(averageFitnessPerGeneration.size()-1));
        	if(averageFitnessGain > bestFitnessGain)
        	{
        		bestFitnessGain = averageFitnessGain;
        		bestMutationRate = mutationRate;
        		bestFitness = averageFitnessPerGeneration.get(averageFitnessPerGeneration.size()-1);
        		bestPacMans = pacMans;
        	}
        	mutationRate += 0.01;
    	}
    	
    	System.out.println("Best mutationRate: "+ bestMutationRate);
    	System.out.println("Best averageFitnessGain: "+ bestFitnessGain);
    	System.out.println("Best Fitness: "+ bestFitness);
    	String listSavePath = "C:/Daten/pacmans.list";
    	savePacManList(bestPacMans, listSavePath);
//        Executor executor = new Executor(true, true);
//        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
//        
//        controllers.put(GHOST.INKY, new Inky());
//        controllers.put(GHOST.BLINKY, new Blinky());
//        controllers.put(GHOST.PINKY, new Pinky());
//        controllers.put(GHOST.SUE, new Sue());
//        
//        MyPacMan pacMan = new MyPacMan();
//        pacMan.setProbabilities(nFittestPacMans(pacMans.toArray(new MyPacMan[pacMans.size()]),1).get(0).getProbabilities());
//        //printStrategyProbabilities(pacMans);
//        executor.runGameTimed(pacMan, new MASController(controllers), true);
      //executor.runExperiment(pacMan, new MASController(controllers), 1, "", 4000);
    }

	private static void savePacManList(ArrayList<MyPacMan> bestPacMans, String listSavePath) {
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

	private static void printStrategyProbabilities(ArrayList<MyPacMan> pacMans) {
		for (int j = 0; j < pacMans.get(0).getProbabilities().size(); j++) {
			System.out.println("State Name: " + pacMans.get(0).getProbabilities().get(j).m_stateString);
			System.out.print("Best PacMan probabilities (No."+j+"):[");
		    for (int k = 0; k < pacMans.get(0).getProbabilities().get(j).getProbability().getNumberOfProbabilities(); k++) {
		        System.out.print(pacMans.get(0).getProbabilities().get(j).getProbability().getProbability(k)+", ");
			}
		    System.out.print("]\n\n");
		}
	}

	private static ArrayList<MyPacMan> generateNextGeneration(ArrayList<MyPacMan> nFittestPacMans, int fitnessSum) {
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

	private static void calculateFitness(int numberGamesPerPacMan, ArrayList<MyPacMan> pacMans) {
    	for(int i = 0; i < pacMans.size(); i++){
    		int fitness = 0;
    		MyPacMan current_pacMan = pacMans.get(i);
    		for(int j=0; j < numberGamesPerPacMan; j++)
    		{
    			//System.out.println("Pacman #"+i+", game #"+j+" started");
    			notMain(new String[0], current_pacMan);
    			
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
	private static ArrayList<MyPacMan> createNPacMans(int n)
	{
		ArrayList<MyPacMan> pacMans = new ArrayList<>();
		for(int i=0; i < n; i++)
			pacMans.add(new MyPacMan());
		return pacMans;
	}
	private static MyPacMan childPacMan(ArrayList<MyPacMan> nFittestPacMans, Random rand, int i, int j) {
		ArrayList<ProbabilityByState> probs1 = nFittestPacMans.get(i).getProbabilities();
		ArrayList<ProbabilityByState> probs2 = nFittestPacMans.get(j).getProbabilities();
		ArrayList<ProbabilityByState> finalProbs = nFittestPacMans.get(j).getProbabilities();
		for (int k = 0; k < probs1.size(); k++) {
			if (probs1.get(k).counter != 0 && probs2.get(k).counter != 0) {
				if(rand.nextDouble() <= 0.5)
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
    	return list;
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
        			double stepSize = rand.nextDouble();
        			while(stepSize > mutationStepSizeUpperLimit)
        				stepSize = rand.nextDouble();
        			if(rand.nextDouble() <= 0.5)
        				stepSize = -stepSize;
        			double newProbability = prob.getProbability().getProbability(j)+stepSize;
        			prob.getProbability().setProbability(j, newProbability);
        		}
    		}
    		prob.getProbability().normalizeProbability();
		}
    	pacman.setProbabilities(probs);
    	return pacman;
    }
}

