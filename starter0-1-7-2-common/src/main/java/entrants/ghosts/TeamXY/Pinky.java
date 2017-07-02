package entrants.ghosts.TeamXY;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Pinky extends IndividualGhostController {

	public MyGhost ghostBase;
	
    public Pinky() {
        super(Constants.GHOST.PINKY);
        ghostBase = new MyGhost(Constants.GHOST.PINKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        //return null;
        return ghostBase.getMove(game, timeDue);
    }
}
