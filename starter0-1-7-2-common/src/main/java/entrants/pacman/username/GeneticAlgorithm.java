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

//import examples.StarterGhostComm.Blinky;
//import examples.StarterGhostComm.Inky;
//import examples.StarterGhostComm.Pinky;
//import examples.StarterGhostComm.Sue;
import entrants.ghosts.username.Blinky;
import entrants.ghosts.username.Inky;
import entrants.ghosts.username.Pinky;
import entrants.ghosts.username.Sue;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.GHOST;

public class GeneticAlgorithm {

	/*@brief old Main function sligthly changed
	 *@input runExperiment true if no GUI should be shown for fast training, false will show the GUI
	 *@input pacMan The PacMan that should play the game
	 *@returns nothing, but PacMans will keep internal values like fitness
	*/
	public static void notMain(boolean runExperiment, MyPacMan pacMan) {
        Executor executor = new Executor(true, true);
        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);
        
        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());
      
        if(runExperiment)
        	executor.runExperiment(pacMan, new MASController(controllers), 1, "", 4000);//no gui
        else
        	executor.runGameTimed(pacMan, new MASController(controllers), true); //show gui
    }
	
	/*@brief Creates new PacMans that use the probabilities of the old PacMans.
	 * Used for making sure that internal static values and state counters are reset before playing again 
	 *@input pacmansToReset List of old PacMans, which probabilities should be used
	 *@returns List of newly created PacMan that use the same probabilities as the old PacMans
	*/
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
	
	
	/*@brief Function to let a set of PacMans play the game n times. 
	 * The PacMans will store internal values like avergae fitness. 
	 *@input numberGamesPerPacMan Number of games each PacMan should play
	 *@returns nothing, but PacMans will keep internal values like average fitness
	*/
	public static void calculateFitness(int numberGamesPerPacMan, ArrayList<MyPacMan> pacMans) 
	{
    	for(int i = 0; i < pacMans.size(); i++)
    	{
    		double fitness = 0;
    		MyPacMan current_pacMan = pacMans.get(i);
    		for(int j=0; j < numberGamesPerPacMan; j++)
    		{
    			GeneticAlgorithm.notMain(true, current_pacMan); //play the game
    			fitness += current_pacMan.fitness; //add fitness from last played game
    			
    			if(j == numberGamesPerPacMan -1 ) //stop resetting PacMans if this was the last game
    				break;
    			
    			//reset PacMan and her internal values
    			ArrayList<ProbabilityByState> probs = current_pacMan.getProbabilities();
    			current_pacMan = new MyPacMan();
    			current_pacMan.setProbabilities(probs);
    		}
    		fitness /= (double)numberGamesPerPacMan; //calculate average fitness
    		current_pacMan.fitness = fitness; //set average fitness before returning
    	}
	} 
   

	/*@brief Function to simply add up the fitness of all PacMans in a list. 
	 *@input generation List of PacMans which fitness should be summed up
	 *@returns Sum of  the fitness of all PacMans in the list
	*/
	public static double calculateFitnessSumOfGeneration(ArrayList<MyPacMan> generation) {
		double fitnessSum = 0;
		for (MyPacMan myPacMan : generation) 
		{
			fitnessSum += myPacMan.fitness;	
		}
		return fitnessSum;
	}
	
	/*@brief Saves a list of PacMans to a file 
	 *@input list of PacMans to save
	 *@input listSavePath 
	 *@returns nothing
	*/
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
	
	/*@brief Mutates a list of PacMans 
	 *@input pacmans list of PacMans to mutate
	 *@input mutationRate probability of mutation in percent, ranging from 0 to 1
	 *@input mutationStepSizeUpperLimit maximum intensity of mutation in percent, ranging from 0 to 1
	 *@returns List of mutated PacMans
	*/
	 public static ArrayList<MyPacMan> mutate(ArrayList<MyPacMan> pacmans, double mutationRate, double mutationStepSizeUpperLimit)
	 {
		 for(int i =0; i < pacmans.size(); i++)
		 {
			 pacmans.set(i, mutate(pacmans.get(i), mutationRate, mutationStepSizeUpperLimit));
		 }
		 return pacmans;
	 }
		
	
	 /*@brief Mutates a single PacMan 
		 *@input pacman PacMan to mutate
		 *@input mutationRate probability of mutation in percent, ranging from 0 to 1
		 *@input mutationStepSizeUpperLimit maximum intensity of mutation in percent, ranging from 0 to 1
		 *@returns mutated PacMans
		*/
	 public static MyPacMan mutate(MyPacMan pacman, double mutationRate, double mutationStepSizeUpperLimit)
	    {
	    	ArrayList<ProbabilityByState> probs = pacman.getProbabilities();
	    	Random rand = new Random();
	    	for (int i = 0; i < probs.size(); i++) {
	    		ProbabilityByState prob = probs.get(i);//gets specific state with probabilities for this state
	    		
	    		//only mutate states that occured (counter > 0) the last time this PacMan played
	    		if(prob.counter == 0)
	    			continue;
	    		
	    		//iterate through probabilities for the strategies
	    		for(int j=0; j < prob.getProbabilityObject(false).getNumberOfProbabilities(); j++)
	    		{
	    			if(rand.nextDouble() <= mutationRate)// if true -> mutate
	        		{
	        			double stepSize = rand.nextDouble()*mutationStepSizeUpperLimit; //get value between 0 and mutationStepSizeUpperLimit to still allow small mutations 
	    				//double stepSize = mutationStepSizeUpperLimit;
	        			if(rand.nextDouble() <= 0.5) //50% chance for this probability to decrease instead of increase
	        				stepSize = -stepSize;
	        			
	        			//set new probability
	        			double newProbability = prob.getProbabilityObject(false).getProbability(j)+stepSize;
	        			prob.getProbabilityObject(false).setProbability(j, newProbability);
	        		}
	    		}
	    		//sum of all probabilities should be 100% percent, so we need to normalize
	    		prob.getProbabilityObject(false).normalizeProbability();
			}
	    	pacman.setProbabilities(probs);
	    	return pacman;
	    }
	
	 /*@brief Select the n fittest PacMans from a list of PacMans 
		 *@input pacMans List of PacMans to select from
		 *@input n Number of PacMans to return
		 *@returns List of the n PacMans with highest fitness
		*/
	 public static ArrayList<MyPacMan> nFittestPacMans(MyPacMan[] pacMans, int n)
	    {
	    	Arrays.sort(pacMans, new Comparator<MyPacMan>(){ //sort PacMans according to their fitness
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
	    	Collections.reverse(list); //fittest PacMans should be first in the list
	    	return list;
	    }
	
	 /*@brief Creates a list of newly created PacMans 
		 *@input n Number of PacMans to create
		 *@returns List of  n newly created PacMans
		*/
	 public static ArrayList<MyPacMan> createNewGeneration(int n)
		{
		 	ArrayList<MyPacMan> pacMans = createNPacMans(n);
		 	return pacMans;
		}
	 
	 /*@brief Creates a list of newly created PacMans 
		 *@input n Number of PacMans to create
		 *@returns List of  n newly created PacMans
		*/
	private static ArrayList<MyPacMan> createNPacMans(int n)
	{
		ArrayList<MyPacMan> pacMans = new ArrayList<>();
		for(int i=0; i < n; i++)
			pacMans.add(new MyPacMan());
		return pacMans;
	}
	
	/*@brief Creates a child PacMan out of two parent PacMans
	 *@input nFittestPacMans list of all PacMans
	 *@input i index of the first parent PacMan
	 *@input j index of the second parent PacMan 
	 *@returns Child PacMan out of the two given parent PacMans
	*/
	public static MyPacMan childPacMan(ArrayList<MyPacMan> nFittestPacMans, Random rand, int i, int j) {
		ArrayList<ProbabilityByState> probs1 = nFittestPacMans.get(i).getProbabilities(); //get first parent PacMan
		ArrayList<ProbabilityByState> probs2 = nFittestPacMans.get(j).getProbabilities(); //get second parent PacMan
		ArrayList<ProbabilityByState> finalProbs = nFittestPacMans.get(j).getProbabilities(); //list of states and probabilities for the child PacMan
		
		//iterate through ProbabilityByState Objects. 
		//Each ProbabilityByState Object contains an unique string for identification of a state.
		//They also contain the probabilities for the different strategies for the corresponding state.
		for (int k = 0; k < probs1.size(); k++) 
		{
			//the state corresponding to this state object occurred when both parents played the game
			//we use the occurrence counter of the ProbabilityByState Objects to select from which parent the child
			//should inherit it's probabilities for this state
			if (probs1.get(k).counter != 0 && probs2.get(k).counter != 0) 
			{
				//get sum of all occurrence counters of all states
				int stateCounterSum_first = nFittestPacMans.get(i).getStateCounterSum();
				int stateCounterSum_second = nFittestPacMans.get(j).getStateCounterSum();
				
				//compute influence of this state to the fitness of both parents
				//we assume that this state is more responsible for the current fitness of the PacMans 
				//if it occurred more often because then it's probabilities were used more often
				double influenceOfThisState_first = probs1.get(k).counter/stateCounterSum_first;
				double influenceOfThisState_second = probs2.get(k).counter/stateCounterSum_second;
				
				//get the amount of fitness the parent PacMans got because of this state's probabilities 
				double fitnessState_first = influenceOfThisState_first * nFittestPacMans.get(i).fitness;
				double fitnessState_second = influenceOfThisState_second * nFittestPacMans.get(j).fitness;
				
				Random rand2 = new Random();
				if(fitnessState_first > fitnessState_second) //amount of fitness for first parent PacMan is higher -> child inherits probabilities from first parent PacMan
				{
					finalProbs.set(k, probs1.get(k));
					
					if(rand2.nextDouble() < 0.1) //10% probability for crossOver
					{
						ProbabilityByState p = crossoverOnProbabilitiesLevel(finalProbs.get(k), probs2.get(k));
						p.normalizeProbabilities();//sum of probabilities should always be 100%
						finalProbs.set(k, p);
					}
					
				}
				else //amount of fitness for second parent PacMan is higher -> child inherits probabilities from second parent PacMan
				{
					if(rand2.nextDouble() < 0.1)//10% probability for crossOver
					{
						finalProbs.set(k, probs2.get(k));
						ProbabilityByState p = crossoverOnProbabilitiesLevel(finalProbs.get(k), probs1.get(k));
						p.normalizeProbabilities(); //sum of probabilities should always be 100%
						finalProbs.set(k, p);
					}
				}
					
			}
			//the state corresponding to this state object occurred only when the second parent PacMan played
			// -> child should inherit probabilities of the second parent PacMan for this state
			if (probs1.get(k).counter == 0 && probs2.get(k).counter != 0) 
				finalProbs.set(k, probs2.get(k));
			
			//the state corresponding to this state object occurred only for the first parent PacMan
			// -> child should inherit probabilities of the first parent PacMan for this state
			if (probs1.get(k).counter != 0  && probs2.get(k).counter == 0)
				finalProbs.set(k, probs1.get(k));
		
		}
	MyPacMan current_pacMan = new MyPacMan();
	current_pacMan.setProbabilities(finalProbs);
		return current_pacMan;
	}
	
	/*@brief Exchanges single probabilities (50% chance) of one ProbabilityByState with probabilities of another ProbabilityByState Object
	 *@input prob1 The ProbabilityByState Object that should receive probabilities of the second ProbabilityByState Object
	 *@input prob2 The ProbabilityByState Object that should provide probabilities for the first ProbabilityByState Object 
	 *@returns The changed ProbabilityByState Object
	*/
	public static ProbabilityByState simpleCrossOver(ProbabilityByState prob1, ProbabilityByState prob2)
	{
		Random rand = new Random();
		for(int i = 0; i < prob1.getNumberOfProbabilities(); i++)
		{
			if(rand.nextDouble() <= 0.5)// 50% chance to receive probability of the second ProbabilityByState Object
			{
				prob1.setProbability(i, prob2.getProbability(i));
			}
		}
		return prob1;
	}
	
	/*@brief Adapts the mutationStepSize by a linear function
	 *@input fitnessGoal Fitness that should be reached. When this Fitness is reached, the returned new mutationStepSize will be mutationStepSizeMin.
	 *@input mutationStepSizeMax Maximum mutationStepSize that will be returned if the fitness is 0 
	 *@input mutationStepSizeMin Minimum mutationStepSize that will be returned if the fitness reached the fitnessGoal
	 *@input averageFitnessOfCurrentGeneration Average fitness of the current generation
	 *@returns The linear adapted new mutationStepSize  
	*/
	public static double adaptMutationStepSizeLinear(int fitnessGoal, double mutationStepSizeMax, double mutationStepSizeMin, int averageFitnessOfCurrentGeneration)
	{
		double newMutationStepSize = 0;
		int diff = Math.abs(fitnessGoal - averageFitnessOfCurrentGeneration);
		double onePercent = fitnessGoal / (mutationStepSizeMax*100);
		newMutationStepSize = (diff/onePercent)/100;
		newMutationStepSize = (newMutationStepSize < mutationStepSizeMin) ? mutationStepSizeMin : newMutationStepSize;
		return newMutationStepSize;
	}
	
	/*@brief Loads a list of saved PacMans
	 *@input path FilePath to the saved PacMans
	 *@returns The list of the loaded PacMans 
	*/
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
	
	/*@brief Loads a list of ProbabilityByState Objects
	 *@input path FilePath to the saved list
	 *@returns The list of ProbabilityByState Objects 
	*/
	public static ArrayList<ProbabilityByState> loadPacManProbabilities(String path)
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
		
		 return pacManList.get(0);
		 
	}
	
	/*@brief Loads a single saved PacMan
	 *@input path FilePath to the saved PacMan
	 *@returns The loaded PacMan
	*/
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
	
	/*@brief Creates the next generation
	 *@input nFittestPacMans Parent PacMans that should be used for creation of the new generation
	 *@input fitnessSum Sum of the fitness of the parent PacMans
	 *@input maxNumberChilds Maximum number of childs that should be created
	 *@returns The created child PacMans
	*/
	public static ArrayList<MyPacMan> generateNextGeneration(ArrayList<MyPacMan> nFittestPacMans, double fitnessSum, int maxNumberChilds) {
		Random rand = new Random();
		ArrayList<MyPacMan> newGeneration = new ArrayList<MyPacMan>();
		// PacMan roulette
		for (int i = 0; i < nFittestPacMans.size(); i++) {
			for (int j = 0; j < nFittestPacMans.size(); j++) {
			 	if(rand.nextDouble() <= nFittestPacMans.get(i).fitness/fitnessSum) //chance to create a child is the influence of the PacMan's fitness to the Sum of all fitnesses
			 	{
			 		MyPacMan current_pacMan = childPacMan(nFittestPacMans, rand, i, j);//create child
			 		newGeneration.add(current_pacMan);
			 		if(newGeneration.size() == maxNumberChilds)
			 			break;
			 	}
			}
			if(newGeneration.size() == maxNumberChilds)
	 			break;
		}
		return newGeneration;
	}
	
	/*@brief General method for training populations of PacMans.
	 *@returns nothing
	*/
	public static void train() {
		//training parameters
		int numberGamesPerPacMan = 100; ///number of games each Mrs. PacMan should play to measure her fitness 
    	int numberOfDifferentPacMans = 20; //number of different Mrs. PacMans
    	double mutationRate = 0.10; // probability of mutation in percent from 0 to 1
    	double mutationStepSizeUpperLimit = 0.2; //maximum intensity of mutation in percent from 0 to 1
    	final int runs = 10; //number of generations for training, currently not used
    	String listSavePath = "C:/Daten/pacman/"; //path to save the PacMans
    	
    	//variables to store values of current and last generation
    	double averageFitnessLastGeneration = 0;
    	double lastGenerationFitnessSum = 0;
    	ArrayList<MyPacMan> currentGeneration;
    	ArrayList<MyPacMan> lastGeneration = new ArrayList<>();
    	int badGenCounter = 0; //counter for counting consecutive bad generations
    	int saveCounter = 0; //counter for preventing name collisions on saving
    	
    	//initialize initial population
    	currentGeneration = GeneticAlgorithm.createNewGeneration(numberOfDifferentPacMans);	
    	
    	//currently runs until mutationRate and mutationStepSize is 0
       	for (int i = 0; true || i < runs ; i++) 
       	{
       		//Let the population play to determine their fitness
       		GeneticAlgorithm.calculateFitness(numberGamesPerPacMan, currentGeneration);
       		
       		//choose best 50% of Mrs. PacMans from current generation
       		currentGeneration = GeneticAlgorithm.nFittestPacMans(currentGeneration.toArray(new MyPacMan[currentGeneration.size()]), numberOfDifferentPacMans/2);
    		
       		//calculate average fitness of current generation
       		double fitnessSum = GeneticAlgorithm.calculateFitnessSumOfGeneration(currentGeneration);
       		double averageFitnessOfGeneration = fitnessSum/currentGeneration.size();
    		
    		//if last generation fitness was better, drop this generation, but keep good Mrs. PacMans
    		if(averageFitnessOfGeneration < averageFitnessLastGeneration && lastGeneration.size() > 0)
    		{
    			saveCounter++;
    			badGenCounter++;
    			System.out.println("badGenCounter: "+badGenCounter);
    			
    			//get Mrs. PacMans from current generations that were better than the average of the last generation 
    			ArrayList<MyPacMan> overAveragePacMans = new ArrayList<>();
    			for(MyPacMan p : currentGeneration)
    			{
    				if(p.fitness > averageFitnessLastGeneration)
    					overAveragePacMans.add(p);
    			}
    			
    			//replace old pacmans that are worse than the new ones
    			for(int j = 0; j < overAveragePacMans.size(); j++)
    			{
    				MyPacMan newPacMan = overAveragePacMans.get(j);
    				
    				int idx = -1;
    				for(int k = 0; k < lastGeneration.size(); k++)
        			{
        				MyPacMan oldPacMan = lastGeneration.get(k);
        				if(oldPacMan.fitness < newPacMan.fitness)
        				{
        					idx = k;
        					break;
        				}
        			}
    				if(idx > -1)
    					lastGeneration.set(idx, newPacMan);
    			}
    			
    			//recalculate average fitness of lastGeneration
    	 		lastGenerationFitnessSum = GeneticAlgorithm.calculateFitnessSumOfGeneration(lastGeneration);
           		averageFitnessLastGeneration = lastGenerationFitnessSum/lastGeneration.size();
    		
           	//if population changed -> save PacMans	
            // since current generation still is worse than last, make last generation the current one
           	if(overAveragePacMans.size() > 0)	
           		GeneticAlgorithm.savePacManList(lastGeneration, listSavePath+"fitness_"+averageFitnessLastGeneration+"counter_"+saveCounter);
           		
    			currentGeneration = new ArrayList<>(); 
    			currentGeneration.addAll(lastGeneration); // since current generation still is worse than last, make last generation the current one
    			
    			//if the population didn't improve for 10 generations, change mutationRate and/or mutationStepSize
    			if(badGenCounter > 10)
    			{
    				GeneticAlgorithm.savePacManList(currentGeneration, listSavePath+"fitness_"+averageFitnessLastGeneration+"counter_"+saveCounter);
    				currentGeneration = GeneticAlgorithm.resetPacMans(currentGeneration);
    				System.out.println("average end fitness: "+averageFitnessLastGeneration);
    				mutationStepSizeUpperLimit -= 0.05;
    				if(mutationStepSizeUpperLimit <= 0.01)// 0.01 because it's a double that often becomes something like 0.000000004 when it's supposed to be 0
    				{
    					mutationStepSizeUpperLimit = 0.2;
    					mutationRate -= 0.05;
    					if(mutationRate <= 0.01)// 0.01 because it's a double that often becomes something like 0.000000004 when it's supposed to be 0
    						break; //end training
    				}
    				System.out.println("new mutationRate: "+mutationRate);
    				System.out.println("new MutationStepSize: "+mutationStepSizeUpperLimit);
    				badGenCounter = 0;
    			}
    			//create childs, mutate and reset static variables used
    			currentGeneration.addAll(GeneticAlgorithm.generateNextGeneration(currentGeneration, lastGenerationFitnessSum, numberOfDifferentPacMans/2));
    			currentGeneration = GeneticAlgorithm.mutate(currentGeneration, mutationRate, mutationStepSizeUpperLimit);
    			currentGeneration = GeneticAlgorithm.resetPacMans(currentGeneration);
    			
    			System.out.println("Generation dropped");
    			System.out.println("current mutationRate: "+mutationRate);
				System.out.println("current MutationStepSize: "+mutationStepSizeUpperLimit);
    			continue; //continue training with last generation
    		}
    		
    		// //This will only be reached if current generation is better than last generation 
    		//or last generation doesn't exist yet, so we are keeping this generation as the new last generation
    		lastGeneration = new ArrayList<>();
    		lastGeneration.addAll(currentGeneration);
    		averageFitnessLastGeneration = averageFitnessOfGeneration;
    		lastGenerationFitnessSum = fitnessSum;
    		badGenCounter = 0; //reset counter because this generation is better than last generation
    		
    		//save new best generation
    		GeneticAlgorithm.savePacManList(lastGeneration, listSavePath+"fitness_"+averageFitnessOfGeneration+"counter_"+saveCounter);
    		System.out.println("Average Fitness:" + averageFitnessOfGeneration);
    		System.out.println("MuationRate:" + mutationRate);
    		System.out.println("New mutationStepUpperLimit:" + mutationStepSizeUpperLimit);
    			
    		//stop generating new generation on last run
    		//if(i == runs-1)
    		//	break;
    		
    		//create childs, mutate and reset static variables used
    		currentGeneration = GeneticAlgorithm.generateNextGeneration(currentGeneration, fitnessSum, numberOfDifferentPacMans/2);
    		currentGeneration.addAll(0, lastGeneration); //at this point the last generation is the one we had just now
    		currentGeneration = GeneticAlgorithm.mutate(currentGeneration, mutationRate, mutationStepSizeUpperLimit);
    		currentGeneration = GeneticAlgorithm.resetPacMans(currentGeneration);
       	}
	}
	
	/*@brief Exchanges a block of probabilities of one ProbabilityByState with a block of probabilities of another ProbabilityByState Object
	 *@input prob1 The ProbabilityByState Object that should receive probabilities of the second ProbabilityByState Object
	 *@input prob2 The ProbabilityByState Object that should provide probabilities for the first ProbabilityByState Object 
	 *@returns The changed ProbabilityByState Object
	*/
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
    		Probability1.setProbability(i, Probability2.getProbability(i));
    		//Probability2.setProbability(i, tmp.getProbability(i));
		}
		return Probability1;
    }
}
