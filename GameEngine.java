import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.awt.TextRenderer;

//does all the back-end logic..
public class GameEngine implements GLEventListener {

	private Camera myCamera;
	private long myTime;
	private long startTime;
	
	private TextRenderer renderer;
	
	public static final Random rand = new Random(); 
			//just because ease of use, for specific games, might be given a seed
	
	public static GameData curGame;
	
	public static int boardWidth = 12, 
				boardHeight = 10; //at least 4 please
	
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, mousePos = new double[]{0,0}; //updated each 'update()' for speed of access
	
	////Textures
	public static MyTexture[] textures;
	public static final String EXTENSION = "png";
	public static final int TEXTURE_SIZE = 25; //space for more
	public static final int PLAYER = 0, SPINNER = 1, DIAMOND = 2, SQUARE = 3, BULLET = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,
				SNAKEBODY = 8, BUTTERFLY = 9, CIRCLE = 10, SHY = 11, EXTRA_BULLET = 12, EXTRA_SPEED = 13, TEMP_SHIELD = 14, EXTRA_BOMB =15,
				EXTRA_LIFE = 16, BOUNCY_SHOT = 17, SUPER_SHOT = 18, REAR_SHOT = 19, SIDE_SHOT = 20;
	
	////Colours
	public static final double[] WHITE = {1,1,1,0.5}, RED = {1,0,0,0.5}, LIGHT_BLUE = {0,1,0.8,0.5}, GREEN = {0,1,0,0.5},
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, BLUE = {0.2,0.2,1,0.5}, ORANGE = {1,0.6,0,0.5},
			REALLY_LIGHT_BLUE = {0,1,0.9,0.5};
	
