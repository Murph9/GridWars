package game.logic;

import game.objects.*;

import java.util.Random;


/**Handles the spawning of objects, initalsed with the difficulty of the game.
 * @author Jake Murphy
 */

////////////////////////////
/* Rules: (that need to be met)
* ConnectedTriangle - should probably spawn with a friend (or more)
* Blackholes - should only really have a maximum for 4 on the board at once 
* 				(checking is easy, BlackHole.ALL_THIS.size())
* 
* Spawners - maybe like 16th the chance of spawning something
* 
* Powerups - need to be used
*/


/*Objects have a "difficulty range" they spawn in (this varies with difficulty):
 * Difficulty table key:
 * {swarm difficulty | spawn corner| spawn around player| spawn random - name}
 * 
 * 1.0 | 1.0 | 1.2 | 1.1 - SimpleSpinner
 * 1.6 | 1.2 | 1.3 | - HomingDiamond
 * 2.4 | 1.3 | 1.3 | - SnakeHead
 * 2.1 | 1.8 | 1.8 | - ShySquare
 * 2.4 | 2.0 | 2.1 |- SplitingSquare
 * 
 * 2.5 | 1.1 | 1.2 | - ConnectedTriangle
 * 
 * 2.8 | 2.3 | 3.0 | - HomingButterfly
 * 2.9 | 2.4 | 3.2 | - HomingSeeker
 * 
 * 3.0 | 1.6 | 3.0 |  - BlackHole (note swarm difficulty is for other swarms here)
 * 3.5 | 2.6 | 4.0 | - ShieldedClone
 */


////////////////////////////
//THOUGHTS:

//thinking about having "waves" of things spawn: (in order) (with swarms in the transition, either around the player or on the edge)	
	//maybe a "swarm countdown" meaning it will eventually happen
	
	//correct gameplay should be (on as many levels as possible) easy/hard/easy/hard/...
		//this is a known good gameplay mechanic (also works for movies)

//what happens when the player dies, what kind of objects spawn then?
	//was thinking like slowly (for like 5 seconds) moves back to where it was? 
	//or reduces the "momentary difficulty" by a fixed/proportional amount

public class SpawnHandler {
	
	private static final double OLD_SPAWN_TIME = 0.25;

	private Random random;
	private String diff;

	private double totalTime;
	private double countDown;

	
	//inital rate = 2 a second (where rate = current difficulty)
	//dRate = 0.2 a kill (maybe less?)
	
	
	public SpawnHandler(String difficulty) {
		this.diff = difficulty;
		this.random = new Random();
	}
	
	
	//spawning done simple
    private void newEnemy() {
    	if (!GameEngine.canSpawn()) {
    		return;
    	}
    	int a = random.nextInt(13);
    	GameObject s = null;
    	switch (a) {
    	case 0: case 1: case 2:
    		s = new SimpleSpinner();    	break;
    	case 3: case 4:
    		s = new HomingDiamond();   		break;
    	case 5: case 6:
    		s = new SplitingSquare();		break;
    	case 7:
    		s = new ShieldedClone();    	break;
    	case 8:
    		s = new SnakeHead();    		break;
    	case 9: case 10:
    		s = new ShySquare();			break;
    	case 11:
    		s = new BlackHole();   			break;
    	case 12:
    		s = new ConnectedTriangle();	break;
    	}
    	pos2Corner(s);
    }


    private void pos2Corner(GameObject obj) {
    	obj.x = (random.nextInt(2)*2-1)*(GameEngine.settings.getBoardWidth()-0.5);
    	obj.y = (random.nextInt(2)*2-1)*(GameEngine.settings.getBoardHeight()-0.5);
    }
    
    
    
	public void update(double dt) {
		totalTime += dt;
		
		countDown -= dt;
		
		if (countDown < 0) {
			newEnemy();
			countDown = OLD_SPAWN_TIME;
		}
	}
}




//An idea for different spawning sets, like only clones ..... (just increasing numbers of them) [extended feature]

//http://gamedev.stackexchange.com/questions/69376/how-can-i-scale-the-number-and-challenge-of-enemies-in-an-attack-wave-as-the-gam
