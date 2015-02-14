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

/**
 * Handles all moving objects and all OpenGL initialisation.
 * 
 * @author Jake Murphy
 */
public class GameEngine implements GLEventListener {

	////Textures
	public static MyTexture[] textures;
	public static final int TEXTURE_SIZE = 35; //space for more (and order space)
	public static final int /**/
			SHY = 0, SPINNER = 1, DIAMOND = 2, SQUARE = 3, TRIANGLE = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,  
			SNAKEBODY = 8, BUTTERFLY = 9, SEEKER = 10, BLACKHOLE = 11,
							/**/
			PLAYER = 20, BULLET = 21, EXTRA_BULLET = 22, EXTRA_SPEED = 23, TEMP_SHIELD = 24, EXTRA_BOMB = 25, 
			EXTRA_LIFE = 26, BOUNCY_SHOT = 27, SUPER_SHOT = 28, REAR_SHOT = 29, SIDE_SHOT = 30;
	
	////Colours - note the alpha values
	public static final double[] WHITE = {1,1,1,0.5}, RED = {1,0,0,0.5}, LIGHT_BLUE = {0,1,0.8,0.5}, GREEN = {0,1,0,0.5},
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, LIGHT_YELLOW = {1,1,0.2,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, 
			BLUE = {0.2,0.2,1,0.5},	ORANGE = {1,0.6,0,0.5}, REALLY_LIGHT_BLUE = {0,1,0.9,0.5};

	public static final String 	EASY_D = "easy", MEDIUM_D = "medium", 
								HARD_D = "hard", EXT_D = ".txt";
	private static final double EASY_SPEED = 0.85, MEDIUM_SPEED = 1, HARD_SPEED = 1.25; //TODO balance
	
	public static final Random rand = new Random();
		//just because ease of use, ive heard making a random object is slow [unconfirmed]
	
	public SpawnHandler spawner;
	public static GameState curGame; //so every file can access these
	public static GameSettings curSettings;
	
	private double curAspect; //for the GUI positioning

	public static Camera myCamera;
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, mousePos = new double[]{0,0}; 
				//updated each 'update()' for speed of access, all methods should use this
	
	private long myTime;
	
	
	private static final double KILL_SCREEN_TIME = 2;
	//for measuring the respawn speed
	
	private static boolean isPaused;
	private static double killCountdown; //if killscreen
	private static GameObject killObj;
	
	private ShaderControl shader; //might have an option to change this later
	
	/**Instantiates a new game engine, should have an animator called on this as well.
	 */
	public GameEngine(SpawnHandler spawnner, GameState state, GameSettings settings) {
		Border border = new Border(settings.getBoardWidth(), settings.getBoardHeight());
		border.setSize(1); //just incase it stopped being 1 (removes warning)
		
		myCamera = new Camera();
		myCamera.setSize(settings.getScale());
		
		GameEngine.player = new Player(1, GameEngine.WHITE);
		textures = new MyTexture[TEXTURE_SIZE];
		
		spawner = spawnner;
		curGame = state;
		curSettings = settings;
		
		isPaused = false;
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
		textures[SEEKER] = new MyTexture(gl, img + "seeker.png");
		textures[SHY] = new MyTexture(gl, img + "shy.png");
		textures[BLACKHOLE] = new MyTexture(gl, img + "circle.png");

		textures[EXTRA_BULLET] = new MyTexture(gl, img + "extraBullet.png");
		textures[EXTRA_SPEED] = new MyTexture(gl, img + "extraSpeed.png");
		textures[TEMP_SHIELD] = new MyTexture(gl, img + "tempShield.png");
		textures[EXTRA_BOMB] = new MyTexture(gl, img + "extraBomb.png");
		textures[EXTRA_LIFE] = new MyTexture(gl, img + "extraLife.png");
		textures[BOUNCY_SHOT] = new MyTexture(gl, img + "bouncyShot.png");
		textures[SUPER_SHOT] = new MyTexture(gl, img + "superShot.png");
		textures[REAR_SHOT] = new MyTexture(gl, img + "rearShot.png");
		textures[SIDE_SHOT] = new MyTexture(gl, img + "sideShot.png");
		textures[TRIANGLE] = new MyTexture(gl, img + "triangle.png");
		
		playerPos = player.getPosition(); //this is here because it requires a screen to function
        mousePos = Mouse.theMouse.getPosition();
        
        //enable textures
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP); //Set wrap mode for texture in S direction
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP); //Set wrap mode for texture in T direction
		
