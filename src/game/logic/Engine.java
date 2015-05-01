package game.logic;
import game.objects.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.gl2.GLUT;


//TODO Idea for gameplay slow down when blackhole is exploded (mixed with a overpowering sound to help the effect)

/**
 * Handles all moving objects and all OpenGL initialisation.
 * @author Jake Murphy
 */
public class Engine implements GLEventListener {

	////Textures
	public static MyTexture[] textures;
	public static final int TEXTURE_SIZE = 35; //space for more (and order space)
	public static final int /**/
			SHY = 0, SPINNER = 1, DIAMOND = 2, SQUARE = 3, TRIANGLE = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,  
			SNAKEBODY = 8, BUTTERFLY = 9, CIRCLE = 10, BLACKHOLE = 11,
							/**/
			PLAYER = 20, BULLET = 21, EXTRA_BULLET = 22, EXTRA_SPEED = 23, TEMP_SHIELD = 24, EXTRA_BOMB = 25, 
			EXTRA_LIFE = 26, BOUNCY_SHOT = 27, SUPER_SHOT = 28, REAR_SHOT = 29, SIDE_SHOT = 30,
							/**/
			PLAYER_SHEILD = 31;
	
	////Colours - note the alpha values
	public static final double[] WHITE = {1,1,1,0.5}, RED = {1,0,0,0.5}, LIGHT_BLUE = {0,1,0.8,0.5}, GREEN = {0,1,0,0.5},
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, LIGHT_YELLOW = {1,1,0.2,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, 
			BLUE = {0.2,0.2,1,0.5},	ORANGE = {1,0.6,0,0.5}, REALLY_LIGHT_BLUE = {0,1,0.9,0.5};

	//Difficulty:
	public static final String 	EASY_D = "easy", MEDIUM_D = "medium", 
								HARD_D = "hard", EXT_D = ".txt";
	
	//Game Static variables
	public static SpawnHandler spawner;
	public static GameState gameState; //so every file can access these
	public static GameSettings settings;
	private ShaderControl shader; //might have an option to change this later

	public static Camera myCamera;
	public static SpringGrid grid;
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, mousePos = new double[]{0,0}; 
				//updated each 'update()' for speed of access, all methods should use this
	
	public static final Random rand = new Random();
				//just because ease of use, ive heard making a random object is slow [unconfirmed]

	private static final double KILL_SCREEN_TIME = 2;
				//for measuring the respawn speed

	private double curAspect; //for the GUI positioning
	private long myTime;
	
	@SuppressWarnings("unused")
	private double fps;
	
	private static boolean isPaused;
	
	private static GameObject killObj;
	private static double hitObjCounter; //if just died

	private static double respawnCounter; //if respawning
	private static boolean gameOver = false;
	
	
	/**Instantiates a new game engine, should have an animator called on this as well.
	 */
	@SuppressWarnings("unused")
	public Engine(SpawnHandler inSpawnner, GameState inState, GameSettings inSettings) {
		
		Border border = new Border(inSettings.getBoardWidth(), inSettings.getBoardHeight());
		
		grid = new SpringGrid(inSettings.getGridXCount(),inSettings.getGridYCount(),
										inSettings.getBoardWidth(),inSettings.getBoardHeight());

		spawner = inSpawnner;
		gameState = inState;
		settings = inSettings;
		
		myCamera = new Camera();
		
		Engine.player = new Player(1, Engine.WHITE);
		textures = new MyTexture[TEXTURE_SIZE];
		
		isPaused = false;
		respawnCounter = 2; //seems fine
		
		TextPopup t = new TextPopup(WHITE, "Ready", 0.1, -0.6, 1);
		TextPopup t2 = new TextPopup(WHITE, "Next Powerup At: " + Engine.spawner.getNextPowerup(), 0.1, -3, -2);
	}
	
	public static double[] getPlayerPos(){  return playerPos; }
	public static double[] getMousePos() {  return mousePos;  }
	public static GameObject getPlayer() {  return player;    }

	/**Initalises what is needed for the base game to work, (but not an actually playable game)
	 * Everything should be in the constructor unless it needs gl context
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		myTime = System.currentTimeMillis(); //this is here because of timing
		GL2 gl = drawable.getGL().getGL2();
		
		String img = "images/"; //because everything is in its own folder
		
		textures[PLAYER] = new MyTexture(gl, img + "player.png");
		textures[SPINNER] = new MyTexture(gl, img + "spinner.png");
		textures[DIAMOND] = new MyTexture(gl, img + "diamond.png");
		textures[SQUARE] = new MyTexture(gl, img + "square.png");
		textures[BULLET] = new MyTexture(gl, img + "bullet.png");
		textures[SHIELD] = new MyTexture(gl, img + "shield.png");
		textures[SNAKEBODY] = new MyTexture(gl, img + "snakeBody.png");
		textures[SNAKEHEAD] = new MyTexture(gl, img + "snakeHead.png");
		textures[BUTTERFLY] = new MyTexture(gl, img + "butterfly.png");
		textures[CIRCLE] = new MyTexture(gl, img + "seeker.png");
		textures[SHY] = new MyTexture(gl, img + "shy.png");
		textures[BLACKHOLE] = new MyTexture(gl, img + "circle.png");
		textures[TRIANGLE] = new MyTexture(gl, img + "triangle.png");
		
		textures[EXTRA_BULLET] = new MyTexture(gl, img + "extraBullet.png");
		textures[EXTRA_SPEED] = new MyTexture(gl, img + "extraSpeed.png");
		textures[TEMP_SHIELD] = new MyTexture(gl, img + "tempShield.png");
		textures[EXTRA_BOMB] = new MyTexture(gl, img + "extraBomb.png");
		textures[EXTRA_LIFE] = new MyTexture(gl, img + "extraLife.png");
		textures[BOUNCY_SHOT] = new MyTexture(gl, img + "bouncyShot.png");
		textures[SUPER_SHOT] = new MyTexture(gl, img + "superShot.png");
		textures[REAR_SHOT] = new MyTexture(gl, img + "rearShot.png");
		textures[SIDE_SHOT] = new MyTexture(gl, img + "sideShot.png");
		
		textures[PLAYER_SHEILD] = new MyTexture(gl, img+"playerShield.png");
		
		playerPos = player.getPosition(); //this is here because it requires a screen to function
        mousePos = Mouse.theMouse.getPosition();
        
        gameOver = false;
        
        //enable textures
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP); //Set wrap mode for texture in S direction
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP); //Set wrap mode for texture in T direction
		
		gl.glEnable(GL2.GL_BLEND); //alpha blending (you know transparency)
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA); //special blending
		
		if (settings.ifAliasing()) {
			gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
			gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
			gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		} else {
			gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_FASTEST);
			gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_FASTEST);
			gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_FASTEST);
		}
		
		// create a new shader object that we can reference later to activate it.
		shader = new ShaderControl();
		shader.fsrc = shader.loadShader(img + "f.txt"); // fragment GLSL Code
		shader.vsrc = shader.loadShader(img + "v.txt"); // vertex GLSL Code
		shader.init(gl);
		//this seems to be fine above, check the shader use in draw
		
		SoundEffect.init(); //sounds are per file now
		if (settings.ifSound()) {
			SoundEffect.volume = SoundEffect.Volume.HIGH;
		} else {
			SoundEffect.volume = SoundEffect.Volume.MUTE; //too easy i get it..
		}
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		settings.setPixelHeight(drawable.getSurfaceHeight()); //might decide that the window can't be resized later 
		settings.setPixelWidth(drawable.getSurfaceWidth());
		
		if (gameOver) { //killed in the last update
			TheGame.reloadMenu(gameState, settings.getName());
			GameObject.ALL_OBJECTS.clear();
			return; //game will die now :)
		}
		
		update();

		GL2 gl = drawable.getGL().getGL2();
		
		shader.useShader(gl); //it has a disable method but i want everything to use it
		
		// set the view matrix based on the camera position
		myCamera.setView(gl);
		
		Mouse.theMouse.update(gl); //only update method allowed outside of update(), requires gl object
		playerPos = player.getPosition(); //by extension these 2 other update line need to be here
		mousePos = Mouse.theMouse.getPosition();
		
		
		if (hitObjCounter > 0) { //if just hit object, show object
			LinkedList<GameObject> allObj = new LinkedList<GameObject>(GameObject.ALL_OBJECTS);
			for (GameObject o : allObj) {
				if (!(o instanceof Player)) {
					o.draw(gl);
				}
			}	
			
		} else { //normal gamplay
			LinkedList<GameObject> allObj = new LinkedList<GameObject>(GameObject.ALL_OBJECTS);
			for (GameObject o: allObj) {
				o.draw(gl);
			}
		}
		
		
		//start again to draw the GUI
		gl.glPushMatrix();
		gl.glLoadIdentity();
		drawGUI(gl);
		gl.glPopMatrix();

		shader.dontUseShader(gl);
	}

	
	/** Updates all the game objects, called from display(GL)
	 */
	private void update() {
		long time = System.currentTimeMillis();
		this.fps = time - myTime;
		double dt = (time - myTime) / 1000.0;
		myTime = time;
		
		//lag handling TO?DO
		double lagfixeddt = 0.01666; //seems to work at this point, as its always the same
		
		if (hitObjCounter > 0) { //just hit something
			hitObjCounter -= dt;
			//animate just the particles because visuals are cool
			LinkedList<Particle> all = new LinkedList<Particle>(Particle.ALL_THIS);
			for (GameObject obj: all) {
				obj.update(dt);
			}
			grid.update(dt);
			
			return; //because we don't want anything else to move
			
		} else if (respawnCounter > 0) {
			respawnCounter -= dt;
			
			if (killObj != null) { //first time in respawn, 
				killObj = null; //so it finally goes away
				
				Particle.ALL_THIS.clear(); //no more particles please
				Engine.killAll(null, false, false); //kill everything for real this time
				
				Engine.spawner.lostLife(); //TODO?: maybe listeners for this kind of stuff?
				Engine.gameState.lostLife();
				
				grid.resetAll(); //reset grid so its easier
				
				Engine.player.x = 0;
				Engine.player.y = 0;
				Engine.player.dx = 0;
				Engine.player.dy = 0;
				Engine.myCamera.x = 0;
				Engine.myCamera.y = 0; //move camera and player to center
				
				@SuppressWarnings("unused")
				TextPopup t = new TextPopup(WHITE, "Ready", 0.1, -0.6, 1);
				@SuppressWarnings("unused")
				TextPopup t2 = new TextPopup(WHITE, "Next Powerup At: " + Engine.spawner.getNextPowerup(), 0.1, -3, -2);
			}
			
			return; //nothing moves still
		}
		
		
		if (isPaused) {
			//no updates
		} else {
			spawner.update(lagfixeddt); //needs to spawn objects before objects are updated (because they spawn in the middle)
			
			// update all objects
			List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
			for (GameObject g: objects) {
				g.update(lagfixeddt);
			}

			gameState.update(lagfixeddt); //to count down the powerups timers
		}
	}


	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// tell the camera and the mouse that the screen has reshaped
		GL2 gl = drawable.getGL().getGL2();
		
		myCamera.reshape(gl, x, y, width, height);
		curAspect = (double)width / (double)height; //so the GUI elements are fine
		
		// this has to happen after myCamera.reshape() to use the new projection matrix
		Mouse.theMouse.reshape(gl);
	}
	
