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
	
	private static final double OLD_SPAWN_TIME = 1;
	

	private Random random;
	private String diff; //maybe: med = normal, easy = 0.8*rate, hard = 0.3+1.1*rate 


	private double temp;
	
	public SpawnHandler(String difficulty) {
		this.diff = difficulty;
		this.random = new Random();
		
	}
	

	public void update(double dt) {
		/* Logic:
		 * Get time.
		 * mod time using % operator by various things (see comments at end of file)
		 * spawn thing
		 *
		 */
		
		if (temp < 0) {
			newEnemy();
			temp = OLD_SPAWN_TIME;
		}
	}
	
	private int pickEnemy() {
		return 0;
	}
	
	
	//spawning done simple
    private void newEnemy() {
    	if (!Engine.canSpawn()) {
    		return;
    	}
    	int a = random.nextInt(13);
    	GameObject s = null;
    	a = 5;
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
    		if (BlackHole.ALL_THIS.size() > 3) {
    			s = new SimpleSpinner();
    		} else {
    			s = new BlackHole();
    		}
    		break;
    	case 12:
    		s = new ConnectedTriangle();	break;
    	}
    	spawnCorner(s);
    }


    private void spawnCorner(GameObject obj) {
    	//spawn in a range near the side
    	obj.x = (random.nextInt(2)*2-1)*(Engine.settings.getBoardWidth()-0.5);
    	obj.y = (random.nextInt(2)*2-1)*(Engine.settings.getBoardHeight()-0.5);
    }
    
    private void spawnRandom(GameObject obj) {
    	obj.x = random.nextInt(Engine.settings.getBoardWidth())*2 -Engine.settings.getBoardWidth();
    	obj.y = random.nextInt(Engine.settings.getBoardHeight())*2 -Engine.settings.getBoardHeight();
    }
    
    private void spawnPlayer(GameObject obj) {
    	double[] playerPos = Engine.player.getPosition();
    	double angle = random.nextDouble()*Math.PI*2;
    	obj.x = playerPos[0]+(Math.cos(angle)*5);
    	obj.y = playerPos[1]+(Math.sin(angle)*5);
    	
    	//TODO check for spawning outside play field in here
    }
    
}


//class Spawn {
//	
//	String typeName;
//	boolean isSwarm;
//	
//	double chance; //out of 100% of the current items in spawn
//	double diff; //from 0 - 4
//	
//	
//	
//}


//An idea for different spawning sets, like only clones ..... (just increasing numbers of them) [extended feature]

//http://gamedev.stackexchange.com/questions/69376/how-can-i-scale-the-number-and-challenge-of-enemies-in-an-attack-wave-as-the-gam

/**
 * Note below is from:
 * http://maxgames.googlecode.com/svn/trunk/vectorzone/vectorzone.bmx
 */

/*
Function Spawn(cnt:Int) //frames of game past, note they used 50 fps not 60...

Rem 									//Rem at this point may mean comment
	If cnt Mod 40 = 0 
		CreateEnemy(Rand(1,2),20, Rand(0,12))
	EndIf
	If cnt Mod 40 = 0 
		CreateEnemy(5,20, Rand(0,12))
	EndIf
	Return
End Rem									//so guess ignore this section?, looks important though


	Local gk:Int,sz:Int,rate:Int //these are inits to varibles gk:Int == int gk;
	
	'single enemy 
	If (cnt/350) Mod 2 = 0
		If cnt Mod 33 = 0 
			CreateEnemy(EnemyType(cnt),20, Rand(0,12))
		EndIf
	EndIf
	
	'single generator
	If cnt Mod 444 = 0 
		gk = EnemyType(cnt/4)+1
		sz = Rand(16+cnt/2000,32)
		rate = 80+Rand(60)-cnt/1000
		If rate < 80 Then rate = 80	
		If gk = 9 'no clone generator
			gk = 10 'butterfly geerator
		EndIf
		If gk = 6 'no sun generator
			gk = Rand(3,5) 'green, purp, or blue
		EndIf
		CreateEnemy(10,20,Rand(0,12),rate,gk,sz)
	EndIf
	
	'whole bunch
	If cnt Mod 777 = 0
		sp_c = Rand(0,12)
		sp_x = EnemyType(cnt/3)
		sp_t = Rand(15,24+(cnt/750))*2
		If sp_t > 100 Then sp_t = 100
	EndIf
	'whole bunch2
	If cnt Mod 1850 = 0
		sp_c2 = Rand(0,12)
		sp_x2 = EnemyType(cnt/2)
		sp_t2 = Rand(15,24+(cnt/750))*2
		If sp_t2 > 175 Then sp_t2 = 175
	EndIf
	
	'whole bunch3
	If cnt Mod 2900  = 0
		sp_c3 = 5 'all 4 corners
		sp_x3 = EnemyType(cnt/2)
		sp_t3 = Rand(20,40+(cnt/750))*3
		If sp_t3 > 100*3 Then sp_t3 = 100*3
	EndIf

	'whole bunch4
	If ((cnt/4000) Mod 2 = 1) And (cnt Mod 3333 = 0)
		sp_c4 = Rand(0,11) 'any corner/s
		sp_x4 = EnemyType(cnt/2)
		sp_t4 = Rand(20,40+(cnt/750))*3
		If sp_t4 > 100*3 Then sp_t4 = 100*3
	EndIf
	
	'keep placing the whole bunch
	If sp_t > 0
		sp_t:-1
		If sp_t Mod 2 = 0
			CreateEnemy(sp_x,24,sp_c)
		EndIf
	EndIf
	
	'keep placing the whole bunch2
	If sp_t2 > 0
		sp_t2:-1
		If sp_t2 Mod 2 = 0
			CreateEnemy(sp_x2,24,sp_c2)
		EndIf
	EndIf
	
	'keep placing the whole bunch3
	If sp_t3 > 0
		sp_t3:-1
		If sp_t3 Mod 3 = 0
			If sp_x3 = 9 Then CreateEnemy(sp_x3,20,sp_c3) ' 2X more indigo triangles
			CreateEnemy(sp_x3,20,sp_c3)
		EndIf
	EndIf
	
	'keep placing the whole bunch4
	If sp_t4 > 0
		sp_t4:-1
		If sp_t4 Mod 3 = 0
			If sp_x4 = 9 Then CreateEnemy(sp_x4,20,sp_c4) ' 2X more indigo triangles
			CreateEnemy(sp_x4,20,sp_c4)
		EndIf
	EndIf
	
	If cnt Mod 50*60 = 1  'every minute, increase the speed of all the objects (scales with difficulty)
		Local inc# = 0.15+startingdifficulty*0.1
		speed_nme#:+ inc
		speed_nme1#:+ inc
		speed_nme2#:+ inc
		speed_nme3#:+ inc
		speed_nme4#:+ inc
		speed_nme5#:+ inc
		speed_nme6#:+ inc
		speed_nme7#:+ inc
		speed_nme8#:+ inc
		speed_le#:+ inc
	EndIf
	
End Function*/