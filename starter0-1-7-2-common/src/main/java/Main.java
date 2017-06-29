
import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;
import entrants.pacman.username.GeneticAlgorithm;
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
import java.util.Collections;
/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    
    
    public static void main(String[] args) 
    {
    	
    	int numberGamesPerPacMan = 1;
    	int numberOfDifferentPacMans = 10;
    	double mutationRate = 0.001;
    	double mutationStepSizeUpperLimit = 0.05;
    	final int runs = 20;
    	String listSavePath = "C:/Daten/pacman/";
    	
    	int averageFitnessLastGeneration = 0;
    	int lastGenerationFitnessSum = 0;
    	ArrayList<MyPacMan> currentGeneration;
    	ArrayList<MyPacMan> lastGeneration = new ArrayList<>();
    	
    	currentGeneration = GeneticAlgorithm.loadPacManList("C:/Daten/pacman/fitness_2720_run_2counter_0");//GeneticAlgorithm.createNewGeneration(numberOfDifferentPacMans);	
       	for (int i = 0; true || i < runs ; i++) 
       	{
       		GeneticAlgorithm.calculateFitness(numberGamesPerPacMan, currentGeneration);
       		currentGeneration = GeneticAlgorithm.nFittestPacMans(currentGeneration.toArray(new MyPacMan[currentGeneration.size()]), numberOfDifferentPacMans/2);
    			
    		int fitnessSum = GeneticAlgorithm.calculateFitnessSumOfGeneration(listSavePath, i, currentGeneration);
    		int averageFitnessOfGeneration = fitnessSum/currentGeneration.size();
    		
    		//if last generation fitness was better, drop this generation
    		if(averageFitnessOfGeneration < averageFitnessLastGeneration && lastGeneration.size() > 0)
    		{
    			ArrayList<MyPacMan> overAveragePacMans = new ArrayList<>();
    			for(MyPacMan p : currentGeneration)
    			{
    				if(p.fitness >= averageFitnessLastGeneration)
    					overAveragePacMans.add(p);
    			}
    			lastGeneration.addAll(0, overAveragePacMans);
    			currentGeneration = GeneticAlgorithm.resetPacMans(lastGeneration);
    			currentGeneration.addAll(GeneticAlgorithm.generateNextGeneration(currentGeneration, lastGenerationFitnessSum));
    			currentGeneration = GeneticAlgorithm.mutate(currentGeneration, mutationRate, mutationStepSizeUpperLimit);
    			
    			System.out.println("Generation dropped");
    			continue;
    		}
    		lastGeneration = currentGeneration;
    		averageFitnessLastGeneration = averageFitnessOfGeneration;
    		lastGenerationFitnessSum = fitnessSum;
    		
    	
    		
    	/*	{	//for range 1000 - 2000
    			int fitnessGoal = 2000;
    			int minFitness = 1000;
    			double mutationStepSizeMax = 0.05;
    			double mutationStepSizeMin = 0.01;
    			if(averageFitnessOfGeneration > minFitness)
    				mutationStepSizeUpperLimit = GeneticAlgorithm.adaptMutationStepSizeLinear(fitnessGoal, mutationStepSizeMax, mutationStepSizeMin, averageFitnessOfGeneration);
    		}*/
    		
    		System.out.println("Average Fitness:" + averageFitnessOfGeneration);
    		System.out.println("MuationRate:" + mutationRate);
    		System.out.println("New mutationStepUpperLimit:" + mutationStepSizeUpperLimit);
    			
    		//stop generating new generation on last run
    		//if(i == runs-1)
    		//	break;

    		currentGeneration = GeneticAlgorithm.generateNextGeneration(currentGeneration, fitnessSum);
    			
    		currentGeneration.addAll(0, lastGeneration); //at this point the last generation is the one we had just now
    		for (int j = 0; j < currentGeneration.size(); j++) 
    		{
    			MyPacMan pacMan = new MyPacMan();
    			pacMan.setProbabilities(GeneticAlgorithm.mutate(currentGeneration.get(j), mutationRate, mutationStepSizeUpperLimit).getProbabilities());
    			currentGeneration.set(j, pacMan);
    		}
       	}
        
       	
        MyPacMan pacMan = new MyPacMan();
        ArrayList<MyPacMan> fittest = GeneticAlgorithm.nFittestPacMans(currentGeneration.toArray(new MyPacMan[currentGeneration.size()]),1);
        pacMan.setProbabilities(fittest.get(0).getProbabilities());
        
       // pacMan = GeneticAlgorithm.loadPacMan("C:/Daten/pacman/fitness_1180_run_5counter_0");
        GeneticAlgorithm.notMain(false, pacMan);
   
    }

	

	private static void printPacManWithWaitAndEatNearestPowerPill(ArrayList<MyPacMan> nFittestPacMans) {
		int c = 0;
		for(MyPacMan p : nFittestPacMans)
		{
			c++;
			System.out.println("PacMan #"+c+": ");
			System.out.println("\tFitness: "+p.fitness);
			ArrayList<ProbabilityByState> probByStates = p.getProbabilities();
			for(ProbabilityByState probByState : probByStates)
			{
					System.out.println("State: "+probByState.m_stateString);
					System.out.println("\tWait "+probByState.getProbability().getProbability(0));
					System.out.println("\tEatNearestPowerPill "+probByState.getProbability().getProbability(1));
			}
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

	
	
   
}

