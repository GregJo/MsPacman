package entrants.ghosts.username;

import entrants.pacman.username.*;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;
/**
 * Created by Piers on 11/11/2015.
 */
public class Blinky extends IndividualGhostController {

	public MyGhost ghostBase;
	
    public Blinky() {
        super(Constants.GHOST.BLINKY);
        ghostBase = new MyGhost(Constants.GHOST.BLINKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return ghostBase.getMove(game, timeDue);
    }
}
