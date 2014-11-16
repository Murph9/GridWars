
/**Nice class to handle the current game state (could be given to a leaderboard class later for saving....)
 * @author Jake Murphy
 */
public class GameState {
	
	private final double SPEED_INC = 0.15;
	private final double POWERUP_LENGTH = 15; //apparently 15 seconds is the standard
	
	private int[] killSteps = new int[]{25,50,100,200,400,800,1600,3200,6400,12800}; //multipler steps
	
	private double isShield;
	private double isSideBullets;
	private double isRearBullets;
	
	private double isBouncyShot;
	private double isSuperShot;
	
	private int score;
	private int lives;
	private int multiplier;
	private int kills;
	
	private int bombCount; //don't really have a mouse listener for that...
	private int bulletCount;
	private double bulletSpeed;
	
	GameState() {
		isShield = 0;
		isSideBullets = 0;
		isRearBullets = 0;
		
		isBouncyShot = 0;
		isSuperShot = 0;
		
		score = 0;
		multiplier = 1;
		kills = 0;
		
		lives = 3;		
		bombCount = 3;
		bulletCount = 4;
		bulletSpeed = 1; //how to change the bullet speed
	}
	
	//because it has time dependant things
	public void update(double dt) {
		isShield = Math.max(0, isShield-dt);
		isSideBullets = Math.max(0, isSideBullets-dt);
		isRearBullets = Math.max(0, isRearBullets-dt);

		isBouncyShot = Math.max(0, isBouncyShot-dt);
		isSuperShot = Math.max(0, isSuperShot-dt);
	}

	public void addKill() {
		kills++;
		if (kills >= killSteps[multiplier-1]) {
			multiplier = Math.min(multiplier+1, 10); //please don't ever get this much
		}
	}
	public void addScore(int add) {
		if (add > 0) { //please don't give a negative, won't work (well or 0)
			score += add*multiplier;
		}
	}
	
	public void useBomb() {
		if (bombCount > 0) {
			bombCount--;
			GameEngine.killAll();
			kills = 0;
			multiplier = 1;
		}
	}
	
	public void lostLife() {
		lives--;
		kills = 0;
		multiplier = 1;
		
		bulletSpeed = Math.max(1, bulletSpeed-SPEED_INC); //set minimum bullet speed to be 1
		bulletCount = Math.max(2, bulletCount-1); //set min bullet count to be 2
		isShield = 3; //set temp shield (for 1 sec) so you don't die really quick
		
		isSideBullets = 0;
		isRearBullets = 0;
		
		isBouncyShot = 0;
		isSuperShot = 0;
	}
	
	public void incBulletSpeed() {
		bulletSpeed += SPEED_INC;
	}
	public void incBulletCount() {
		if (bulletCount > 3) {
			score += 2000; //in the gridwars wiki
		} else {
			bulletCount++;
		}
	}
	public void incBombCount() {
		if (bombCount > 8) {
			score += 2000;
		} else {
			bombCount++;
		}
	}
	public void incLives() {
		if (lives > 8) {
			score += 2000;
		} else {
			lives++;
		}
	}

	//Then start the count down (with the time set)
	public void gotShield() { isShield = POWERUP_LENGTH; }
	public void gotSideShot() {	isSideBullets = POWERUP_LENGTH; }
	public void gotRearShot() {	isRearBullets = POWERUP_LENGTH; }
	public void gotBouncyShot() { isBouncyShot = POWERUP_LENGTH; }
	public void gotSuperShot() { isSuperShot = POWERUP_LENGTH; }

	public int getScore() { return score; }
	public int getLives() { return lives; }
	public int getMultiplier() { return multiplier; }
	public int getKills() { return kills; }
	
	public int getBombCount() { return bombCount; }
	public int getBulletCount() { return bulletCount; }
	public double getBulletSpeed() { return bulletSpeed; }

	public boolean ifTempShield() { return (isShield > 0); }
	public boolean ifSideShot() { return (isSideBullets > 0); }
	public boolean ifRearShot() { return (isRearBullets > 0); }
	public boolean ifBouncyShot() { return (isBouncyShot > 0); }
	public boolean ifSuperShot() { return (isSuperShot > 0); }
}