		gl.glEnable(GL2.GL_BLEND); //alpha blending (you know transparency)
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA); //special blending
		
		if (curSettings.ifAliasing()) {
			gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
			gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
			gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		}
		
		// create a new shader object that we can reference later to activate it.
		shader = new ShaderControl();
		shader.fsrc = shader.loadShader(img + "f.txt"); // fragment GLSL Code
		shader.vsrc = shader.loadShader(img + "v.txt"); // vertex GLSL Code
		shader.init(gl);
		//this seems to be fine above, check the shader use in draw
		
		SoundEffect.init(); //TODO other volumes: HIGH, MEDIUM and LOW
		if (curSettings.ifSound()) {
			SoundEffect.volume = SoundEffect.Volume.HIGH;
		} else {
			SoundEffect.volume = SoundEffect.Volume.MUTE; //too easy i get it..
		}
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		curSettings.setPixelHeight(drawable.getHeight()); //might decide that the window can't be resized later 
		curSettings.setPixelWidth(drawable.getWidth());
		
		update();

		GL2 gl = drawable.getGL().getGL2();
		
		shader.useShader(gl); //it has a disable method but i want everything to use it
		
		// set the view matrix based on the camera position
		myCamera.setView(gl);
		
		Mouse.theMouse.update(gl);
		playerPos = player.getPosition();
		mousePos = Mouse.theMouse.getPosition();
		
		if (killCountdown > 0) { //if in killscreen
			if (killCountdown >= GameEngine.KILL_SCREEN_TIME/2) {
				LinkedList<GameObject> allObj = new LinkedList<GameObject>(GameObject.ALL_OBJECTS);
				for (GameObject o : allObj) {
					if (!(o instanceof Player || o.equals(GameObject.ROOT))) {
						o.draw(gl);
					}
				}
				
			} else {
				GameObject.ROOT.draw(gl);
			}
		} else { //if not then draw everything as normal
			GameObject.ROOT.draw(gl);
		}
		
		//start again to draw the GUI
		gl.glPushMatrix();
		gl.glLoadIdentity();
		drawGUI(gl);
		gl.glPopMatrix();

		shader.dontUseShader(gl);
	}

	
	/** Updates all the game objects
	 */
	private void update() {
		long time = System.currentTimeMillis();
		double dt = (time - myTime) / 1000.0;
		myTime = time;
		
		if (killCountdown > 0) { //if kill count down is active
			killCountdown -= dt;
			
			if (killCountdown <= KILL_SCREEN_TIME/2) { //second half of countdown
				if (killObj != null) { //only happens the once coming into the second half of killscreen
					GameObject.ALL_OBJECTS.remove(killObj); //the obj that hit you is now removed as it doesn't need to be displayed anymore
					killObj = null;
					GameEngine.curGame.lostLife();
					GameEngine.killAll(null, false);
				}
				
				GameEngine.player.x = 0;
				GameEngine.player.y = 0;
				GameEngine.player.dx = 0;
				GameEngine.player.dy = 0;
				GameEngine.myCamera.x = 0;
				GameEngine.myCamera.y = 0; //move camera and player to center
				
				TextPopup t = new TextPopup(WHITE, "Ready", 0.1, -0.6, 1);
				t.angle = 0; //warning message
				
			} else if (killCountdown <= 0) { //the overflow happened this pass
				killCountdown = 0; //so killscreen is over
			}
			
			//animate just the particles because visuals
			LinkedList<Particle> all = new LinkedList<Particle>(Particle.ALL_THIS);
			for (GameObject obj: all) {
				obj.update(dt);
			}
			
			return;
		}
		
		//lag handling TO?DO
		dt = 0.016; //seems to work at this point, as its always the same
		
		if (isPaused) {
			//no updates
		} else {
			spawner.update(dt); //needs to spawn objects before objects are updated (because they spawn in the middle)

			double newDt = dt; //because difficulties have a direct impact on object speed
			if (curGame.getDifficulty().equals(GameEngine.EASY_D)) {
				newDt = dt*EASY_SPEED;
			} else if (curGame.getDifficulty().equals(GameEngine.MEDIUM_D)) {
				newDt = dt*MEDIUM_SPEED;
			} else if (curGame.getDifficulty().equals(GameEngine.HARD_D)) {
				newDt = dt*HARD_SPEED;
			}
			
			// update all objects
			List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
			for (GameObject g: objects) {
				if (g instanceof Player) {
					g.update(dt);
				} else {
					g.update(newDt);
				}
			}

			curGame.update(dt); //to count down the powerups timers
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
			String score = " "+curGame.getScore();
			gl.glPushMatrix();
			gl.glTranslated(-1*curAspect,0.9,0);
			gl.glScalef(0.0005f, 0.0005f, 1); //for some reason it starts very big (152 or something)
		
			for (int i = 0; i < score.length(); i++) {
				char ch = score.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
		
		//record
			String record = " "+curGame.getRecord();
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
			if (curGame.getLives() > 0) {
				lives = " "+curGame.getLives();
			
				gl.glPushMatrix();
				gl.glTranslated(0.9*curAspect,0.9,0);
				gl.glScalef(0.1f, 0.1f, 1);
				gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_LIFE].getTextureId());
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
			if (curGame.getBombCount() > 0) {
				bCount = " "+curGame.getBombCount();
				
				gl.glPushMatrix();
				gl.glTranslated(0.9*curAspect,0.8,0);
				gl.glScalef(0.1f, 0.1f, 1);
				gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_BOMB].getTextureId());
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
			String multi = "x"+curGame.getMultiplier();
			gl.glPushMatrix();
			gl.glTranslated(0,0.9,0);
			gl.glScalef(0.0005f, 0.0005f, 1); //for some reason it starts very big (152 or something)
	
			for (int i = 0; i < multi.length(); i++) {
				char ch = multi.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
		
		//kills
			String kills = "  "+curGame.getKills();
			
			gl.glPushMatrix();
			gl.glTranslated(0.8 *curAspect,0.7,0);
			gl.glScalef(0.0004f, 0.0004f, 1); //for some reason it starts very big (152 or something)
	
			for (int i = 0; i < kills.length(); i++) {
				char ch = kills.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
			
			String tKills = "  "+curGame.getTotalKills();
			gl.glPushMatrix();
			gl.glTranslated(0.8*curAspect,0.65,0);
			gl.glScalef(0.0004f, 0.0004f, 1);
			
			for (int i = 0; i < tKills.length(); i++) {
				char ch = tKills.charAt(i);
				glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
			}
			gl.glPopMatrix();
			
			
		//time
			int a = (int)curGame.getTime();
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
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}


	@Override
	public void dispose(GLAutoDrawable drawable) {
		//called by system when the window is closed

//		System.out.println(GameEngine.curGame.toString());
		//TODO possible double up between lostLife and this when closing the game
		
		LeaderBoard.writeScore(curGame.getDifficulty(), curGame.getScore(), "auto_"+curSettings.getName(), (int)curGame.getTime());
		
		LeaderBoard.addToStats(curGame);
		
		System.out.println(LeaderBoard.getStats() + "\t from dispose(), GameEngine");
	}
	
	
	///////////////////////////// STATIC things
	
	/**Kill all objects on the screen (excluding Player,Border,Camera,Powerups,ROOT)
	 * @param object Optional game object that will not be deleted after method
	 */
	public static void killAll(GameObject object, boolean particles) {
		LinkedList<GameObject> allList = new LinkedList<GameObject>(GameObject.ALL_OBJECTS);
		for (GameObject obj: allList) {
			if (obj instanceof Player || obj instanceof Border || obj instanceof Camera || obj instanceof PowerUp || obj.equals(GameObject.ROOT)) {
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
		
		if (particles) {
			//particles of the death screen (was at 100 but caused lag, now 25) [TODO particle count setting]
			for (int i = 0; i < 25; i++) {
				for (int j = 0; j < 20; j++) {
					MovingObject p = new Particle(2, GameEngine.WHITE, 0.7, Particle.DEFAULT_DRAG);
					p.x = playerPos[0];
					p.y = playerPos[1];
					p.dx = GameEngine.rand.nextDouble()*Math.cos(360*i/20)*50;
					p.dy = GameEngine.rand.nextDouble()*Math.sin(360*i/20)*50;
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
		if (killCountdown > 0) {
			return false;
		}
		
		return true;
	}
	
	
	/** Called when a life is lost
	 * @param obj
	 */
	public static void lostLife(GameObject obj) { 
		GameEngine.killAll(obj, true); //delete everything, then add the object later
		
		killCountdown = GameEngine.KILL_SCREEN_TIME;
		killObj = obj;
		
		if (curGame.getLives() < 1) { //no lives left
			TheGame.reloadMenu(curGame, curSettings.getName());
		} //TODO must stop itself (somehow, maybe through a "pause" like thing)
	}
	
	public static void togglePause() {
		if (GameEngine.killCountdown > 0) {
			return;
		}
		isPaused = !isPaused;
		
		TextPopup text = new TextPopup(WHITE, "Paused", 0.1, playerPos[0]-0.8, playerPos[1]+1.2);
		text.angle = 0;
	}
	
}
