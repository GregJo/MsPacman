package entrants.ghosts.username;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;
import entrants.pacman.username.*;
/**
 * Created by Piers on 11/11/2015.
 */
public class Blinky extends IndividualGhostController {

	MyGhost ghostBase;
	
    public Blinky() {
        super(Constants.GHOST.BLINKY);
        ghostBase = new MyGhost(Constants.GHOST.BLINKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return ghostBase.getMove(game, timeDue);
    }
}
