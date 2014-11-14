import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.awt.TextRenderer;

//does all the back-end logic..
//multiplier works of this many kills [25,50,100,200,400,800,1600,3200,6400,12800]
public class GameEngine implements GLEventListener {

//	//Textures
	public static MyTexture[] textures;
	public static final int TEXTURE_SIZE = 25; //space for more
	public static final int PLAYER = 0, SPINNER = 1, DIAMOND = 2, SQUARE = 3, BULLET = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,
				SNAKEBODY = 8, BUTTERFLY = 9, CIRCLE = 10, SHY = 11, EXTRA_BULLET = 12, EXTRA_SPEED = 13, TEMP_SHIELD = 14, EXTRA_BOMB =15,
				EXTRA_LIFE = 16, BOUNCY_SHOT = 17, SUPER_SHOT = 18, REAR_SHOT = 19, SIDE_SHOT = 20, TRIANGLE = 21;
	
	////Colours
	public static final double[] WHITE = {1,1,1,0.5}, RED = {1,0,0,0.5}, LIGHT_BLUE = {0,1,0.8,0.5}, GREEN = {0,1,0,0.5},
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, LIGHT_YELLOW = {1,1,0.2,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, BLUE = {0.2,0.2,1,0.5}, 
			ORANGE = {1,0.6,0,0.5}, REALLY_LIGHT_BLUE = {0,1,0.9,0.5};
	
	
	public static TextRenderer renderer;
	public static int viewHeight;
	public static int viewWidth;
	
	public static final Random rand = new Random(); 
			//just because ease of use, seeds could be used for specific game spawns (maybe?)
	
	public static GameState curGame;
	public static int boardWidth = 12, boardHeight = 10; //at least 4 please
	
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, mousePos = new double[]{0,0}; //updated each 'update()' for speed of access
	
	private Camera myCamera;
	private long myTime;
	private long startTime;
	
	public GameEngine(Camera camera, int width, int height) {
		startTime = System.currentTimeMillis();
		myCamera = camera;
		curGame = new GameState();
		boardWidth = width;
		boardHeight = height;
	}
	
	public static double[] getPlayerPos() { return playerPos; }
	public static double[] getMousePos() {  return mousePos;  }
	public static GameObject getPlayer() {  return player;    }
	

	@Override
	public void init(GLAutoDrawable drawable) {
		myTime = System.currentTimeMillis();
		GL2 gl = drawable.getGL().getGL2();
		
		textures = new MyTexture[TEXTURE_SIZE];
		textures[PLAYER] = new MyTexture(gl, "player.png");
		textures[SPINNER] = new MyTexture(gl, "spinner.png");
		textures[DIAMOND] = new MyTexture(gl, "diamond.png");
		textures[SQUARE] = new MyTexture(gl, "square.png");
		textures[BULLET] = new MyTexture(gl, "bullet.png");
		textures[SHIELD] = new MyTexture(gl, "shield.png");
		textures[SNAKEBODY] = new MyTexture(gl, "snakeBody.png");
		textures[SNAKEHEAD] = new MyTexture(gl, "snakeHead.png");
		textures[BUTTERFLY] = new MyTexture(gl, "butterfly.png");
		textures[CIRCLE] = new MyTexture(gl, "circle.png");
		textures[SHY] = new MyTexture(gl, "shy.png");
		textures[EXTRA_BULLET] = new MyTexture(gl, "extraBullet.png");
		textures[EXTRA_SPEED] = new MyTexture(gl, "extraSpeed.png");
		textures[TEMP_SHIELD] = new MyTexture(gl, "tempShield.png");
		textures[EXTRA_BOMB] = new MyTexture(gl, "extraBomb.png");
		textures[EXTRA_LIFE] = new MyTexture(gl, "extraLife.png");
		textures[BOUNCY_SHOT] = new MyTexture(gl, "bouncyShot.png");
		textures[SUPER_SHOT] = new MyTexture(gl, "superShot.png");
		textures[REAR_SHOT] = new MyTexture(gl, "rearShot.png");
		textures[SIDE_SHOT] = new MyTexture(gl, "sideShot.png");
		textures[TRIANGLE] = new MyTexture(gl, "triangle.png");
		
		GameEngine.renderer = new TextRenderer(new Font("Courier", Font.BOLD, 22), true);
		GameEngine.viewHeight = drawable.getHeight();
		GameEngine.viewWidth  = drawable.getWidth();
		
		playerPos = player.getPosition();
        mousePos = Mouse.theMouse.getPosition();
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT); //Set wrap mode for texture in S direction
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);//Set wrap mode for texture in T direction
		
		gl.glEnable(GL2.GL_BLEND); //alpha blending (you know transparency)
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA); //special blending
	}

	
	@Override
	public void display(GLAutoDrawable drawable) {
		GameEngine.viewHeight = drawable.getHeight();
		GameEngine.viewWidth  = drawable.getWidth();
		
		update();

		GL2 gl = drawable.getGL().getGL2();

		// set the view matrix based on the camera position
		myCamera.setView(gl);
		
		Mouse.theMouse.update(gl);
		playerPos = player.getPosition();
		mousePos = Mouse.theMouse.getPosition();
		
		GameObject.ROOT.draw(gl);
		
		//Draw the score text
		GameEngine.renderer.beginRendering(viewWidth, viewHeight, true);
		GameEngine.renderer.setColor(0.3f, 0.7f, 1.0f, 0.8f);
		GameEngine.renderer.draw("S:" + curGame.getScore()+" | L:"+curGame.getLives()+ " | B: " + curGame.getBombCount() + //
				" | x"+curGame.getMultiplier() +" | Time:  "+(myTime-startTime)/1000, 10, viewHeight-22);
		GameEngine.renderer.endRendering();
		
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
        
        // update all objects (and deleting when necessary)
        for (GameObject g: objects) {
            g.update(dt);
        }

        curGame.update(dt); //to count down the timer for the powerups
	}
	
	public static void killAll() {
		LinkedList<GameObject> n = new LinkedList<GameObject>(GameObject.ALL_OBJECTS);
		for (GameObject o: n) {
			if (o instanceof Player || o instanceof Border || o instanceof Camera || o instanceof PowerUp || o.equals(GameObject.ROOT)) {
			} else {
				GameObject.ALL_OBJECTS.remove(o);
			}
		}
		
		BlackHole.ALL_THIS.clear();
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
				MovingObject p = new Particle(2, GameEngine.WHITE, GameEngine.rand.nextDouble()*0.7 + 0.2);
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
