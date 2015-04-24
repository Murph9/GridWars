package game.logic;
import game.objects.*;

@SuppressWarnings("unused") //alot of 's' that i just don't need to see here 
				//(im using the eclipse editor if you hadn't figured already)

/**Handles the current game state, including all information on scoring and powerups.
 * 
 * Used by the Engine
 * Has a lot of read only variables and should only contain info for this game
 * @author Jake Murphy
 */
public class GameState {
	
	private final double SPEED_INC = 0.15;
	
	//TODO these change value depending on the level
	private static final double POWERUP_LENGTH_LONG = 15; //15 seconds is the standard (according to the game)
	private static final double POWERUP_LENGTH_SHORT = 15; //10 is for super shots
	
	private final int[] killSteps = new int[]{25,50,100,200,400,800,1600,3200,6400,12800}; //multipler steps
	
	//player states
	private double 
			hasShield, hasSideBullets, hasRearBullets, hasBouncyShot, hasSuperShot;
	
	//Current game values
	private String difficulty;
	
	private int lastRecord, score, lives, multiplier, kills;
	
	private int bombCount;
	private int bulletCount;
	private double bulletSpeed;
	private double time;
	
	//for the long term stats file: (more needed i think)
	private int deaths, totalKills, powerupCount, heighestMulti;
	
	/**
	 * @param boardWidth - boardwidth
	 * @param boardHeight - boardheight
	 * @param diff - game difficulty
	 * @param scale - zoom level (usually 10)
	 */
	GameState(String difficulty) {
			//note how anything not here is initalised to 0.
		this.lastRecord = FileHelper.getBestScore(difficulty);
		this.difficulty = difficulty;
		
		lives = 2;
		multiplier = 1;
		
		bombCount = 3;
		bulletCount = 2;
		bulletSpeed = 1.6;
		
//		hasShield =1000;
		
		resetSpeeds(difficulty);
	}
	
	//because as the game goes these speed get faster
	private void resetSpeeds(String diff) {
		Player.MAX_SPEED = 6.5;
		
		ShySquare.MAX_SPEED = 3.5;
		SimpleSpinner.MAX_SPEED = 2.25;
		HomingDiamond.MAX_SPEED = 3.25;
		SplitingSquare.MAX_SPEED = 3.0;
		HomingCircle.MAX_SPEED = 4.5;
		BlackHole.MAX_SPEED = 2.5;
		SnakeHead.MAX_SPEED = 3.5;
		ShieldedClone.MAX_SPEED = 4.25;
		HomingButterfly.MAX_SPEED = 5.0;
		ConnectedTriangle.MAX_SPEED = 2.0;
		
		switch(diff) {
		case Engine.EASY_D:
			ShySquare.MAX_SPEED *= 0.65;
			SimpleSpinner.MAX_SPEED *= 0.75;
			HomingDiamond.MAX_SPEED *= 0.75;
			SplitingSquare.MAX_SPEED *= 0.7;
			HomingCircle.MAX_SPEED *= 0.75;
			BlackHole.MAX_SPEED *= 0.7;
			SnakeHead.MAX_SPEED *= 0.7;
			ShieldedClone.MAX_SPEED *= 0.7;
			HomingButterfly.MAX_SPEED *= 0.75;
			ConnectedTriangle.MAX_SPEED *= 0.7;
			break;
		case Engine.MEDIUM_D:
			break;
		case Engine.HARD_D:
			ShySquare.MAX_SPEED *= 1.25;
			SimpleSpinner.MAX_SPEED *= 1.25;
			HomingDiamond.MAX_SPEED *= 1.25;
			SplitingSquare.MAX_SPEED *= 1.25;
			HomingCircle.MAX_SPEED *= 1.15;
			BlackHole.MAX_SPEED *= 1.15;
			SnakeHead.MAX_SPEED *= 1.15;
			ShieldedClone.MAX_SPEED *= 1.15;
			HomingButterfly.MAX_SPEED *= 1.15;
			ConnectedTriangle.MAX_SPEED *= 1.15;
			
			
			break;
			
		}
	}
	
	//time dependant things updated here
	public void update(double dt) {
		hasShield = Math.max(0, hasShield-dt);
		hasSideBullets = Math.max(0, hasSideBullets-dt);
		hasRearBullets = Math.max(0, hasRearBullets-dt);

		hasBouncyShot = Math.max(0, hasBouncyShot-dt);
		hasSuperShot = Math.max(0, hasSuperShot-dt);
		
		time += dt;
	}

