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
	
	public static MyTexture[] textures;
	public static final String EXTENSION = "png";
	public static final int PLAYER = 0, DOT = 1,DIAMOND = 2,SQUARE = 3, BULLET = 4, CLONE = 5, SHIELD = 6, SNAKEHEAD = 7,
				SNAKEBODY = 8;
	
	public static final String  PLAYER_IMG = "player.png", DOT_IMG = "dot.png", DIAMOND_IMG = "diamond.png",
	SQUARE_IMG = "square.png",  BULLET_IMG = "bullet.png", CLONE_IMG = "clone.png", SHIELD_IMG = "shield.png",
	SNAKEHEAD_IMG = "snakeHead.png", SNAKEBODY_IMG = "snakeBody.png";
	
	
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
		return player.getPosition();
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
		
		textures = new MyTexture[10]; //space for more
		textures[PLAYER] = new MyTexture(gl, PLAYER_IMG, EXTENSION);
		textures[DOT] = new MyTexture(gl, DOT_IMG, EXTENSION);
		textures[DIAMOND] = new MyTexture(gl, DIAMOND_IMG, EXTENSION);
		textures[SQUARE] = new MyTexture(gl, SQUARE_IMG, EXTENSION);
		textures[BULLET] = new MyTexture(gl, BULLET_IMG, EXTENSION);
		textures[SHIELD] = new MyTexture(gl, SHIELD_IMG, EXTENSION);
		textures[SNAKEBODY] = new MyTexture(gl, SNAKEBODY_IMG, EXTENSION);
		textures[SNAKEHEAD] = new MyTexture(gl, SNAKEHEAD_IMG, EXTENSION);
		
		renderer = new TextRenderer(new Font("Courier", Font.BOLD, 22), true);
		
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
		List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
	
		HashSet<GameObject> obj2Del = new HashSet<GameObject>(); //because constant check time
		
		for (GameObject obj1: objects) { //for each bullet
			if (obj2Del.contains(obj1)) {
				continue;
			}
			
			if (obj1 instanceof PlayerBullet || obj1 instanceof Player) { 
							//if a bullet, check that you hit something
							//if player, check if you lose a life
				
				for (GameObject obj2: objects) { //look at everything that could hit you (or get hit by bullet)
					if (obj2Del.contains(obj2)) {
						continue;
					}
					
					//list of things you can't even hit with a bullet (but might have a position)
					if (obj2 instanceof PlayerBullet || obj2 instanceof Player || obj2 instanceof Border 
							|| obj2 instanceof Camera || obj2.equals(GameObject.ROOT)) {
						continue; //not these, next object please
						//and please don't delete root
					}
					
					double[] pos1 = obj1.getCollisionPosition(); //make sure this one is called
					double size1 = (obj1).getSize();
					double[] pos2 = obj2.getCollisionPosition(); //seriously
					double size2 = (obj2).getSize();
					
					if ((Math.abs(pos1[0] - pos2[0]) < Math.abs(size1/3 + size2/3 )) && //if closeish
							(Math.abs(pos1[1] - pos2[1]) < Math.abs(size1/3 + size2/3))) {
						obj2Del.add(obj1);
						obj2Del.add(obj1);
						break;
					}
				}
			}
			
		}
		
		
		if (calcDeletions(obj2Del)) {
			//delete everything from the GameObjects.ROOT
			lives -= 1;
		}
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
			if (o.hasCollided == true) { //bullets call this on themselves if they hit a wall
				o.destroy();
			}
			if (o instanceof SplitingSquare && o.getSize() == 1) { //something about changing this to a variable
				SplitingSquare square = (SplitingSquare) o; //because it has to be to get in here
				double[] t = square.getCollisionPosition();
				double angle = square.getOrbitAngle();
				
				SplitingSquare sA = new SplitingSquare(0.75, TheGame.RED, angle, 0.7, false);
				sA.setPosition(new double[] {t[0]+Math.cos(angle+60)*0.7, t[1]+Math.sin(angle+60)*0.7});
				
				SplitingSquare sB = new SplitingSquare(0.75, TheGame.RED, angle, 0.7, true);
				sB.setPosition(new double[] {t[0]+Math.cos(angle-30)*0.7, t[1]+Math.sin(angle-30)*0.7});
				
				SplitingSquare sC = new SplitingSquare(0.75, TheGame.RED, angle, 0.7, true);
				sC.setPosition(new double[] {t[0]+Math.cos(angle-120)*0.7, t[1]+Math.sin(angle-120)*0.7});
				
				score += 1;
				o.destroy();
			} else if (o instanceof Shield || o instanceof SnakeBody) {
				//NOTHING, because they don't die
			} else {
				score += 1; //could definately be: o.getScore()
				o.destroy();
			}
			
		}
		return false;
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) { }
}
