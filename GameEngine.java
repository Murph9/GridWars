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
        
        bulletCollision();
	}
	
	//calculates collision with bullets only (sounds easier so far, my merge with player in future)
	private void bulletCollision() {
		List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
	
		for (GameObject obj1: objects) { //for each bullet
			if (obj1.hasCollided) { //collided last call
				if (optionsForDelete(objects, obj1)) {
					break; //this occurs if player has been hit, then stop this big loop
				}
			}
			
			if (obj1 instanceof PlayerBullet || obj1 instanceof Player) { 
							//if a bullet, check that you hit something
							//if player, check if you lose a life
				
				for (GameObject obj2: objects) { //look at everything that could hit you (or get hit by bullet)
					
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
						obj1.hasCollided = true;
//						optionsForDelete(objects, obj1);
						obj2.hasCollided = true;
//						optionsForDelete(objects, obj2);
						break;
					}
				}
			}
		}
	}
	

	/**Return true if Player has collided with something
	 * Handles all the logic behind what happens when collisions occur
	 */
	private boolean optionsForDelete(List<GameObject> objects, GameObject obj1) {
		if (obj1 instanceof Player) {
			obj1.hasCollided = false;
			for (GameObject obj2: objects) {
				if (obj2 instanceof Player || obj2 instanceof Border || obj2 instanceof Camera || obj2.equals(GameObject.ROOT)) {
				} else { //all things we need for the next run
//					obj2.destroy();
					//DEBUG: comment out above line if you want to test, player can't be 'killed'
				}
			}
			lives -= 1;
			return true; //stop, there should be no more collisions, because i just deleted it all
			
		} else if (obj1 instanceof PlayerBullet) { //we don't want to add points for just shooting now do we?
			obj1.destroy(); //so kill it
			
		} else if (obj1 instanceof SplitingSquare && obj1.getSize() == 1) { //special case for the square
			SplitingSquare square = (SplitingSquare) obj1;
			double[] t = square.getCollisionPosition();
			double angle = square.getOrbitAngle();
			
			SplitingSquare sA = new SplitingSquare(0.75, TheGame.RED, angle, 0.7, false);
			sA.setPosition(new double[] {t[0]+Math.cos(angle+60)*0.7, t[1]+Math.sin(angle+60)*0.7});
			
			SplitingSquare sB = new SplitingSquare(0.75, TheGame.RED, angle, 0.7, true);
			sB.setPosition(new double[] {t[0]+Math.cos(angle-30)*0.7, t[1]+Math.sin(angle-30)*0.7});
			
			SplitingSquare sC = new SplitingSquare(0.75, TheGame.RED, angle, 0.7, true);
			sC.setPosition(new double[] {t[0]+Math.cos(angle-120)*0.7, t[1]+Math.sin(angle-120)*0.7});
			
			score += 1;
			obj1.destroy();
		} else if (obj1 instanceof Shield || obj1 instanceof SnakeBody) {
			//do nothing because these can NOT be destroyed
			obj1.hasCollided = false; //and to stop further computations
		} else {
			score += 1;
			obj1.destroy();
		}
		return false;
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) { }
}