	public void addKill() {
		kills++;
		totalKills++;
		if (kills >= killSteps[multiplier-1]) {
			multiplier = Math.min(multiplier+1, 9); //please don't ever get this much (might break)
			TextPopup s = new TextPopup(Engine.WHITE, "Multiplier:"+multiplier, 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
			heighestMulti = Math.max(multiplier, heighestMulti);
		}
	}
	public void addScore(int add) {
		//please don't give a negative, won't work (will give 0)
			//also can't put a limit on it because blackholes
		if (add > 0) { 
			score += add*multiplier;
			if (score > lastRecord) {
				lastRecord = score;
			}
		}
	}
	
	public void useBomb() {
		if (bombCount > 0) {
			bombCount--;
			Engine.killAll(null, true, false);
			kills = 0;
			multiplier = 1;
			hasShield = 2;
		}
	}
	
	public void lostLife() {
		lives--;
		kills = 0;
		multiplier = 1;

		deaths++;
		
		bulletSpeed = Math.max(1, bulletSpeed-SPEED_INC); //set minimum bullet speed to be 1
		bulletCount = Math.max(2, bulletCount-1); //set min bullet count to be 2
		
		hasSideBullets = 0;
		hasRearBullets = 0;
		
		hasBouncyShot = 0;
		hasSuperShot = 0;
	}
	
	//technically setters:
	public void incBulletSpeed() {
		bulletSpeed += SPEED_INC;
		TextPopup s = new TextPopup(Engine.WHITE, "Bullet Speed++", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		powerupCount++;
	}
	public void incBulletCount() {
		if (bulletCount > 3) {
			score += 2000; //in the gridwars wiki
			TextPopup s = new TextPopup(Engine.WHITE, "2000 added", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		} else {
			bulletCount++;
			powerupCount++;
			TextPopup s = new TextPopup(Engine.WHITE, "Bullet Count++", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		}
	}
	public void incBombCount() {
		if (bombCount > 8) {
			score += 2000;
			TextPopup s = new TextPopup(Engine.WHITE, "2000 added", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		} else {
			bombCount++;
			powerupCount++;
			TextPopup s = new TextPopup(Engine.WHITE, "Bomb++", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		}
	}
	public void incLives() {
		if (lives > 8) {
			score += 2000;
			TextPopup s = new TextPopup(Engine.WHITE, "2000 added", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		} else {
			lives++;
			powerupCount++;
			TextPopup s = new TextPopup(Engine.WHITE, "Life++", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		}
	}

	public void gotShield()     { 
		hasShield = POWERUP_LENGTH_LONG;
		TextPopup s = new TextPopup(Engine.WHITE, "Shield", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		powerupCount++;
	}
	public void gotSideShot()   { 
		hasSideBullets = POWERUP_LENGTH_LONG;
		TextPopup s = new TextPopup(Engine.WHITE, "Side Shot", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		powerupCount++;
	}
	public void gotRearShot()   { 
		hasRearBullets = POWERUP_LENGTH_LONG; 
		TextPopup s = new TextPopup(Engine.WHITE, "Rear Shot", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		powerupCount++;
	}
	public void gotBouncyShot() { 
		hasBouncyShot = POWERUP_LENGTH_LONG; 
		TextPopup s = new TextPopup(Engine.WHITE, "Bouncy Shot", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		powerupCount++;
	}
	public void gotSuperShot()  { 
		hasSuperShot = POWERUP_LENGTH_SHORT; 
		TextPopup s = new TextPopup(Engine.WHITE, "Super Shot", 2, Engine.getPlayerPos()[0]-0.5, Engine.getPlayerPos()[1]);
		powerupCount++;
	}

	
	//Getters:	
	public int getRecord()     { return lastRecord; }
	public int getScore()      { return score;      }
	public int getLives()      { return lives;      }
	public int getMultiplier() { return multiplier; }
	public int getKills()      { return kills;      }
	public double getTime()    { return time*10;    }

	public String getDifficulty() {	return difficulty; }
	
	public int getTotalDeaths() { return deaths;      }
	public int getTotalKills()  { return totalKills;  }
	public int getPowerUpCount(){ return powerupCount;}
	
	public int    getBombCount()   { return bombCount;   }
	public int    getBulletCount() { return bulletCount; }
	public double getBulletSpeed() { return bulletSpeed; }

	public boolean ifTempShield() { return (hasShield > 0);      }
	public boolean ifSideShot()   { return (hasSideBullets > 0); }
	public boolean ifRearShot()   { return (hasRearBullets > 0); }
	public boolean ifBouncyShot() { return (hasBouncyShot > 0);  }
	public boolean ifSuperShot()  { return (hasSuperShot > 0);   }
	
	
	public String toString() { //has been outdated
		return "Score:  " +score + "\nKills: " + kills + "\nPower ups: " + powerupCount;
	}
}
