package game.logic;

import game.objects.*;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("unused")

/**Handles the spawning of objects (position and time).
 * @author Jake Murphy
 */

//This file has the worst code/time ratio of all. (and then mostly copied from the source code)

////////////////////////////
/* Rules: (that need to be met)
* Blackholes - should only really have a maximum for 4 on the board at once 
* 				(checking is easy, BlackHole.ALL_THIS.size())
* 
* Powerups - need to be used. Rules about them? (they seem to spawn, after a death, at the next 5000 mark)
* 	probably just every 5000 score it seems. NO!! TODO
*/


//TODO blackhole swarms suck balls

public class SpawnHandler {

	private int score;
	
	private Random random;
	private String diff; //maybe: med = normal, easy = 0.8*rate, hard = 0.3+1.1*rate 

	private long totalTime;
	private long frameCount;
	
	private double temp;
	
	
	//is there a better way?
	private int swarmCount1;
	private int swarmType1;
	private int swarmLocation1;
	
	private int swarmCount2;
	private int swarmType2;
	private int swarmLocation2;
	
	private int swarmCount3;
	private int swarmType3;
	private int swarmLocation3;
	
	private int swarmCount4;
	private int swarmType4;
	private int swarmLocation4;
	
	public SpawnHandler(String difficulty) {
		this.diff = difficulty;
		this.random = new Random();
		
//		frameCount = 7000;
 	}
	

