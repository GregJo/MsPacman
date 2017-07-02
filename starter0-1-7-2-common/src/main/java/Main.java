
import entrants.pacman.TeamXY.GeneticAlgorithm;
import entrants.pacman.TeamXY.MyPacMan;

/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    
    
    public static void main(String[] args) 
    {
    	//GeneticAlgorithm.train(); //we used this for training

    	//GeneticAlgorithm.trainGhosts(); //we used this for training
    	GeneticAlgorithm.notMain(false, new MyPacMan());
    } 
}

