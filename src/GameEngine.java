import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.gl2.GLUT;

//does all the back-end logic..
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
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, LIGHT_YELLOW = {1,1,0.2,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, BLUE = {0.2,0.2,1,0.5}, 
			ORANGE = {1,0.6,0,0.5}, REALLY_LIGHT_BLUE = {0,1,0.9,0.5};
	
	////State values
	public static int viewHeight;
	public static int viewWidth;
	
	public static final Random rand = new Random();
			//just because ease of use, seeds could be used for specific game spawns (using seeds maybe?)
	
	public static GameState curGame;
	public static int boardWidth = 12, boardHeight = 10; //just incase something goes bad
	public static double scale;
	private double curAspect;
	
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, mousePos = new double[]{0,0}; 
				//updated each 'update()' for speed of access, all methods use this
	
	public static Camera myCamera;
	
	private long myTime;
	private long startTime;
	
	private ShaderControl shader; //might have an option for this later
	
	public GameEngine(int width, int height, double scale, int record) {
		Border border = new Border(width, height);
		border.setSize(1); //just incase it stopped being 1
		
		startTime = System.currentTimeMillis();
		myCamera = new Camera();
		myCamera.setSize(scale);
		
		GameEngine.player = new Player(1, GameEngine.WHITE);
		
		curGame = new GameState(record);
		boardWidth = width;
		boardHeight = height;
		GameEngine.scale = scale;
	}
	
	public static double[] getPlayerPos(){  return playerPos; }
	public static double[] getMousePos() {  return mousePos;  }
	public static GameObject getPlayer() {  return player;    }
	

	/**Initalises what is needed for the base game to work, (no actual game)
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		myTime = System.currentTimeMillis();
		GL2 gl = drawable.getGL().getGL2();
		
		String dir = "images/"; //because everything got moved :/
		
		textures = new MyTexture[TEXTURE_SIZE]; //this could be one line i guess.. (but the static indexes might be confusing)
		textures[PLAYER] = new MyTexture(gl, dir + "player.png");
		textures[SPINNER] = new MyTexture(gl, dir + "spinner.png");
		textures[DIAMOND] = new MyTexture(gl, dir + "diamond.png");
		textures[SQUARE] = new MyTexture(gl, dir + "square.png");
		textures[BULLET] = new MyTexture(gl, dir + "bullet.png");
		textures[SHIELD] = new MyTexture(gl, dir + "shield.png");
		textures[SNAKEBODY] = new MyTexture(gl, dir + "snakeBody.png");
		textures[SNAKEHEAD] = new MyTexture(gl, dir + "snakeHead.png");
		textures[BUTTERFLY] = new MyTexture(gl, dir + "butterfly.png");
		textures[SEEKER] = new MyTexture(gl, dir + "seeker.png");
		textures[SHY] = new MyTexture(gl, dir + "shy.png");
		textures[BLACKHOLE] = new MyTexture(gl, dir + "circle.png");

		textures[EXTRA_BULLET] = new MyTexture(gl, dir + "extraBullet.png");
		textures[EXTRA_SPEED] = new MyTexture(gl, dir + "extraSpeed.png");
		textures[TEMP_SHIELD] = new MyTexture(gl, dir + "tempShield.png");
		textures[EXTRA_BOMB] = new MyTexture(gl, dir + "extraBomb.png");
		textures[EXTRA_LIFE] = new MyTexture(gl, dir + "extraLife.png");
		textures[BOUNCY_SHOT] = new MyTexture(gl, dir + "bouncyShot.png");
		textures[SUPER_SHOT] = new MyTexture(gl, dir + "superShot.png");
		textures[REAR_SHOT] = new MyTexture(gl, dir + "rearShot.png");
		textures[SIDE_SHOT] = new MyTexture(gl, dir + "sideShot.png");
		textures[TRIANGLE] = new MyTexture(gl, dir + "triangle.png");
		
		playerPos = player.getPosition();
        mousePos = Mouse.theMouse.getPosition();
		
        //enable textures
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP); //Set wrap mode for texture in S direction
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);//Set wrap mode for texture in T direction
		
		gl.glEnable(GL2.GL_BLEND); //alpha blending (you know transparency)
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA); //special blending
		
		
		// create a new shader object that we can reference later to activate it.
		shader = new ShaderControl();
		shader.fsrc = shader.loadShader(dir + "f.txt"); // fragment GLSL Code
		shader.vsrc = shader.loadShader(dir + "v.txt"); // vertex GLSL Code
		shader.init(gl);
		
		//init sounds
		SoundEffect.init();
	}

	
	@Override
	public void display(GLAutoDrawable drawable) {
		GameEngine.viewHeight = drawable.getHeight();
		GameEngine.viewWidth  = drawable.getWidth();
		update();

		GL2 gl = drawable.getGL().getGL2();
		
		shader.useShader(gl);
		
		// set the view matrix based on the camera position
		myCamera.setView(gl);
		
		Mouse.theMouse.update(gl);
		playerPos = player.getPosition();
		mousePos = Mouse.theMouse.getPosition();
		
		shader.useShader(gl);
			GameObject.ROOT.draw(gl);
//		shader.dontUseShader(gl); //causes issues in the UI if this is used (although it should be)
		
		gl.glPushMatrix();
		gl.glLoadIdentity();
		drawUI(gl);
		gl.glPopMatrix();
		
		//finding the screen resolution (could be useful later)
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		int width = (int)screenSize.getWidth();
//		int height = (int)screenSize.getHeight();
//		System.out.println(width + " " + height);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// tell the camera and the mouse that the screen has reshaped
		GL2 gl = drawable.getGL().getGL2();
		
		myCamera.reshape(gl, x, y, width, height);
		this.curAspect = (double)width / (double)height; //so UI is fine
		
		// this has to happen after myCamera.reshape() to use the new projection matrix
		Mouse.theMouse.reshape(gl);
	}

	private void update() {
		long time = System.currentTimeMillis();
		double dt = (time - myTime) / 1000.0;
		myTime = time;

		List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
		
		// update all objects (and deleting when necessary)
		for (GameObject g: objects) {
			g.update(dt);
		}

		curGame.update(dt); //to count down the timer for the powerups
		
		if (GameEngine.curGame.getLives() < 0) {
//			dt = 0; //yeah not too sure yet
		}
	}
	
	public static void killAll() {
		LinkedList<GameObject> n = new LinkedList<GameObject>(GameObject.ALL_OBJECTS);
		for (GameObject o: n) {
			if (o instanceof Player || o instanceof Border || o instanceof Camera || o instanceof PowerUp || o.equals(GameObject.ROOT)) {
			} else {
				GameObject.ALL_OBJECTS.remove(o);
			}
		}
		
		BlackHole.ALL_THIS.clear(); //calling delete() on each object would be weird
		ConnectedTriangle.ALL_THIS.clear();
		HomingButterfly.ALL_THIS.clear();
		HomingDiamond.ALL_THIS.clear();
		PlayerBullet.ALL_THIS.clear();
		ShieldedClone.ALL_THIS.clear();
		ShySquare.ALL_THIS.clear();
		SimpleSpinner.ALL_THIS.clear();
		SplitingSquare.ALL_THIS.clear();
		
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 20; j++) {
				MovingObject p = new Particle(2, GameEngine.WHITE, 0.7);
				p.x = playerPos[0]+Math.cos(360*j/20);
				p.y = playerPos[1]+Math.sin(360*j/20);
				p.dx = GameEngine.rand.nextDouble()*Math.cos(360*i/20)*50;
				p.dy = GameEngine.rand.nextDouble()*Math.sin(360*i/20)*50;
			}
		}
	}

	//Draws the score text and UI
	private void drawUI(GL2 gl) {
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
			gl.glTranslated(-0.98*curAspect,0.8,0);
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
			int a = (int)(myTime-startTime)/100;
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
	public void dispose(GLAutoDrawable drawable) { }
}
