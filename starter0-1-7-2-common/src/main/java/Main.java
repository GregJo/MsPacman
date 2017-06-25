
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
    	int numberGamesPerPacMan = 10;
    	int numberOfDifferentPacMans = 10;
    	double mutationRate = 0.15;
    	double mutationStepSizeUpperLimit = 0.55;
    	final int runs = 10;
    	
    	ArrayList<MyPacMan> pacMans = new ArrayList<MyPacMan>();
    	
    	for (int i = 0; i < runs; i++) {
			calculateFitness(numberGamesPerPacMan, numberOfDifferentPacMans, pacMans);
			
			ArrayList<MyPacMan> nFittestPacMans = nFittestPacMans(pacMans.toArray(new MyPacMan[pacMans.size()]), numberOfDifferentPacMans/2);
			
			int fitnessSum = 0;
			
			for (MyPacMan myPacMan : nFittestPacMans) {
				fitnessSum += myPacMan.fitness;
			}
			
			pacMans = generateNextGeneration(nFittestPacMans, fitnessSum);
			pacMans.addAll(0, nFittestPacMans);
			for (MyPacMan pacMan : pacMans) {
				pacMan = mutate(pacMan, mutationRate, mutationStepSizeUpperLimit);
			}
			
    	}
        Executor executor = new Executor(true, true);
        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
        
        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
        
        MyPacMan pacMan = new MyPacMan();
        pacMan.setProbabilities(nFittestPacMans(pacMans.toArray(new MyPacMan[pacMans.size()]),1).get(0).getProbabilities());
        executor.runGameTimed(pacMan, new MASController(controllers), true);
        System.out.flush();
      //executor.runExperiment(pacMan, new MASController(controllers), 1, "", 4000);
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

	private static void calculateFitness(int numberGamesPerPacMan, int numberOfDifferentPacMans, ArrayList<MyPacMan> pacMans) {
    	for(int i = 0; i < numberOfDifferentPacMans; i++){
    		pacMans.add(new MyPacMan());
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
    	for (ProbabilityByState prob : probs) {
    		
    		for(int i=0; i < prob.getProbability().getNumberOfProbabilities(); i++)
    		{
    			if(rand.nextDouble() <= mutationRate)
        		{
        			double stepSize = rand.nextDouble();
        			while(stepSize > mutationStepSizeUpperLimit)
        				stepSize = rand.nextDouble();
        			if(rand.nextDouble() <= 0.5)
        				stepSize = -stepSize;
        			double newProbability = prob.getProbability().getProbability(i)+stepSize;
        			prob.getProbability().setProbability(i, newProbability);
        		}
    		}
    		prob.getProbability().normalizeProbability();
		}
    	pacman.setProbabilities(probs);
    	return pacman;
    }
}

