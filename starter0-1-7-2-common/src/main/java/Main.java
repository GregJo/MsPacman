
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
    	
    	int numberGamesPerPacMan = 20;
    	int numberOfDifferentPacMans = 10;
    	double mutationRate = 0.001;
    	double mutationStepSizeUpperLimit = 0.05;
    	final int runs = 10;
    	String listSavePath = "C:/Daten/pacman/";
    	
    	double averageFitnessLastGeneration = 0;
    	double lastGenerationFitnessSum = 0;
    	ArrayList<MyPacMan> currentGeneration;
    	ArrayList<MyPacMan> lastGeneration = new ArrayList<>();
    	
    	currentGeneration = GeneticAlgorithm.createNewGeneration(numberOfDifferentPacMans);	
       	for (int i = 0; i < runs ; i++) 
       	{
       		GeneticAlgorithm.calculateFitness(numberGamesPerPacMan, currentGeneration);
       		currentGeneration = GeneticAlgorithm.nFittestPacMans(currentGeneration.toArray(new MyPacMan[currentGeneration.size()]), numberOfDifferentPacMans/2);
    			
       		double fitnessSum = GeneticAlgorithm.calculateFitnessSumOfGeneration(listSavePath, i, currentGeneration);
       		double averageFitnessOfGeneration = fitnessSum/currentGeneration.size();
    		
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
             currentGeneration = lastGeneration;
             currentGeneration.addAll(GeneticAlgorithm.generateNextGeneration(currentGeneration, lastGenerationFitnessSum, 10));
             currentGeneration = GeneticAlgorithm.mutate(currentGeneration, mutationRate, mutationStepSizeUpperLimit);
             currentGeneration = GeneticAlgorithm.resetPacMans(currentGeneration);
             
             System.out.println("Generation dropped");
             continue;
            }
    		lastGeneration = currentGeneration;
    		averageFitnessLastGeneration = averageFitnessOfGeneration;
    		lastGenerationFitnessSum = fitnessSum;
    		
    		System.out.println("Average Fitness:" + averageFitnessOfGeneration);
    		System.out.println("MuationRate:" + mutationRate);
    		System.out.println("New mutationStepUpperLimit:" + mutationStepSizeUpperLimit);
    			
    		//stop generating new generation on last run
    		//if(i == runs-1)
    		//	break;

    		//currentGeneration = GeneticAlgorithm.generateNextGeneration(currentGeneration, fitnessSum);
    		currentGeneration = nextGenerationWithProbabilitiesCrossover(currentGeneration, 0.1);
    		currentGeneration.addAll(0, lastGeneration); //at this point the last generation is the one we had just now
    		currentGeneration = GeneticAlgorithm.mutate(currentGeneration, mutationRate, mutationStepSizeUpperLimit);
    		currentGeneration = GeneticAlgorithm.resetPacMans(currentGeneration);
       	}
        
       	
        MyPacMan pacMan = new MyPacMan();
        ArrayList<MyPacMan> fittest = GeneticAlgorithm.nFittestPacMans(currentGeneration.toArray(new MyPacMan[currentGeneration.size()]),1);
        pacMan.setProbabilities(fittest.get(0).getProbabilities());
        
       // pacMan = GeneticAlgorithm.loadPacMan("C:/Daten/pacman/fitness_1180_run_5counter_0");
        GeneticAlgorithm.notMain(false, pacMan);
   
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
    	int cutLower = rand.nextInt(pacMan1.getProbabilities().size()-1);
    	int cutUpper = rand.nextInt(pacMan1.getProbabilities().size()-1+cutLower);
    	if (cutUpper > pacMan1.getProbabilities().size()-1) {
    		cutUpper = pacMan1.getProbabilities().size()-1;
		}
    	
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
    	int cutLower = rand.nextInt(Probability1.getNumberOfProbabilities()-1);
    	int cutUpper = rand.nextInt(Probability1.getNumberOfProbabilities()-1+cutLower);
    	if (cutUpper > Probability1.getNumberOfProbabilities()-1) {
    		cutUpper = Probability1.getNumberOfProbabilities()-1;
		}
    	
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

