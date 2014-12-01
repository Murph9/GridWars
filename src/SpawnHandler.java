import java.util.HashMap;
import java.util.Timer;


//handles the spawning of objects, initalsed with the difficulty.
//////////////////////////
/*objects that can be spawned:
 * SimpleSpinner
 * HomingDiamond
 * SplitingSquare
 * ShySquare
 * SnakeHead
 * HomingButterfly
 * HominghSeeker
 * BlackHole
 * ShieldedClone
 * ConnectedTriangle
 * 
 * And their respective spawners (excluding blackholes)
 */


////////////////////////////
/* Rules:
 * ConnectedTriangle - should probably spawn with a friend
 * Blackholes - should only really have a maximum for 4 on the board at once
 * 
 * Spawners - maybe like 16th the chance of spawning something
 * 
 * Powerups - need to be used
 */

public class SpawnHandler {
	
	private int diff;
	private Timer timer;
	
	private HashMap<String, Float> chance; //difficulty of the object
	
	public SpawnHandler(int diff) {
		this.diff = diff;
		chance = new HashMap<String, Float>();
		chance.put(SimpleSpinner.class.toString(), 1f);
		chance.put(HomingDiamond.class.toString(), 1f);
		chance.put(SplitingSquare.class.toString(), 1f);
		chance.put(ShySquare.class.toString(), 1f);
		chance.put(SnakeHead.class.toString(), 1f);
		
		chance.put(HomingButterfly.class.toString(), 1f);
		chance.put(HomingSeeker.class.toString(), 1f);
		
		chance.put(BlackHole.class.toString(), 1f);
		chance.put(ShieldedClone.class.toString(), 1f);
		chance.put(ConnectedTriangle.class.toString(), 1f);
	}
}
