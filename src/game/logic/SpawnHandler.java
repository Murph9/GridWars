package game.logic;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;


//handles the spawning of objects, initalsed with the difficulty of the object
//not sure whether thisfile/GameEngine/newfile should handle the actual position of the spawning objects 
	//idea for different spawning sets, like only clones ..... (just increasing numbers of them)


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
		//this is a known good gameplay mechanic
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
	
	private int type;
	private Timer timer;

	public SpawnHandler(int type) {
		this.type = type;
		if (this.type == 0) //TODO, just removing the warning for 'type' now
			return;

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
//				not really sure yet
			}
		};
		this.timer = new Timer(200, taskPerformer);
			//might have to be an object thats updated
		this.timer.start();
	}
}
