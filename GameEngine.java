import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
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
	private	int score;
	private	int lives;
	private int multiplier;
	
	public static int boardWidth = 12, 
				boardHeight = 10; //at least 4 please
	
	public static Player player;
	private static double[] playerPos = new double[]{0,0}, mousePos = new double[]{0,0}; //updated each 'update()' for speed
	
	public static MyTexture[] textures;
	public static final String EXTENSION = "png";
	public static final int TEXTURE_SIZE = 15; //space for more
	public static final int PLAYER = 0, DOT = 1, DIAMOND = 2, SQUARE = 3, BULLET = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,
				SNAKEBODY = 8, BUTTERFLY = 9, CIRCLE = 10;
	
	public static final String  PLAYER_IMG = "player.png", DOT_IMG = "dot.png", DIAMOND_IMG = "diamond.png",
	SQUARE_IMG = "square.png",  BULLET_IMG = "bullet.png", CLONE_IMG = "clone.png", SHIELD_IMG = "shield.png",
	SNAKEHEAD_IMG = "snakeHead.png", SNAKEBODY_IMG = "snakeBody.png", BUTTERFLY_IMG = "butterfly.png", CIRCLE_IMG = "circle.png";
	
	public static final double[] WHITE = {1,1,1,0.5}, RED = {1,0,0,0.5}, LIGHT_BLUE = {0,1,0.8,0.5}, GREEN = {0,1,0,0.5},
			PURPLE = {1,0,1,0.5}, YELLOW = {1,1,0,0.5}, BROWN = {0.8, 0.3, 0.2,0.5}, BLUE = {0,0,1,0.5}, ORANGE = {1,0.6,0,0.5},
			REALLY_LIGHT_BLUE = {0,1,0.9,0.5};
	
	public GameEngine(Camera camera, int width, int height) {
		startTime = System.currentTimeMillis();
		myCamera = camera;
		score = 0;
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
		textures[DOT] = new MyTexture(gl, DOT_IMG, EXTENSION);
		textures[DIAMOND] = new MyTexture(gl, DIAMOND_IMG, EXTENSION);
		textures[SQUARE] = new MyTexture(gl, SQUARE_IMG, EXTENSION);
		textures[BULLET] = new MyTexture(gl, BULLET_IMG, EXTENSION);
		textures[SHIELD] = new MyTexture(gl, SHIELD_IMG, EXTENSION);
		textures[SNAKEBODY] = new MyTexture(gl, SNAKEBODY_IMG, EXTENSION);
		textures[SNAKEHEAD] = new MyTexture(gl, SNAKEHEAD_IMG, EXTENSION);
		textures[BUTTERFLY] = new MyTexture(gl, BUTTERFLY_IMG, EXTENSION);
		textures[CIRCLE] = new MyTexture(gl, CIRCLE_IMG, EXTENSION);
		
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
        
        calcCollisions();
	}
	
	/**checks relative distances of everything then calls @calcDeletions to actually delete them
	 */
	private void calcCollisions() {
//		System.out.print(GameObject.ALL_OBJECTS.size()+ ",");
		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
	
		HashSet<GameObject> obj2Del = new HashSet<GameObject>(); //because constant check time
		
		loop1: for (GameObject oA: objects) { //for each bullet
			if (obj2Del.contains(oA)) {
				continue;
			}

			if (oA instanceof PlayerBullet || oA instanceof Player) { 
							//if a bullet, check that you hit something
							//if player, check if you lose a life
				
				for (GameObject oB: objects) { //look at everything that could hit you (or get hit by bullet)
					if (obj2Del.contains(oB)) {
						continue;
					}
					
					//list of things you can't even hit with a bullet (but might have a position)
					if (oB instanceof PlayerBullet || oB instanceof Player || oB instanceof Border
							|| oB instanceof Camera || oB.equals(GameObject.ROOT)) {
						continue; //not these, next object please
						//and please don't delete root
					}
					
					double[] pos1 = oA.getCollisionPosition(); //make sure this one is called
					double sizeA = (oA).getSize();
					double[] pos2 = oB.getCollisionPosition(); //seriously
					double sizeB = (oB).getSize();
					
					if ((Math.abs(pos1[0] - pos2[0]) < sizeA/3 + sizeB/3) && //if closeish
							(Math.abs(pos1[1] - pos2[1]) < sizeA/3 + sizeB/3)) {
						obj2Del.add(oA);
						obj2Del.add(oB);
						continue loop1; //don't think about this loop again = yay
					}
				}
			}
			
		}
		
		//if player got hit do stuff
		if (calcDeletions(obj2Del)) {
			//delete everything from the GameObjects.ROOT
			for (GameObject o: objects) {
				if (o instanceof SplitingSquare) {
					SplitingSquare sq = (SplitingSquare) o;
					sq.setSplitStatus();
				}
				if (!(o instanceof Border || o instanceof Player || o instanceof Camera || o.equals(GameObject.ROOT))) {
					o.destroy();
				}
			}
			lives -= 1;
		}
//		System.out.print(obj2Del.size());
//		System.out.println(","+GameObject.ALL_OBJECTS.size());
	}

	/**Return true if Player has collided with something
	 * Handles all the logic behind what happens when collisions occur
	 * replaced the old optionsForDelete
	 * @param objects List of things to try and delete
	 */
	private boolean calcDeletions(HashSet<GameObject> objects) {
		
		for (GameObject o: objects) {
			if (o instanceof Player) {
				return true; //then exit because we are done here
			}
			if (o instanceof Shield || o instanceof SnakeBody) {
				//NOTHING, because they don't die
				
			} else {
				MovingObject mo = (MovingObject) o;
				score += mo.score(); //could definately be: o.getScore()
				o.destroy();
			}
		}
		return false;
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) { }
}
