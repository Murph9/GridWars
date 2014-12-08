import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;


//handles the spawning of objects, initalsed with the difficulty of the object

//////////////////////////
/*objects that can be spawned: (note the key, going to use GameEngine.<NAME> later)
 * S - SimpleSpinner
 * D - HomingDiamond
 * P - SplitingSquare
 * Y - ShySquare
 * N - SnakeHead
 * 
 * B - HomingButterfly
 * C - HomingSeeker
 * 
 * H - BlackHole
 * L - ShieldedClone
 * T - ConnectedTriangle
 * 
 * And their respective spawners
 * Other symbols: 
 *  s - spawners can spawn
 *  v - spawns from now on
 *  ^ - until here
 *  + - more frequent spawning
 *  # - count in wave
 */

////////////////////////////
//thinking about having "waves" of things spawn: (in order) (with swarms in the transition, either around the player or on the edge)	
	//maybe the transition is slower on easy difficulty?
	//correct gameplay should be (on as many levels as possible) easy/hard/easy/hard/...
/* Sv |  
 * Dv |
 *    | Ss
 *    | P
 *    | DsH
 *    | PY
 * Pv | 1L
 * Bv | 
 *           
 * 
 */



////////////////////////////
/* Rules:
 * ConnectedTriangle - should probably spawn with a friend
 * Blackholes - should only really have a maximum for 4 on the board at once 
 * 				(checking is easy, BlackHole.ALL_THIS.size())
 * 
 * Spawners - maybe like 16th the chance of spawning something
 * 
 * Powerups - need to be used
 */

public class SpawnHandler {
	
	private int diff;
	private Timer timer;

	public SpawnHandler(int diff) {
		this.diff = diff;
		if (this.diff == 0) //TODO, just removing the warning for now
			return;

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
//				not really sure yet
			}
		};
		this.timer = new Timer(200, taskPerformer);
		this.timer.start();
	}
}
