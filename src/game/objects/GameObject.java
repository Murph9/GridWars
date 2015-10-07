
package game.objects;

import java.util.LinkedList;

import javax.media.opengl.GL2;

import game.logic.*;

//see README.me
public class GameObject {

	public final static LinkedList<GameObject> ALL_OBJECTS = new LinkedList<GameObject>();
	
	public double angle;
	public double size;
	public double x;
	public double y;

	public double spawnTimer;
	
	protected double[] colour;
	protected int score;
	
	public GameObject() {
		size = 1;
		spawnTimer = 0;
		
	    this.colour = new double[]{1,1,1,0.5}; //just in case don't want 'null' confusing things
    	ALL_OBJECTS.add(this);
	}
    
	public double getRotation() { return angle; } //required.. because of snake interface
    public double[] getPosition() { return new double[]{x, y}; }
    public double[] getColour() {
    	return new double[]{colour[0], colour[1], colour[2], colour[3]};
    }
    
    /**Useful for functions that move differently to their position (eg. SplitingSquare)
     * Change if the objects' location is different to x, y, for example if the object is spawning it hasn't got collision.
     * This is the method called by the selfCol()'s
     * @return double[2]{x, y}
     */
    public double[] getCollisionPosition() {
    	if (spawnTimer > 0) {
    		return new double[]{Double.MAX_VALUE, Double.MAX_VALUE}; 
    			//an object spawning has no collision, annoying when random spawn in used
    	} else {
    		return getPosition();
    	}
    }
    
	public void setRotation(double rot) {
        angle = rot;
    }
    public void setSize(double scale) {
        size = scale;
    }
    public void setPosition(double[] translate) {
    	x = translate[0];
    	y = translate[1];
    }
    public void setColour(double[] inColour) {
    	colour[0] = inColour[0];
    	colour[1] = inColour[1];
    	colour[2] = inColour[2];
    	colour[3] = inColour[3];
    }
    
    /**Handles removing the object and ifPoints adding to the score
     * @param ifPoints If score should be added
     */
    public void amHit(boolean ifPoints) {
        ALL_OBJECTS.remove(this);
        if (ifPoints) {
        	Engine.gameState.addKill();
        	Engine.gameState.addScore(score);
        	
        	@SuppressWarnings("unused")
        	TextPopup s = new TextPopup(this.colour, score, 1, x-(size/2), y-0.1);
        	
        	double x = Engine.getPlayerPos()[0];
        	double t = (this.x-x)/10; //seems to work well
        	
        	new SoundEffect(Engine.SHOT_HARD, 1, t).start();
        }
    }
    
    ///////////////////////////////////////////////
    //things to use in sub classes:
    //below is usually what the class will call after binding the texture...
    public void drawSelf(GL2 gl) {
    	//...here
    	gl.glColor4d(this.colour[0], this.colour[1], this.colour[2], this.colour[3]);
    	Helper.square(gl);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		
    }
    
    public void update(double dt) {
    	// root doesn't move or rotate
    }
    
    
    /**Move the matrix, then draw the object and all of its descendants recursively.
     * @param gl I don't think you should be using this code and NOT know what this is for
     */
    public void draw(GL2 gl) {
        gl.glPushMatrix(); //remember it ....
        
        //transform to position, draw, then call on children
        gl.glTranslated(x, y, 0);
        gl.glRotated(angle, 0, 0, 1); //because 2D, everything rotates about the z axis
        if (spawnTimer > 0) {
        	gl.glScaled(size/(1-spawnTimer), size/(1-spawnTimer), 1); //so it spawns in with an effect
        } else {
        	gl.glScaled(size, size, 1);
        }
        
        drawSelf(gl);
        //this used to have ROOT code, but it seems to have just been slowing things down?
        
        gl.glPopMatrix(); //.... then put it back
    }
}
