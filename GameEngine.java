import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

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
	public static Score score;
	private	int lives;
	private int multiplier;
	
	public static int boardWidth = 12, 
				boardHeight = 10; //at least 4 please
	
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, mousePos = new double[]{0,0}; //updated each 'update()' for speed of access
	
	////Textures
	public static MyTexture[] textures;
	public static final String EXTENSION = "png";
	public static final int TEXTURE_SIZE = 15; //space for more
	public static final int PLAYER = 0, SPINNER = 1, DIAMOND = 2, SQUARE = 3, BULLET = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,
				SNAKEBODY = 8, BUTTERFLY = 9, CIRCLE = 10, SHY = 11;
	
	////Images
	public static final String  PLAYER_IMG = "player.png", SPINNER_IMG = "spinner.png", DIAMOND_IMG = "diamond.png",
	SQUARE_IMG = "square.png",  BULLET_IMG = "bullet.png", CLONE_IMG = "clone.png", SHIELD_IMG = "shield.png",
	SNAKEHEAD_IMG = "snakeHead.png", SNAKEBODY_IMG = "snakeBody.png", BUTTERFLY_IMG = "butterfly.png", CIRCLE_IMG = "circle.png",
	SHY_IMG = "shy.png";
	
	////Colours
	public static final double[] WHITE = {1,1,1,0.5}, RED = {1,0,0,0.5}, LIGHT_BLUE = {0,1,0.8,0.5}, GREEN = {0,1,0,0.5},
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, BLUE = {0,0,1,0.5}, ORANGE = {1,0.6,0,0.5},
			REALLY_LIGHT_BLUE = {0,1,0.9,0.5};
	
	public GameEngine(Camera camera, int width, int height) {
		startTime = System.currentTimeMillis();
		myCamera = camera;
		score = new Score();
		lives = 3;
		multiplier = 1;
		boardWidth = width;
		boardHeight = height;
	}
	
	public static double[] getPlayerPos() {
		return playerPos;
	}
	public static double[] getMousePos() {
		return mousePos;
	}
	public static GameObject getPlayer() {
		return player;
	}
	
	
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
		renderer.draw("Score: "+score+"   Time:  "+(myTime-startTime)/1000+"   Lives:  "+lives+"   Multiplier:  "+multiplier, 10, drawable.getHeight()-22); //x,y = 10,10
		renderer.endRendering();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		myTime = System.currentTimeMillis();
		GL2 gl = drawable.getGL().getGL2();
		
		textures = new MyTexture[TEXTURE_SIZE]; 
		textures[PLAYER] = new MyTexture(gl, PLAYER_IMG, EXTENSION);
		textures[SPINNER] = new MyTexture(gl, SPINNER_IMG, EXTENSION);
		textures[DIAMOND] = new MyTexture(gl, DIAMOND_IMG, EXTENSION);
		textures[SQUARE] = new MyTexture(gl, SQUARE_IMG, EXTENSION);
		textures[BULLET] = new MyTexture(gl, BULLET_IMG, EXTENSION);
		textures[SHIELD] = new MyTexture(gl, SHIELD_IMG, EXTENSION);
		textures[SNAKEBODY] = new MyTexture(gl, SNAKEBODY_IMG, EXTENSION);
		textures[SNAKEHEAD] = new MyTexture(gl, SNAKEHEAD_IMG, EXTENSION);
		textures[BUTTERFLY] = new MyTexture(gl, BUTTERFLY_IMG, EXTENSION);
		textures[CIRCLE] = new MyTexture(gl, CIRCLE_IMG, EXTENSION);
		textures[SHY] = new MyTexture(gl, SHY_IMG, EXTENSION);
		
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
        
        //calcCollisions(); this was a function, but now every class handles it itself see 'update()'
	}

	@Override
	public void dispose(GLAutoDrawable drawable) { }


	//nice small class so the other functions can call it
	
	class Score {
		private int score;
		
		public void addScore(int add) {
			if (add >= 0) { //say yes
				score += add;
			}
			
		}
		public int getScore() {
			return score;
		}
	}
}
