package game.logic;
import game.objects.*;

@SuppressWarnings("unused") //alot of 's' that i just don't need to see here 
				//(im using the eclipse editor if you hadn't figured already)

/**Nice class to handle the current game state, handed to the game engine
 * to use. Has a lot of read only variables and should only contain info for this game
 * @author Jake Murphy
 */
public class GameState {
	
	private final double SPEED_INC = 0.15;
	private final double POWERUP_LENGTH = 15; //15 seconds is the standard (according to the game)
	
	private final int[] killSteps = new int[]{25,50,100,200,400,800,1600,3200,6400,12800}; //multipler steps
	
	//player states
	private double 
			hasShield, hasSideBullets, hasRearBullets, 
			hasBouncyShot, hasSuperShot;
	
	//Current game values
	private int 
		lastRecord, score, lives, multiplier, kills;
	private String diff;
	
	private int bombCount;
	private int bulletCount;
	private double bulletSpeed;
	private double time;
	
	//for the long term stats file: (TODO more)
	private int deaths, totalKills, powerupCount, heighestMulti;
	
	//Settings:
	private int pixelWidth, pixelHeight;
	private int boardWidth, boardHeight;
	private double scale;
	
	private boolean ifParticles; //should have more than on/off options
	private boolean ifAliasing;
	
	
	/**
	 * @param boardWidth - boardwidth
	 * @param boardHeight - boardheight
	 * @param diff - game difficulty
	 * @param scale - zoom level (usually 10)
	 * @param settings - list of settings defined by TheGame
	 */
	GameState(int boardWidth, int boardHeight, String diff, double scale, boolean[] settings) { 
			//note how anything not here is initalised to 0.
		this.lastRecord = LeaderBoard.getBestScore(diff);
		
		lives = 3;
		multiplier = 1;
		
		bombCount = 3;
		bulletCount = 4;
		bulletSpeed = 1; //how to change the intial bullet speed
		
		//part of settings
		this.diff = diff;
		
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.scale = scale;
		
		this.ifParticles = settings[0]; //definately helps
		this.ifAliasing = settings[1]; //hasn't been tested to cause lag yet
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
			TextPopup s = new TextPopup(GameEngine.WHITE, "Multiplier:"+multiplier, 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
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
			GameEngine.killAll(null);
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
		hasShield = 3; //set temp shield (for 1 sec) so you don't die really quick
		
		hasSideBullets = 0;
		hasRearBullets = 0;
		
		hasBouncyShot = 0;
		hasSuperShot = 0;
	}
	
	//technically setters:
	public void incBulletSpeed() {
		bulletSpeed += SPEED_INC;
		TextPopup s = new TextPopup(GameEngine.WHITE, "Bullet Speed++", 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
		powerupCount++;
	}
	public void incBulletCount() {
		if (bulletCount > 3) {
			score += 2000; //in the gridwars wiki
			TextPopup s = new TextPopup(GameEngine.WHITE, 2000, 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
		} else {
			bulletCount++;
			powerupCount++;
			TextPopup s = new TextPopup(GameEngine.WHITE, "Bullet Count++", 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
		}
	}
	public void incBombCount() {
		if (bombCount > 8) {
			score += 2000;
			TextPopup s = new TextPopup(GameEngine.WHITE, 2000, 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
		} else {
			bombCount++;
			powerupCount++;
			TextPopup s = new TextPopup(GameEngine.WHITE, "Bomb++", 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
		}
	}
	public void incLives() {
		if (lives > 8) {
			score += 2000;
			TextPopup s = new TextPopup(GameEngine.WHITE, 2000, 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
		} else {
			lives++;
			powerupCount++;
			TextPopup s = new TextPopup(GameEngine.WHITE, "Life++", 2, GameEngine.getPlayerPos()[0]-0.5, GameEngine.getPlayerPos()[1]);
		}
	}

	public void setPixelHeight(int height) {
		this.pixelHeight = height;
	}
	public void setPixelWidth(int width) {
		this.pixelWidth = width;
	}
	
	
	//Getters:
	public void gotShield()     { hasShield      = POWERUP_LENGTH; }
	public void gotSideShot()   { hasSideBullets = POWERUP_LENGTH; }
	public void gotRearShot()   { hasRearBullets = POWERUP_LENGTH; }
	public void gotBouncyShot() { hasBouncyShot  = POWERUP_LENGTH; }
	public void gotSuperShot()  { hasSuperShot   = POWERUP_LENGTH; }

	public int getRecord()     { return lastRecord; }
	public int getScore()      { return score;      }
	public int getLives()      { return lives;      }
	public int getMultiplier() { return multiplier; }
	public int getKills()      { return kills;      }
	public double getTime()    { return time*10;    }
	
	public int getTotalDeaths(){ return deaths;     }
	public int getTotalKills() { return totalKills; }
	
	public int    getBombCount()   { return bombCount;   }
	public int    getBulletCount() { return bulletCount; }
	public double getBulletSpeed() { return bulletSpeed; }

	public boolean ifTempShield() { return (hasShield > 0);      }
	public boolean ifSideShot()   { return (hasSideBullets > 0); }
	public boolean ifRearShot()   { return (hasRearBullets > 0); }
	public boolean ifBouncyShot() { return (hasBouncyShot > 0);  }
	public boolean ifSuperShot()  { return (hasSuperShot > 0);   }
	
	//thinking of compressing this to a settings file..
	public int getPixelWidth()  { return pixelWidth;  }
	public int getPixelHeight() { return pixelHeight; }
	public int getBoardWidth()  { return boardWidth;  }
	public int getBoardHeight() { return boardHeight; }
	
	public double  getScale()    { return scale;       }
	public String  getDifficulty(){return diff;        }
	public boolean ifParticles() { return ifParticles; }
	public boolean ifAliasing()  { return ifAliasing;  }
	
	public String toString() { //has been outdated
		return "Closed score = "+score;
	}
}