	public GameEngine(Camera camera, int width, int height) {
		startTime = System.currentTimeMillis();
		myCamera = camera;
		curGame = new GameData();
		boardWidth = width;
		boardHeight = height;
	}
	
	
	public static double[] getPlayerPos() { return playerPos; }
	public static double[] getMousePos() {  return mousePos;  }
	public static GameObject getPlayer() {  return player;    }
	
	
	@Override
	public void display(GLAutoDrawable drawable) {
		update();

		GL2 gl = drawable.getGL().getGL2();

		// set the view matrix based on the camera position
		myCamera.setView(gl);
		
		Mouse.theMouse.update(gl);
		playerPos = player.getPosition();
		mousePos = Mouse.theMouse.getPosition();
		
		GameObject.ROOT.draw(gl);
		
		//Draw the text
		renderer.beginRendering(drawable.getWidth(), drawable.getHeight(), true);
		renderer.setColor(0.3f, 0.7f, 1.0f, 0.8f);
		renderer.draw("S:" + curGame.score+" | L:"+curGame.lives+ " | B: " + curGame.bombCount +
				" | x"+curGame.multi +" | Time:  "+(myTime-startTime)/1000, 10, drawable.getHeight()-22);
		renderer.endRendering();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		myTime = System.currentTimeMillis();
		GL2 gl = drawable.getGL().getGL2();
		
		textures = new MyTexture[TEXTURE_SIZE]; 
		textures[PLAYER] = new MyTexture(gl, "player.png", EXTENSION);
		textures[SPINNER] = new MyTexture(gl, "spinner.png", EXTENSION);
		textures[DIAMOND] = new MyTexture(gl, "diamond.png", EXTENSION);
		textures[SQUARE] = new MyTexture(gl, "square.png", EXTENSION);
		textures[BULLET] = new MyTexture(gl, "bullet.png", EXTENSION);
		textures[SHIELD] = new MyTexture(gl, "shield.png", EXTENSION);
		textures[SNAKEBODY] = new MyTexture(gl, "snakeBody.png", EXTENSION);
		textures[SNAKEHEAD] = new MyTexture(gl, "snakeHead.png", EXTENSION);
		textures[BUTTERFLY] = new MyTexture(gl, "butterfly.png", EXTENSION);
		textures[CIRCLE] = new MyTexture(gl, "circle.png", EXTENSION);
		textures[SHY] = new MyTexture(gl, "shy.png", EXTENSION);
		textures[EXTRA_BULLET] = new MyTexture(gl, "extraBullet.png", EXTENSION);
		textures[EXTRA_SPEED] = new MyTexture(gl, "extraSpeed.png", EXTENSION);
		textures[TEMP_SHIELD] = new MyTexture(gl, "tempShield.png", EXTENSION);
		textures[EXTRA_BOMB] = new MyTexture(gl, "extraBomb.png", EXTENSION);
		textures[EXTRA_LIFE] = new MyTexture(gl, "extraLife.png", EXTENSION);
		textures[BOUNCY_SHOT] = new MyTexture(gl, "bouncyShot.png", EXTENSION);
		textures[SUPER_SHOT] = new MyTexture(gl, "superShot.png", EXTENSION);
		textures[REAR_SHOT] = new MyTexture(gl, "rearShot.png", EXTENSION);
		textures[SIDE_SHOT] = new MyTexture(gl, "sideShot.png", EXTENSION);
		
		
		renderer = new TextRenderer(new Font("Courier", Font.BOLD, 22), true);
		
		playerPos = player.getPosition();
        mousePos = Mouse.theMouse.getPosition();
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT); //Set wrap mode for texture in S direction
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);//Set wrap mode for texture in T direction
		
		gl.glEnable(GL2.GL_BLEND); //alpha blending (you know transparency)
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA); //special blending
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// tell the camera and the mouse that the screen has reshaped
        GL2 gl = drawable.getGL().getGL2();
        
        myCamera.reshape(gl, x, y, width, height);
        
        // this has to happen after myCamera.reshape() to use the new projection
        Mouse.theMouse.reshape(gl);
	}

	private void update() {
		long time = System.currentTimeMillis();
        double dt = (time - myTime) / 1000.0;
        myTime = time;
        
        List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
        
        // update all objects
        for (GameObject g: objects) {
            g.update(dt);
        }
        
        curGame.update(dt); //to count down the timer for the powerups
	}

	@Override
	public void dispose(GLAutoDrawable drawable) { }


	/**nice small class to handle the current game state (could be given to a leaderboard class later)
	 * @author Jake Murphy
	 */
	class GameData {
		private final double SPEED_INC = 0.2;
		
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
		
		GameData() {
			isShield = 0;
			isSideBullets = 0;
			isRearBullets = 0;
			
			isBouncyShot = 0;
			isSuperShot = 100;
			
			score = 0;
			lives = 3;
			multi = 1;
			bombCount = 2;
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
		
		public void lostLife() {
			lives--;
			bulletSpeed = Math.max(1, bulletSpeed-SPEED_INC); //set minimum bullet speed to be 1
			bulletCount = Math.max(2, bulletCount-1); //set min bullet count to be 2
			isShield = 1; //set temp shield (for 1 sec) so you don't die really quick
			
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
		public void gotShield() { isShield = 1;	}
		public void gotSideShot() {	isSideBullets = 1; }
		public void gotRearShot() {	isRearBullets = 1; }
		public void gotBouncyShot() { isBouncyShot = 1; }
		public void gotSuperShot() { isSuperShot = 1; }

		
		public double getBulletSpeed() {
			return bulletSpeed;
		}
		public int getBulCount() {
			return bulletCount;
		}
		public boolean ifTempShield() {
			return (isShield > 0);
		}
		public boolean ifSideShot() {
			return (isSideBullets > 0);
		}
		public boolean ifRearShot() {
			return (isRearBullets > 0);
		}
		public boolean ifBouncyShot() {
			return (isBouncyShot > 0);
		}
		public boolean ifSuperShot() {
			return (isSuperShot > 0);
		}
	}
}
