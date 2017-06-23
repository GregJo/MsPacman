
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
import java.util.EnumMap;


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
       
       executor.runGameTimed(pacMan, new MASController(controllers), true);
      //executor.runExperiment(pacMan, new MASController(controllers), 1, "", 4000);
       
    }
    
    public static void main(String[] args) {
    	int numberGamesPerPacMan = 1;
    	int numberOfDifferentPacMans =1;
    	
    	MyPacMan[] pacMans = new MyPacMan[numberOfDifferentPacMans];
    	int[] all_fitness = new int[numberOfDifferentPacMans];
    	for(int i = 0; i < numberOfDifferentPacMans; i++){
    		pacMans[i] = new MyPacMan();
    		int fitness = 0;
    		MyPacMan current_pacMan = pacMans[i];
    		for(int j=0; j < numberGamesPerPacMan; j++)
    		{
    			//System.out.println("Pacman #"+i+", game #"+j+" started");
    			notMain(new String[0], current_pacMan);
    		//	System.out.println("fitness for this game: "+newFitness);
    			//fitness += newFitness; 
    			ArrayList<ProbabilityByState> probs = current_pacMan.getProbabilities();
    			current_pacMan = new MyPacMan();
    			current_pacMan.setProbabilities(probs);
    		}
    		fitness /= numberGamesPerPacMan;
    		//System.out.println("Pacman #"+i+" finished. Fitness: "+fitness);
    		all_fitness[i] = fitness;
    	}
    }
    
}

