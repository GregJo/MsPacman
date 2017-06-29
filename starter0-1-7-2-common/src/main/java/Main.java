
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
import java.util.Collections;
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
    	int numberGamesPerPacMan = 20;
    	int numberOfDifferentPacMans = 20;
    	double mutationRate = 0.05;
    	double mutationStepSizeUpperLimit = 0.05;
    	final int runs = 20;
    	
    	double bestMutationRate = -1;
    	double bestFitnessGain = -1;
    	double bestFitness = -1;
    	ArrayList<MyPacMan> bestPacMans = new ArrayList<>();
    	ArrayList<MyPacMan> pacMans = createNPacMans(numberOfDifferentPacMans);
    	
    	/*
    	ArrayList<Integer> averageFitnessPerRun = new ArrayList<Integer>();
    	for(int i=0; i < runs; i++)
    	{
    		calculateFitness(numberGamesPerPacMan, pacMans);
    		int fitnessSum = 0;
			for (MyPacMan myPacMan : pacMans) {
				fitnessSum += myPacMan.fitness;
			}
			averageFitnessPerRun.add(fitnessSum/pacMans.size());
			for (int j = 0; j < pacMans.size(); j++) {
				MyPacMan pacMan = new MyPacMan();
				pacMan.setProbabilities(pacMans.get(j).getProbabilities());
				pacMans.set(j, pacMan);
			}
    	}
    	double variance = 0;
    	for(int i=0; i < averageFitnessPerRun.size()-1; i++)
    	{
    		variance += Math.abs(averageFitnessPerRun.get(i) - averageFitnessPerRun.get(i+1));
    	}
    	variance /= runs-1;
    	System.out.println("Variance: "+variance);
    	*/
    	
    	
    	//while(mutationRate < 0.15)
    	//{
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
    			
    			ArrayList<MyPacMan> tmp = nFittestPacMans;
    			pacMans = nextGenerationWithProbabilitiesCrossover(nFittestPacMans,0.2);
    			pacMans.addAll(0, tmp);
    			for (int j = 0; j < pacMans.size(); j++) {
    				MyPacMan pacMan = new MyPacMan();
    				pacMan.setProbabilities(mutate(pacMans.get(j), mutationRate, mutationStepSizeUpperLimit).getProbabilities());
    				pacMans.set(j, pacMan);
    			}
    			
    	        //printStrategyProbabilities(nFittestPacMans);
    			System.out.println("run: "+ i);
            	System.out.println("averageFitness: "+ averageFitnessPerGeneration.get(averageFitnessPerGeneration.size()-1));
            	
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
        	//break;
        	//mutationRate += 0.01;
    	//}
    	
    	System.out.println("Best mutationRate: "+ bestMutationRate);
    	System.out.println("Best averageFitnessGain: "+ bestFitnessGain);
    	System.out.println("Best Fitness: "+ bestFitness);
    	String listSavePath = "C:/Users/Grigori/Desktop/pacman.list";
    	savePacManList(bestPacMans, listSavePath);
        Executor executor = new Executor(true, true);
        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
        
        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
        
        MyPacMan pacMan = new MyPacMan();
        ArrayList<MyPacMan> fittest = nFittestPacMans(pacMans.toArray(new MyPacMan[pacMans.size()]),1);
        pacMan.setProbabilities(fittest.get(0).getProbabilities());
        //printStrategyProbabilities(pacMans);
        executor.runGameTimed(pacMan, new MASController(controllers), true);
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
		    for (int k = 0; k < pacMans.get(0).getProbabilities().get(j).getNumberOfProbabilities(); k++) {
		        System.out.print(pacMans.get(0).getProbabilities().get(j).getProbability(k)+", ");
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
    
    public static MyPacMan mutate(MyPacMan pacman, double mutationRate, double mutationStepSizeUpperLimit)
    {
    	ArrayList<ProbabilityByState> probs = pacman.getProbabilities();
    	Random rand = new Random();
    	for (int i = 0; i < probs.size(); i++) {
    		ProbabilityByState prob = probs.get(i);
    		for(int j=0; j < prob.getNumberOfProbabilities(); j++)
    		{
    			if(rand.nextDouble() <= mutationRate)
        		{
        			//double stepSize = rand.nextDouble()*mutationStepSizeUpperLimit;
    				double stepSize = mutationStepSizeUpperLimit;
        			if(rand.nextDouble() <= 0.5)
        				stepSize = -stepSize;
        			double newProbability = prob.getProbability(j)+stepSize;
        			prob.setProbability(j, newProbability);
        		}
    		}
    		prob.normalizeProbabilities();
		}
    	pacman.setProbabilities(probs);
    	return pacman;
    }
    
    public static ArrayList<MyPacMan> nextGenerationWithStrategiesCrossover(ArrayList<MyPacMan> nFittestPacMans, double crossoverProbability)
    {
		Random rand = new Random();
		
		for (int i = 0; i < nFittestPacMans.size(); i++) {
			int randomIdx = rand.nextInt(nFittestPacMans.size()-1);
			if (rand.nextDouble()<crossoverProbability) {
				nFittestPacMans.set(i, crossoverOnStrategiesLevel(nFittestPacMans.get(i), nFittestPacMans.get(randomIdx)));
			}
		}
		return nFittestPacMans;
    }
    
    public static ArrayList<MyPacMan> nextGenerationWithProbabilitiesCrossover(ArrayList<MyPacMan> nFittestPacMans, double crossoverProbability)
    {
		Random rand = new Random();
		
		ArrayList<MyPacMan> result = new ArrayList<>();
		
		for (int i = 0; i < nFittestPacMans.size(); i++) {
			ArrayList<ProbabilityByState> probabilities = nFittestPacMans.get(i).getProbabilities();
			ArrayList<ProbabilityByState> tmp = nFittestPacMans.get(i).getProbabilities();
			for (int j = 0; j < probabilities.size(); j++) {
				int randomIdx = rand.nextInt(probabilities.size()-1);
				if (rand.nextDouble()<crossoverProbability) {
					probabilities.set(j, crossoverOnProbabilitiesLevel(probabilities.get(j),tmp.get(randomIdx)));
				}
			}
			MyPacMan pacMan = new MyPacMan();
			pacMan.setProbabilities(probabilities);
			result.add(pacMan);
		}
		
		return result;
    }
    
    public static MyPacMan crossoverOnStrategiesLevel(MyPacMan pacMan1, MyPacMan pacMan2)
    {
    	Random rand = new Random();
    	int cutUpper = rand.nextInt(pacMan1.getProbabilities().size()-1);
    	int cutLower = rand.nextInt(pacMan1.getProbabilities().size()-1-cutUpper);
    	
    	MyPacMan tmp = pacMan1;
    	for (int i = cutLower; i < cutUpper; i++) {
    		int randomIdx = rand.nextInt(pacMan1.getProbabilities().size()-1); 
    		//pacMan1.getProbabilities().set(i, pacMan2.getProbabilities().get(randomIdx));
    		//pacMan2.getProbabilities().set(randomIdx, tmp.getProbabilities().get(i));
    		pacMan1.getProbabilities().set(i, pacMan2.getProbabilities().get(i));
    		pacMan2.getProbabilities().set(i, tmp.getProbabilities().get(i));
		}
    	return pacMan1;
    }
    
    public static ProbabilityByState crossoverOnProbabilitiesLevel(ProbabilityByState Probability1, ProbabilityByState Probability2)
    {
    	Random rand = new Random();
    	int cutUpper = rand.nextInt(Probability1.getNumberOfProbabilities()-1);
    	int cutLower = rand.nextInt(Probability1.getNumberOfProbabilities()-1-cutUpper);
    	
    	ProbabilityByState tmp = Probability1;
    	for (int i = cutLower; i < cutUpper; i++) {
    		//int randomIdx = rand.nextInt(Probability1.getNumberOfProbabilities()-1); 
    		//Probability1.setProbability(i, Probability2.getProbability(randomIdx));
    		//Probability2.setProbability(randomIdx, tmp.getProbability(i));
    		Probability1.setProbability(i, Probability2.getProbability(i));
    		Probability2.setProbability(i, tmp.getProbability(i));
		}
		return Probability1;
    }
}

