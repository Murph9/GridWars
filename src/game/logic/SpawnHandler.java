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
* Blackholes - should only really have a maximum for 4 on the board at once 
* 				(checking is easy, BlackHole.ALL_THIS.size())
* 
* Powerups - need to be used. Rules about them? (they seem to spawn, after a death, at the next 5000 mark)
* 	probably just every 5000 score it seems.
*/


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
	
	private Random random;
	private String diff; //maybe: med = normal, easy = 0.8*rate, hard = 0.3+1.1*rate 

	private long totalTime;
	private long frameCount;
	
	private double temp;
	
	private int swarmCount1;
	private int swarmType1;
	private int swarmLocation1;
	
	public SpawnHandler(String difficulty) {
		this.diff = difficulty;
		this.random = new Random();
	}
	

	public void update(double dt) {
		/* Logic for this method:
		 * Get time.
		 * mod time using % operator by various things (see comments at end of file)
		 * spawn things
		 *
		 */
		totalTime = (long)(Engine.gameState.getTime()*100); //this line retrieves the time and makes it back to miliseconds
		frameCount++;
		
		if ((frameCount/350) % 2 == 0) {
			if (frameCount % 33 == 0) {
				GameObject o = createEnemy(pickEnemy(frameCount), 0.2);
				spawnCorner(o); //choose corner with "random.nextInt(13)"
			}
		}

		if (frameCount % 444 == 0) {
			int count =  16 + random.nextInt(Math.min((int)(frameCount/2000) + 1, 16));
			double rate = 0.8 + random.nextInt(60)/100  - (double)(frameCount)/100000;
			if (rate < 80) 
				rate = 80;

			int type = pickEnemy(frameCount/4) + 1;
			if (type == 5) type = 3+ random.nextInt(3); //3,4,5
			if (type == 8) type = 9;
			
			Spawner s = createSpawner(type, count, rate);
			spawnCorner(s);
		}
		
//		'whole bunch //- create swarm: corner (either one corner, 2 corners or all), kind cnt/3, count min(rand(15, 24+(cnt/750))*2, 100)
//		If cnt Mod 777 = 0
//			sp_c = Rand(0,12)
//			sp_x = EnemyType(cnt/3)
//			sp_t = Rand(15,24+(cnt/750))*2
//			If sp_t > 100 Then sp_t = 100
//		EndIf
		
		if (frameCount % 777 == 0) {
			swarmLocation1 = random.nextInt(13);
			swarmType1 = pickEnemy(frameCount/3);
			swarmCount1 = (15 + random.nextInt(25+((int)(frameCount/750))))*2;
		}
		
		if (swarmCount1 > 0) {
			swarmCount1--;
			if (swarmCount1 % 2 == 0) {
				GameObject o = createEnemy(pickEnemy(swarmType1), 0.24); //nice number
				spawnCorner(o);
			}
		}
	
	}
	
	/**Uses the frameCount value to pick from a list of objects. 
	 * @return Number from 0 to 8 (representing the objects)
	 */
	private int pickEnemy(long frameCount) {
		int s = 0;
		int level = (int) (frameCount/1100);
		if (level > 8) level = 8;
		
		switch(level) {
		case 0:		s = 0;   break;
		case 1:		s = random.nextInt(2);   break;
		case 2:		s = random.nextInt(3);   break;
		case 3:		s = random.nextInt(4);   break;
		case 4:		s = random.nextInt(5);   break;
		case 5:		s = random.nextInt(6);   break;
		case 6:		s = random.nextInt(7);   break;
		case 7:		s = random.nextInt(8);   break;
		case 8:		s = random.nextInt(10);  break; //the orginal code skips to here as well...
		}
		return s;
	}
	
	private GameObject createEnemy(int enemyType, double spawnDelay) {
		GameObject o = null;
		switch (enemyType) {
		case 0: 	o = new SimpleSpinner(spawnDelay); break;
		case 1:		o = new HomingDiamond(spawnDelay); break;
		case 2:		o = new ShySquare(spawnDelay); break;
		case 3:		o = new SplitingSquare(spawnDelay); break;
		case 4:		o = new HomingCircle(spawnDelay); break;
		case 5:		o = new BlackHole(spawnDelay); break;
		case 6:		o = new ConnectedTriangle(spawnDelay); break;
		case 7:		o = new SnakeHead(spawnDelay); break;
		case 8:		o = new ShieldedClone(spawnDelay); break;
		case 9:		o = new HomingButterfly(spawnDelay); break;
		}
		return o;
	}
	
	
	private Spawner createSpawner(int spawnType, int count, double rate) {
		return new Spawner(0.2, spawnType, count, rate);
	}
	
	
    private void spawnCorner(GameObject obj) {
    	//spawn in a range near the side
    	obj.x = (random.nextInt(2)*2-1)*(Engine.settings.getBoardWidth()-obj.size);
    	obj.y = (random.nextInt(2)*2-1)*(Engine.settings.getBoardHeight()-obj.size);
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


//An idea for different spawning sets, like only clones ..... (just increasing numbers of them) [extended feature]

//http://gamedev.stackexchange.com/questions/69376/how-can-i-scale-the-number-and-challenge-of-enemies-in-an-attack-wave-as-the-gam
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * Note below is from:
 * http://maxgames.googlecode.com/svn/trunk/vectorzone/vectorzone.bmx
 */

/*
Function Spawn(cnt:Int) //cnt = total frames of game, note they used 50 fps not 60...

Rem 					//Rem at this point may mean comment
	If cnt Mod 40 = 0 
		CreateEnemy(Rand(1,2),20, Rand(0,12))
	EndIf
	If cnt Mod 40 = 0 
		CreateEnemy(5,20, Rand(0,12))
	EndIf
	Return
End Rem					//so guess ignore this section?, looks important though


	Local gk:Int,sz:Int,rate:Int //these are inits to varibles gk:Int == int gk;
	
	'single enemy //- create singluar enemy from rand(0,x) from x = cnt/1100
	If (cnt/350) Mod 2 = 0
		If cnt Mod 33 = 0 
			CreateEnemy(EnemyType(cnt),20, Rand(0,12))
		EndIf
	EndIf
	
	'single generator //- create generator from rand(0,x) from 4*x = cnt/1100, size 16+cnt/2000, rate 80+Rand(60) -cnt/1000
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
	
	'whole bunch //- create swarm: corner (either one corner, 2 corners or all), kind cnt/3, count min(rand(15, 24+(cnt/750))*2, 100)
	If cnt Mod 777 = 0
		sp_c = Rand(0,12)
		sp_x = EnemyType(cnt/3)
		sp_t = Rand(15,24+(cnt/750))*2
		If sp_t > 100 Then sp_t = 100
	EndIf
	'whole bunch2 //- create swarm: corner (either one corner, 2 corners or all), kind cnt/2, count min(rand(15, 24+(cnt/750))*2, 175)
	If cnt Mod 1850 = 0
		sp_c2 = Rand(0,12)
		sp_x2 = EnemyType(cnt/2)
		sp_t2 = Rand(15,24+(cnt/750))*2
		If sp_t2 > 175 Then sp_t2 = 175
	EndIf
	
	'whole bunch3 //- create swarm: corner all, kind cnt/2, count min(rand(20, 40+(cnt/750))*2, 300)
	If cnt Mod 2900  = 0
		sp_c3 = 5 'all 4 corners
		sp_x3 = EnemyType(cnt/2)
		sp_t3 = Rand(20,40+(cnt/750))*3
		If sp_t3 > 100*3 Then sp_t3 = 100*3
	EndIf

	'whole bunch4 //- create swarm rare: corner (any options above), kind cnt/2, count min(rand(20, 40+(cnt/750))*3, 300)
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
	
	If cnt Mod 50*60 = 1  'every minute //- increase the speed of all the objects (scales with difficulty)
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