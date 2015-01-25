package game.logic;

import game.objects.*;

import java.util.Random;


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
		//this is a known good gameplay mechanic (also works for movies)


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
	
	private static final double SPAWN_TIME = 0.25;
	
	private String diff;
	private double timer;
	private Random random;

	public SpawnHandler(String difficulty) {
		this.diff = difficulty;

		this.random = new Random();
	}
	
	
	//spawning done simple. Look at SpawnHandler for better spawning logic
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
    	int boardWidth = GameEngine.curSettings.getBoardWidth();
    	int boardHeight = GameEngine.curSettings.getBoardHeight();
    	
    	s.x = (random.nextInt(2)*2-1)*(boardWidth-0.5);
    	s.y = (random.nextInt(2)*2-1)*(boardHeight-0.5);
    }


	public void update(double dt) {
		timer -= dt;
		
		if (timer < 0) {
			newEnemy();
			timer = SPAWN_TIME;
		}
	}
}
