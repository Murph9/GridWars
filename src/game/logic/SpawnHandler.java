package game.logic;
import game.objects.BlackHole;
import game.objects.ConnectedTriangle;
import game.objects.GameObject;
import game.objects.HomingDiamond;
import game.objects.ShieldedClone;
import game.objects.ShySquare;
import game.objects.SimpleSpinner;
import game.objects.SnakeHead;
import game.objects.SplitingSquare;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

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
	
	private static int TIME_INTERVAL = 250; //500 seems the best so far, 250 is just hard
	
	private String type;
	private Timer timer;
	private Random random;

	public SpawnHandler(String type) {
		this.type = type;

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newEnemy();
			}
		};
		this.timer = new Timer(TIME_INTERVAL, taskPerformer);
			//might have to be an object thats updated
		this.timer.start();
		
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
    	
    	s.setPosition(new double[]{(random.nextInt(2)*2-1)*(boardWidth-0.5), (random.nextInt(2)*2-1)*(boardHeight-0.5)});
    }
}