	/**Handles the position of the GUI, mainly using curAspect, and assumes its given a identity to use
	 * @param gl
	 */
	private void drawGUI(GL2 gl) {
		gl.glLoadIdentity();
		GLUT glut = new GLUT();
		gl.glLineWidth(2f);
		gl.glColor3d(1,1,1);
		
		//score
			String score = " "+gameState.getScore();
			gl.glPushMatrix();
			gl.glTranslated(-1*curAspect,0.9,0);
			gl.glScalef(0.0005f, 0.0005f, 1); //for some reason it starts very big (152 or something)
		
			for (int i = 0; i < score.length(); i++) {
				char ch = score.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
		
		//record
			String record = " "+gameState.getRecord();
			gl.glPushMatrix();
			gl.glTranslated(-1*curAspect,0.8,0);
			gl.glScalef(0.0004f, 0.0004f, 1); //for some reason it starts very big (152 or something)
		
			for (int i = 0; i < record.length(); i++) {
				char ch = record.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
			
		//lives
			String lives = "";
			if (gameState.getLives() > 0) {
				lives = " "+gameState.getLives();
			
				gl.glPushMatrix();
				gl.glTranslated(0.9*curAspect,0.9,0);
				gl.glScalef(0.1f, 0.1f, 1);
				gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.EXTRA_LIFE].getTextureId());
				Helper.square(gl);
				
				gl.glScalef(0.005f, 0.005f, 1); //for some reason it starts very big (152 or something)
				gl.glTranslated(10,-50,0);
				for (int i = 0; i < lives.length(); i++) {
					char ch = lives.charAt(i);
					glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
				}
				gl.glPopMatrix();
			}
			
		//bomb count
			String bCount = "";
			if (gameState.getBombCount() > 0) {
				bCount = " "+gameState.getBombCount();
				
				gl.glPushMatrix();
				gl.glTranslated(0.9*curAspect,0.8,0);
				gl.glScalef(0.1f, 0.1f, 1);
				gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.EXTRA_BOMB].getTextureId());
				Helper.square(gl);
				
