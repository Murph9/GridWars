import java.util.HashMap;
import java.util.Timer;


//handles the spawning of objects, initalsed with the difficulty of the object
//////////////////////////
/*objects that can be spawned:
 * 1 - SimpleSpinner
 * 1 - HomingDiamond
 * 1 - SplitingSquare
 * 1 - ShySquare
 * 1 - SnakeHead
 * 
 * 1 - HomingButterfly
 * 1 - HominghSeeker
 * 
 * 1 - BlackHole
 * 1 - ShieldedClone
 * 1 - ConnectedTriangle
 * 
 * And their respective spawners (excluding blackholes, i think?)
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
	
	private HashMap<String, Integer> chance; //difficulty of the object
		//maybe cumulative total so it inside 
	
	public SpawnHandler(int diff) {
		this.diff = diff;
		chance = new HashMap<String, Integer>();
		
	}
}
