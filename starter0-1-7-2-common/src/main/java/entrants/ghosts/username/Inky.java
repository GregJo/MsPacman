package entrants.ghosts.username;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;

/**
 * Created by Piers on 11/11/2015.
 */
public class Inky extends IndividualGhostController {

	public MyGhost ghostBase;
	
    public Inky() {
        super(Constants.GHOST.INKY);
        ghostBase = new MyGhost(Constants.GHOST.INKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        //return null;
        return ghostBase.getMove(game, timeDue);
    }
}
