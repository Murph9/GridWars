import java.awt.Dimension;
import java.awt.Toolkit;
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

//	//Textures
	public static MyTexture[] textures;
	public static final int TEXTURE_SIZE = 35; //space for more (and order space)
	public static final int /**/
			SHY = 0, SPINNER = 1, DIAMOND = 2, SQUARE = 3, TRIANGLE = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,  
			SNAKEBODY = 8, BUTTERFLY = 9, SEEKER = 10, BLACKHOLE = 11,
							/**/
			PLAYER = 21, BULLET = 22, EXTRA_SPEED = 23, TEMP_SHIELD = 24, EXTRA_BOMB = 25, 
			EXTRA_LIFE = 26, BOUNCY_SHOT = 27, SUPER_SHOT = 28, REAR_SHOT = 29, SIDE_SHOT = 30, EXTRA_BULLET = 31;
	
	////Colours
	public static final double[] WHITE = {1,1,1,0.5}, RED = {1,0,0,0.5}, LIGHT_BLUE = {0,1,0.8,0.5}, GREEN = {0,1,0,0.5},
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, LIGHT_YELLOW = {1,1,0.2,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, BLUE = {0.2,0.2,1,0.5}, 
			ORANGE = {1,0.6,0,0.5}, REALLY_LIGHT_BLUE = {0,1,0.9,0.5};
	
	
	public static int viewHeight;
	public static int viewWidth;
	
	public static final Random rand = new Random();
			//just because ease of use, seeds could be used for specific game spawns (using seeds maybe?)
	
	public static GameState curGame;
	public static int boardWidth = 12, boardHeight = 10; //just incase something goes bad
	public static double scale;
	
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, 
							mousePos = new double[]{0,0}; //updated each 'update()' for speed of access, all methods use this
	
	public static Camera myCamera;
	
	private long myTime;
	private long startTime;
	
	private ShaderControl shader;
	
	public GameEngine(Camera camera, int width, int height, double scale) {
		startTime = System.currentTimeMillis();
		myCamera = camera;
		curGame = new GameState();
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
		textures[SEEKER] = new MyTexture(gl, dir + "circle.png");
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
		
		gl.glPushMatrix();
		
		//Draw the score text
		GLUT glut = new GLUT();
		gl.glTranslated(-2, scale - 0.5 + myCamera.y,0);
		gl.glColor3d(1,1,1);
		gl.glScalef(0.004f, 0.004f, 0.004f); //for some reason it starts very big (152 or something)
		String score = "S: " + curGame.getScore()+" | L: "+curGame.getLives()+ " | B: " + curGame.getBombCount() + //
					" | x"+curGame.getMultiplier() +" | Time:  "+(myTime-startTime)/1000 + " | Kills: " + curGame.getKills();
		gl.glLineWidth(2f);
		for (int i = 0; i < score.length(); i++) {
			char ch = score.charAt(i);
			glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
		}
		gl.glPopMatrix();
		
		Mouse.theMouse.update(gl);
		playerPos = player.getPosition();
		mousePos = Mouse.theMouse.getPosition();
		
		GameObject.ROOT.draw(gl);
		
		shader.dontUseShader(gl);
		
		//finding the screen resolution (of the computer im on)
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)screenSize.getWidth();
		int height = (int)screenSize.getHeight();
		System.out.println(width + " " + height);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// tell the camera and the mouse that the screen has reshaped
		GL2 gl = drawable.getGL().getGL2();
		
		myCamera.reshape(gl, x, y, width, height);
		
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
//			dt = 0; //yeah not too sure
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
	
	@Override
	public void dispose(GLAutoDrawable drawable) { }
}