	public void update(double dt) {
		int curScore = Engine.gameState.getScore()/5000; //power every 5000 
		
		if (score != curScore) {
			int rand = random.nextInt(9);
			int type = -1; //so it breaks if not changed
			
			switch (rand) {
			case 0:
				type = Engine.EXTRA_SPEED; break;
			case 1:
				type = Engine.EXTRA_BULLET; break;
			case 2:
				type = Engine.EXTRA_BOMB; break;
			case 3:
				type = Engine.EXTRA_LIFE; break;
			case 4:
				type = Engine.SIDE_SHOT; break;
			case 5:
				type = Engine.REAR_SHOT; break;
			case 6:
				type = Engine.TEMP_SHIELD; break;
			case 7:
				type = Engine.SUPER_SHOT; break;
			case 8:
				type = Engine.BOUNCY_SHOT; break;
			}
			
			PowerUp up = new PowerUp(type);
			chooseSpawn(up, 0);
			up.dx = random.nextDouble()*2 -1;
			up.dy = random.nextDouble()*2 -1;
		}
		score = curScore;
		
		/* Logic for this method:
		 * Get frameCount
		 * mod count using % operator by various things (see end of file)
		 * spawn!
		 */
		totalTime = (long)(Engine.gameState.getTime()*100); //this line retrieves the time and makes it back to miliseconds
		frameCount++;
		
		if ((frameCount/350) % 2 == 0) {
			if (frameCount % 33 == 0) {
				createEnemy(pickEnemy(frameCount), 0.2, random.nextInt(13));
				//choose corner with "random.nextInt(13)"
			}
		}

		if (frameCount % 444 == 0) {
			int count =  16 + random.nextInt(Math.min((int)(frameCount/2000) + 1, 16));
			double rate = 0.8 + random.nextInt(60)/100  - (double)(frameCount)/100000; //TODO fix timing of spawners
			if (rate > 0.8) 
				rate = 0.8;

			int type = pickEnemy(frameCount/4) + 1;
			if (type == 5) type = 3+ random.nextInt(3); //3,4,5
			if (type == 8) type = 9;
			
			createSpawner(type, count, rate, random.nextInt(13));
		}
		
		////swarm 1 spawn:
		if (frameCount % 777 == 0) {
			swarmLocation1 = random.nextInt(13);
			swarmType1 = pickEnemy(frameCount/3);
			swarmCount1 = (15 + random.nextInt(25+((int)(frameCount/750))))*2;
			if (swarmCount1 > 100) swarmCount1 = 100;
		}
		////swarm 1 generate
		if (swarmCount1 > 0) {
			swarmCount1--;
			if (swarmCount1 % 2 == 0) {
				createEnemy(swarmType1, 0.24, swarmLocation1); //0.24 is a nice number
			}
		}
		
		////swarm 2 spawn:
		if (frameCount % 1850 == 0) {
			swarmLocation2 = random.nextInt(13);
			swarmType2 = pickEnemy(frameCount/2);
			swarmCount2 = (15 + random.nextInt(25+((int)(frameCount/750))))*2;
			if (swarmCount2 > 175) swarmCount2 = 175;
		}
		////swarm 2 generate:
		if (swarmCount2 > 0) {
			swarmCount2--;
			if (swarmCount2 % 2 == 0) {
				createEnemy(swarmType2, 0.24, swarmLocation2);
			}
		}
	
		////swarm 3 spawn:
		if (frameCount % 2900 == 0) {
			swarmLocation3 = 5; //all corners
			swarmType3 = pickEnemy(frameCount/2);
			swarmCount3 = (20 + random.nextInt(40+((int)(frameCount/750))))*3;
			if (swarmCount3 > 300) swarmCount3 = 300;
		}
		////swarm 3 generate:
		if (swarmCount3 > 0) {
			swarmCount3--;
			if (swarmCount3 % 3 == 0) {
				createEnemy(swarmType3, 0.2, swarmLocation3); //slightly faster spawn time
			}
		}
		
		
		////swarm 4 spawn: (rare)
		if ((frameCount % 3333 == 0) && (frameCount/4000  % 2 == 1)) {
			swarmLocation4 = random.nextInt(12); //no spawning around player
			swarmType4 = pickEnemy(frameCount/2);
			swarmCount4 = (20 + random.nextInt(40+((int)(frameCount/750))))*3;
			if (swarmCount4 > 300) swarmCount4 = 300;
		}
		////swarm 4 generate:
		if (swarmCount4 > 0) {
			swarmCount4--;
			if (swarmCount4 % 3 == 0) {
				createEnemy(swarmType4, 0.2, swarmLocation4);
			}
		}
		
		
		if (frameCount % 3600 == 1) {
			double inc = 0.15;
			switch(diff) {
			case Engine.EASY_D:
				inc += 0*0.1;
				break;
			case Engine.MEDIUM_D:
				inc += 1*0.1;
				break;
			case Engine.HARD_D:
				inc += 2*0.1;
				break;
			}
			
			ShySquare.MAX_SPEED += inc; //speed scaling
			SimpleSpinner.MAX_SPEED += inc;
			HomingDiamond.MAX_SPEED += inc;
			SplitingSquare.MAX_SPEED += inc;
			HomingCircle.MAX_SPEED += inc;
			BlackHole.MAX_SPEED += inc;
			SnakeHead.MAX_SPEED += inc;
			ShieldedClone.MAX_SPEED += inc;
			HomingButterfly.MAX_SPEED += inc;
			ConnectedTriangle.MAX_SPEED += inc;
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
	
	private void createEnemy(int enemyType, double spawnDelay, int location) {
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
		chooseSpawn(o, location);
	}
	
	
	private void createSpawner(int spawnType, int count, double rate, int location) {
		GameObject o = new Spawner(0.2, spawnType, count, rate);
		chooseSpawn(o, location);
	}
	
	
	/**Spawns/moves the object depending on the input location (int 0-12) 
	 * @param o
	 */
	private void chooseSpawn(GameObject o, int location) {
		boolean bool = random.nextBoolean();
		//these options are for picking many corners with the same number
			//e.g. 6 will alternate between corners 1 and 2 (good for swarms)
		
		//TODO handle spawning directly on player (as in try again)
		switch (location) {
		case 5: //random corner
			location = random.nextInt(4) + 1; //1,2,3,4
			break;
		case 6: //corners 1,2
			if (bool) location = 1;
			else location = 2;
			break;
		case 7: //c 2,3
			if (bool) location = 2;
			else location = 3;
			break;
		case 8: //c 3,4
			if (bool) location = 3;
			else location = 4;
			break;
		case 9: //c 1,3
			if (bool) location = 1;
			else location = 3;
			break;
		case 10://c 1,4
			if (bool) location = 1;
			else location = 4;
			break;
		case 11://c 2,4
			if (bool) location = 2;
			else location = 4;
			break;
		}

		double[] playerPos = Engine.player.getPosition();
		
		//actually place it in the corner (or random or player)
		switch (location) {
		case 0: //random spawn
			o.x = 2*(random.nextInt(Engine.settings.getBoardWidth())-Engine.settings.getBoardWidth()/2);
			o.y = 2*(random.nextInt(Engine.settings.getBoardHeight())-Engine.settings.getBoardHeight()/2);
			break;
		case 1: //corners 1,2,3,4
			o.x = Engine.settings.getBoardWidth()-o.size;
			o.y = Engine.settings.getBoardHeight()-o.size;
			break;
		case 2:
			o.x = -Engine.settings.getBoardWidth()+o.size;
			o.y = -Engine.settings.getBoardHeight()+o.size;
			break;
		case 3:
			o.x = -Engine.settings.getBoardWidth()+o.size;
			o.y = Engine.settings.getBoardHeight()-o.size;
			break;
		case 4:
			o.x = Engine.settings.getBoardWidth()-o.size;
			o.y = -Engine.settings.getBoardHeight()+o.size;
			break;
		case 12:
			while (true) {
				double angle = random.nextDouble()*Math.PI*2;
		    	o.x = playerPos[0]+(Math.cos(angle)*5);
		    	o.y = playerPos[1]+(Math.sin(angle)*5);
		    	if (o.x < Engine.settings.getBoardWidth() && o.x > -Engine.settings.getBoardWidth() && 
		    			o.y < Engine.settings.getBoardHeight() && o.y > -Engine.settings.getBoardHeight()) {
		    		break;
		    	}
			}
	    	break;
		default:
			try { 
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//TODO fix the stack overflow errors here
//		if ((o.x - playerPos[0] < 3) && (o.y - playerPos[1] < 3)) {
//			chooseSpawn(o, 5); //random corner
//		}
	}
}


//An idea for different spawning sets, like only clones ..... (just increasing numbers of them) [extended feature]

//http://gamedev.stackexchange.com/questions/69376/how-can-i-scale-the-number-and-challenge-of-enemies-in-an-attack-wave-as-the-gam
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**http://maxgames.googlecode.com/svn/trunk/vectorzone/vectorzone.bmx*/

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