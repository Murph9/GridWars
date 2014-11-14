
/**nice small class to handle the current game state (could be given to a leaderboard class later)
 * @author Jake Murphy
 */
public class GameState {
	
	private final double SPEED_INC = 0.2;
	private final double POWERUP_LENGTH = 15; //apparently 15 seconds
	
	private double isShield;
	private double isSideBullets;
	private double isRearBullets;
	
	private double isBouncyShot;
	private double isSuperShot;
	
	private int score;
	private int lives;
	private int multi;
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
		lives = 3;
		multi = 1;
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

	public void addScore(int add) {
		if (add >= 0) { //please don't give a negative, won't work
			score += add*multi;
		}
	}
	
	public void useBomb() {
		if (bombCount > 0) {
			bombCount--;
			GameEngine.killAll();
		}
	}
	
	public void lostLife() {
		lives--;
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
		bombCount++;
	}
	public void incLives() {
		lives++;
	}
	public void incMultiplier(int in) {
		multi = in;
	}

	//Then start the count down (with the time set)
	public void gotShield() { isShield = POWERUP_LENGTH; }
	public void gotSideShot() {	isSideBullets = POWERUP_LENGTH; }
	public void gotRearShot() {	isRearBullets = POWERUP_LENGTH; }
	public void gotBouncyShot() { isBouncyShot = POWERUP_LENGTH; }
	public void gotSuperShot() { isSuperShot = POWERUP_LENGTH; }

	public int getScore() { return score; }
	public int getLives() { return lives; }
	public int getMultiplier() { return multi; }
	public int getBombCount() { return bombCount; }
	public double getBulletSpeed() { return bulletSpeed; }
	public int getBulletCount() { return bulletCount; }
	public boolean ifTempShield() { return (isShield > 0); }
	public boolean ifSideShot() { return (isSideBullets > 0); }
	public boolean ifRearShot() { return (isRearBullets > 0); }
	public boolean ifBouncyShot() { return (isBouncyShot > 0); }
	public boolean ifSuperShot() { return (isSuperShot > 0); }
}