				gl.glScalef(0.005f, 0.005f, 1); //for some reason it starts very big (152 or something)
				gl.glTranslated(10,-50,0);
				for (int i = 0; i < bCount.length(); i++) {
					char ch = bCount.charAt(i);
					glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
				}
				gl.glPopMatrix();
			}
		
		//multiplier
			String multi = "x"+gameState.getMultiplier();
			gl.glPushMatrix();
			gl.glTranslated(0,0.9,0);
			gl.glScalef(0.0005f, 0.0005f, 1); //for some reason it starts very big (152 or something)
	
			for (int i = 0; i < multi.length(); i++) {
				char ch = multi.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
		
		//kills
			String kills = "  "+gameState.getKills();
			
			gl.glPushMatrix();
			gl.glTranslated(0.8 *curAspect,0.7,0);
			gl.glScalef(0.0004f, 0.0004f, 1); //for some reason it starts very big (152 or something)
	
			for (int i = 0; i < kills.length(); i++) {
				char ch = kills.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
			
			String tKills = "  "+gameState.getTotalKills();
			gl.glPushMatrix();
			gl.glTranslated(0.8*curAspect,0.65,0);
			gl.glScalef(0.0004f, 0.0004f, 1);
			
			for (int i = 0; i < tKills.length(); i++) {
				char ch = tKills.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
			
			
		//time
			int a = (int)gameState.getTime();
			int b = a % 10;
			a /= 10;
			String time = "  "+a+"."+b; //math so its in the form ddddd.d
			
			gl.glPushMatrix();
			gl.glTranslated(0.8*curAspect,0.58,0);
			gl.glScalef(0.0004f, 0.0004f, 1); //for some reason it starts very big (152 or something)
	
			for (int i = 0; i < time.length(); i++) {
				char ch = time.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
		
		//frame speed TODO setting
			/*String fString = Double.toString(fps); //math so its in the form ddddd.d
			
			gl.glPushMatrix();
			gl.glTranslated(0.9*curAspect,0.5,0);
			gl.glScalef(0.0004f, 0.0004f, 1); //for some reason it starts very big (152 or something)
	
			for (int i = 0; i < fString.length(); i++) {
				char ch = fString.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();*/
			
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		//called by system when the window is closed
		//no longer a chance of double up as it quits on the next frame :), using gameOver. 

		FileHelper.writeScore(gameState.getDifficulty(), gameState.getScore(), "auto_"+settings.getName(), (int)gameState.getTime());
		FileHelper.addToStats(gameState);
		System.err.println("Game Quit.\n\t- dispose(), Engine");

	}
	
	
	///////////////////////////// Outside reference things
	
	/**Kill all objects on the screen (excluding Player,Border,Camera,Powerups)
	 * @param object Optional game object that will not be deleted after method
	 */
	public static void killAll(GameObject object, boolean particles, boolean killPowerups) {
		LinkedList<GameObject> allList = new LinkedList<GameObject>(GameObject.ALL_OBJECTS);
		for (GameObject obj: allList) {
			if (obj instanceof Player || obj instanceof Border || obj instanceof Camera || obj instanceof SpringGrid) {
			} else if (obj instanceof PowerUp) {
				if (killPowerups) { //if we want it gone
					GameObject.ALL_OBJECTS.remove(obj);
				}
			} else {
				GameObject.ALL_OBJECTS.remove(obj);
					//was going to account for the given object here, 
						//but its easier just to add it after
			}
		}
		
		//calling delete() on each object would be weird
		BlackHole.ALL_THIS.clear();  //just remove the references (this should be easier)
		ConnectedTriangle.ALL_THIS.clear();
		HomingButterfly.ALL_THIS.clear();
		HomingDiamond.ALL_THIS.clear();
		PlayerBullet.ALL_THIS.clear();
		TextPopup.ALL_THIS.clear();
		ShieldedClone.ALL_THIS.clear();
		ShySquare.ALL_THIS.clear();
		SimpleSpinner.ALL_THIS.clear();
		SplitingSquare.ALL_THIS.clear();
		Spawner.ALL_THIS.clear();
		
		if (killPowerups) {
			PowerUp.ALL_THIS.clear(); //remove all powerups on a lost life
		}
		
//		grid.Push(player.x, player.y, 60, 20); //large
		
		if (particles) {
			for (int i = 0; i < 40; i++) {
				for (int j = 0; j < 20; j++) {
					MovingObject p = new Particle(2, Engine.WHITE, 0.7, Particle.DEFAULT_DRAG);
					p.x = playerPos[0];
					p.y = playerPos[1];
					double temp = 180*Engine.rand.nextInt(360)/Math.PI;
					p.dx = Engine.rand.nextDouble()*Math.cos(temp)*50;
					p.dy = Engine.rand.nextDouble()*Math.sin(temp)*50;
				}
			}
		}
		
		if (object != null) {
			GameObject.ALL_OBJECTS.add(object); //now it exists to draw on the killScreen
		}
	}

	/**Checks if the game isn't in the killscreen, every spawn must check here
	 * @return true, if you can spawn objects 
	 */
	public static boolean canSpawn() {
		if (isPaused) {
			return false;
		}
		if (Engine.respawnCounter > 0 || Engine.hitObjCounter > 0) { //in respawn/obj show mode
			return false;
		}
		
		return true;
	}
	
	public static boolean isPaused() {
		return isPaused;
	}
	
	
	public static void gameQuit() {
		gameOver = true;
	}
	
	/** Called when a life is lost
	 * @param obj
	 */
	public static void lostLife(GameObject obj) { 
		Engine.killAll(obj, true, true); //delete everything, but obj
		
		hitObjCounter = Engine.KILL_SCREEN_TIME;
		respawnCounter = Engine.KILL_SCREEN_TIME;
		
		killObj = obj;
		
		if (gameState.getLives() < 1) { //no lives left
			Engine.gameOver = true; //so in the next one it can be killed
		}
	}
	
	
	@SuppressWarnings("unused")
	public static void togglePause() {
		if (Engine.respawnCounter > 0 || Engine.hitObjCounter > 0) {
			return;
		}
		isPaused = !isPaused;
		
		TextPopup text = new TextPopup(WHITE, "Paused (q quits)", 0.1, playerPos[0]-2, playerPos[1]+1.2);
	}
}
