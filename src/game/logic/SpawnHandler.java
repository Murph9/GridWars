package game.logic;

import game.objects.*;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("unused")

/**Handles the spawning of objects (position and time).
 * @author Jake Murphy
 */

//This file has the worst code/time ratio of all.

////////////////////////////
/* Rules: (that need to be met)
* ConnectedTriangle - should probably spawn with a friend (or more)
* Blackholes - should only really have a maximum for 4 on the board at once 
* 				(checking is easy, BlackHole.ALL_THIS.size())
* 
* Spawners - maybe like 16th the chance of spawning something
* 
* Powerups - need to be used. Rules about them? (they seem to spawn, after a death, at the next 5000 mark)
*/


/*Objects have a "difficulty range" they spawn in (this varies with difficulty):
 * - see excel document
 */

//The above table is for at what point in the large gameplay progression they can spawn.
//What about how they spawn?


////////////////////////////
//THOUGHTS:

//thinking about having "waves" of things spawn: (in order) (with swarms in the transition, either around the player or on the edge)	
	//maybe a "swarm countdown" meaning it will eventually happen
	
	//correct gameplay should be easy/hard/easy/hard/...
		//this is a known good gameplay mechanic (known for movies before games)
		//could be completed using swarms of enemies (even if there is a constant background spawn rate)

//what happens when the player dies, what kind of objects spawn then?
	//was thinking like slowly (for like 5 seconds) moves back to where it was? 
	//or reduces the "momentary difficulty" by a fixed/proportional amount

public class SpawnHandler {
	
	private static final double OLD_SPAWN_TIME = 0.25;

	private Random random;
	private String diff;

	private double totalKills; //total time to calculate the current difficulty (number starting at 1.0)
	
	private double singleCountDown; //count down for single object spawn
	
	private double swarmCountDown; //swarm count down

	
	//inital rate = 2 a second (where rate = current difficulty)
	//dRate = 0.2 a kill (maybe less?)
	
	
	public SpawnHandler(String difficulty) {
		this.diff = difficulty;
		this.random = new Random();
		
	}
	

	public void update(double dt) {
		totalKills += GameEngine.gameState.getTotalKills();
		
		singleCountDown -= dt;
		
		if (singleCountDown < 0) {
			newEnemy();
			singleCountDown = OLD_SPAWN_TIME;
		}
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
    	spawnCorner(s);
    }


    private void spawnCorner(GameObject obj) {
    	obj.x = (random.nextInt(2)*2-1)*(GameEngine.settings.getBoardWidth()-0.5);
    	obj.y = (random.nextInt(2)*2-1)*(GameEngine.settings.getBoardHeight()-0.5);
    }
    
    private void spawnRandom(GameObject obj) {
    	obj.x = random.nextInt(GameEngine.settings.getBoardWidth())*2 -GameEngine.settings.getBoardWidth();
    	obj.y = random.nextInt(GameEngine.settings.getBoardHeight())*2 -GameEngine.settings.getBoardHeight();
    }
    
    private void spawnPlayer(GameObject obj) {
    	double[] playerPos = GameEngine.player.getPosition();
    	double angle = random.nextDouble()*Math.PI*2;
    	obj.x = playerPos[0]+(Math.cos(angle)*5);
    	obj.y = playerPos[1]+(Math.sin(angle)*5);
    	
    	//TODO check for spawning outside play field in here
    }
    
}





//An idea for different spawning sets, like only clones ..... (just increasing numbers of them) [extended feature]

//http://gamedev.stackexchange.com/questions/69376/how-can-i-scale-the-number-and-challenge-of-enemies-in-an-attack-wave-as-the-gam
