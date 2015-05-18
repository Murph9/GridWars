package game.logic;

import game.objects.*;

import java.util.ArrayList;
import java.util.Random;


@SuppressWarnings("unused") //this is because spawning objects just requires a call not to use it

/**Handles the spawning of objects (position and time).
 * @author Jake Murphy
 */

//This file has the worst code/time ratio of all. (and then mostly copied from the source code)

public class SpawnHandler {

	private static final int POWER_UP_OFFSET = 2000;
	private static final int PLAYER_SPAWN_RANGE = 4;
	private int powerUpScore;
	
	private Random random;
	private String diff; //maybe: med = normal, easy = 0.8*rate, hard = 0.3+1.1*rate 

	private long totalTime;
	private long frameCount;
	
	
	//is there a better way to chose a spawn (not without overriding another swarm)
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
		this.frameCount = 0; //so they don't spawn striaght away
		this.powerUpScore = 5000; //always 5000 from the start.
		
		if (diff.equals(Engine.MEDIUM_D)) {
			frameCount = 1500;
		}
		
		if (diff.equals(Engine.HARD_D)) { //so it starts with hard things
			frameCount = 7000;
		}
 	}
	
	public void lostLife() {
		powerUpScore = Engine.gameState.getScore()/5000;
		powerUpScore = powerUpScore*5000 + 5000; //returns the next 5000 multiple from the score
	}
	

	public void update(double dt) {
		int score = Engine.gameState.getScore();
		int mult = Engine.gameState.getMultiplier();
		
		if (score >= powerUpScore) { //from the source code method MakePowerUp(tp:Int=-1)
			int type = random.nextInt(2); //0 or 1
			
			if (Engine.rand.nextInt(100) > 80) { //choosing super, bouncy, speed or shield first
				int rand = random.nextInt(4);
				switch(rand) {
				case 0: 
					type = Engine.TEMP_SHIELD; break;
				case 1: 
					type = Engine.SUPER_SHOT; break;
				case 2: 
					type = Engine.BOUNCY_SHOT; break;
				case 3: 
					type = Engine.EXTRA_SPEED;	break;
				default:
					System.err.println("help, powerup code ~line 81");
				}
			}
			
			if (Engine.gameState.getBulletCount() < 4) { //extra bullet
				if (random.nextInt(Engine.gameState.getBulletCount()) == 0) {
					type = Engine.EXTRA_BULLET;
				}
			}
			
			if (type < 2 && random.nextInt(100) > 40) { //extra life or extra bomb 
				if (Engine.gameState.getBulletSpeed() < 2) { //in seriouser trouble
					type = Engine.EXTRA_LIFE;
				} else if (Engine.gameState.getBombCount() < 2) { //in serious trouble
					type = Engine.EXTRA_BOMB;
				}
			}

			powerUpScore += POWER_UP_OFFSET*mult*mult; //so it get slowly further and further away
				//source code says POWERUP*mult*mult but that gets way too big?
//			System.out.println(powerUpScore + " " + mult);
			
			PowerUp up = new PowerUp(type);
			chooseSpawn(up, 0);
			up.dx = random.nextDouble()*2 -1;
			up.dy = random.nextDouble()*2 -1;
		}
		
		////////////////////////////////////////////
		//check to see which/if objects should spawn
		objectSpawn(dt);
		////////////////////////////////////////////
		
		
		//increase game difficulty with each 3600th frame
		if (frameCount % 3600 == 1) {
			double inc = 0.15;
			switch(diff) {
			case Engine.EASY_D:
				inc += 0*0.1; //really?, just a formality at this point
				break;
			case Engine.MEDIUM_D:
				inc += 1*0.1;
				break;
			case Engine.HARD_D:
				inc += 2*0.1;
				break;
			}
			
			//speed scaling of objects done so the game might actually end
			ShySquare.MAX_SPEED += inc; 
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

	/**Completely ignores "dt" and just uses frameCount to check to see if spawning should happen
	 * @param dt
	 */
	private void objectSpawn(double dt) {
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
			int health =  16 + random.nextInt(Math.min((int)(frameCount/2000) + 1, 16));
			
			double rate = 1.5 //base rate
					+ random.nextDouble()//random between 0 and 1   
					- (double)frameCount/10000d; //it gets smaller as the game goes on
			
			if (rate < 1.5) rate = 1.5;

			int type = pickEnemy(frameCount/4) + 1;
			if (type == 5) type = 3 + random.nextInt(3); //3, 4 or 5
			if (type == 8) type = 9;
			
			createSpawner(type, health, rate, random.nextInt(13));
		}
		
		//swarm 1 spawn: 
		if (frameCount % 777 == 0) {
			swarmLocation1 = random.nextInt(13);
			swarmType1 = pickEnemy(frameCount/3);
			swarmCount1 = (15 + random.nextInt(25+((int)(frameCount/750))))*2;
			if (swarmCount1 > 100) swarmCount1 = 100;
		}
		//swarm 1 generate
		if (swarmCount1 > 0) {
			swarmCount1--;
			if (swarmCount1 % 2 == 0) {
				createEnemy(swarmType1, 0.24, swarmLocation1); //0.24 is a nice number
			}
		}
		
		//swarm 2 spawn:
		if (frameCount % 1850 == 0) {
			swarmLocation2 = random.nextInt(13);
			swarmType2 = pickEnemy(frameCount/2);
			swarmCount2 = (15 + random.nextInt(25+((int)(frameCount/750))))*2;
			if (swarmCount2 > 175) swarmCount2 = 175;
		}
		//swarm 2 generate:
		if (swarmCount2 > 0) {
			swarmCount2--;
			if (swarmCount2 % 2 == 0) {
				createEnemy(swarmType2, 0.24, swarmLocation2);
			}
		}
	
		//swarm 3 spawn:
		if (frameCount % 2900 == 0) {
			swarmLocation3 = 5; //all corners
			swarmType3 = pickEnemy(frameCount/2);
			swarmCount3 = (20 + random.nextInt(40+((int)(frameCount/750))))*3;
			if (swarmCount3 > 300) swarmCount3 = 300;
		}
		//swarm 3 generate:
		if (swarmCount3 > 0) {
			swarmCount3--;
			if (swarmCount3 % 3 == 0) {
				createEnemy(swarmType3, 0.2, swarmLocation3); //slightly faster spawn time
			}
		}
		
		
		//swarm 4 spawn: (rare)
		if ((frameCount % 3333 == 0) && (frameCount/4000  % 2 == 1)) {
			swarmLocation4 = random.nextInt(12); //no spawning around player
			swarmType4 = pickEnemy(frameCount/2);
			swarmCount4 = (20 + random.nextInt(40+((int)(frameCount/750))))*3;
			if (swarmCount4 > 300) swarmCount4 = 300;
		}
		//swarm 4 generate:
		if (swarmCount4 > 0) {
			swarmCount4--;
			if (swarmCount4 % 3 == 0) {
				createEnemy(swarmType4, 0.2, swarmLocation4);
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
	
	private void createEnemy(int enemyType, double spawnDelay, int location) {
		GameObject o = null;
		switch (enemyType) {
		case 0: 	o = new SimpleSpinner(spawnDelay); break;
		case 1:		o = new HomingDiamond(spawnDelay); break;
		case 2:		o = new ShySquare(spawnDelay); break;
		case 3:		o = new SplitingSquare(spawnDelay); break;
		case 4:		o = new HomingCircle(spawnDelay); break;
		case 5:		
			if (BlackHole.ALL_THIS.size() < 4) { //can't have too many blackholes
				o = new BlackHole(spawnDelay);
			} else {
				o = new SimpleSpinner(spawnDelay);
			}
			break;
		case 6:		o = new ConnectedTriangle(spawnDelay); break;
		case 7:		o = new SnakeHead(spawnDelay); break;
		case 8:		o = new ShieldedClone(spawnDelay); break;
		case 9:		o = new HomingButterfly(spawnDelay); break;
		}
		chooseSpawn(o, location);
	}
	
	
	private void createSpawner(int spawnType, int health, double rate, int location) {
		GameObject o = new Spawner(0.2, spawnType, health, rate);
		chooseSpawn(o, location);
	}
	
	
	/**Spawns/moves the object depending on the input location (int 0-12) 
	 * @param o
	 */
	private void chooseSpawn(GameObject o, int location) {
		boolean bool = random.nextBoolean();
		//these options are for picking many corners with the same number
			//e.g. 6 will alternate between corners 1 and 2 (good for swarms)
		
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

		double[] playerPos = Engine.player.getPosition(); //need it twice below
		double width = Engine.settings.getBoardWidth();
		double height = Engine.settings.getBoardHeight();
		
		//actually place it in the corner (or random or player)
		switch (location) {
		case 0: //random spawn
			o.x = random.nextDouble()*width*2 - width;
			o.y = random.nextDouble()*height*2 - height;
			break;
		case 1: //corners 1,2,3,4
			o.x = width - o.size - random.nextDouble();
			o.y = height - o.size - random.nextDouble();
			break;
		case 2:
			o.x = -width + o.size + random.nextDouble();
			o.y = -height + o.size + random.nextDouble();
			break;
		case 3:
			o.x = -width + o.size + random.nextDouble();
			o.y = height - o.size - random.nextDouble();
			break;
		case 4:
			o.x = width-o.size - random.nextDouble();
			o.y = -height+o.size + random.nextDouble();
			break;
		case 12:
			while (true) {
				double angle = random.nextDouble()*Math.PI*2;
		    	o.x = playerPos[0]+(Math.cos(angle)*PLAYER_SPAWN_RANGE);
		    	o.y = playerPos[1]+(Math.sin(angle)*PLAYER_SPAWN_RANGE);
		    	if (o.x < width && o.x > -width && 
		    			o.y < height && o.y > -height) {
		    		break;
		    	}
			}
	    	break;
		default:
			try { 
				throw new Exception();
			} catch (Exception e) {
				System.err.println("invalid corner in spawnhandler.java, line ~380");
				e.printStackTrace();
			}
		}

		//trying to stop objects directly spawning on player
		double dist = (playerPos[0]-o.x)*(playerPos[0]-o.x) + (playerPos[1]-o.y)*(playerPos[1]-o.y);
		if (dist < PLAYER_SPAWN_RANGE + 0.1) {
			chooseSpawn(o, 0); //random, this may be slightly recursive...
		} else {
			//we done here, shockwave!:
			Engine.grid.shockwaveGrid(o.x, o.y, 3, 3);
		}
	}

	public int getNextPowerup() {
		return powerUpScore;
	}


	
}


//An idea for different spawning sets, like only clones ..... (just increasing numbers of them) [extended feature]

//http://gamedev.stackexchange.com/questions/69376/how-can-i-scale-the-number-and-challenge-of-enemies-in-an-attack-wave-as-the-gam
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**http://maxgames.googlecode.com/svn/trunk/vectorzone/vectorzone.bmx*/ //the important file itself