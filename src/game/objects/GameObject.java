
package game.objects;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.media.opengl.GL2;

import game.logic.*;

//see README.me
public class GameObject {

	public final static LinkedList<GameObject> ALL_OBJECTS = new LinkedList<GameObject>();
	
	// the root of the scene tree
	public final static GameObject ROOT = new GameObject();
	
	public double angle;
	public double size;
	public double x;
	public double y;
	
	protected double[] colour;
	protected int score;
	
	public GameObject() { 
		size = 1;
	    	//note all others are 0
		
	    this.colour = new double[]{1,1,1,0.5}; //just in case don't want no 'null' confusing things [yes poor english]
    	ALL_OBJECTS.add(this);
	}
    
	public double getX() { return x; }
	public double getY() { return y; }
	public double getRotation() { return angle; }
    public double getSize() { return size; }
    public double[] getPosition() { return new double[]{x, y}; }
    public double[] getColour() {
    	return new double[]{colour[0], colour[1], colour[2]};
    }
    
    /**Useful for functions that move differently to their position
     * Change if the objects' location is different to x, y
     * This is the method called by the selfCol()'s
     * @return double[2]{x, y}
     */
    public double[] getCollisionPosition() {
    	return getPosition();
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
        	GameEngine.curGame.addKill();
        	GameEngine.curGame.addScore(score);
        	
        	ScorePopup s = new ScorePopup(this.colour, score, 1, x-(size/2), y-0.1);
        	s.angle = 0; //because annoying warning now that ive used it
        }
    }
    
    ///////////////////////////////////////////////
    //things to use in sub classes:
    public void drawSelf(GL2 gl) {
        // should be called after binding the texture
    	
    	gl.glColor4d(colour[0], colour[1], colour[2], colour[3]);
    	Helper.square(gl);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
    public void update(double dt) {
    	// do nothing
    }
    
    
    /**Move the matrix, then draw the object and all of its descendants recursively.
     * @param gl I don't think you should be using this code and NOT know what this is for
     */
    public void draw(GL2 gl) {
        gl.glPushMatrix(); //remember it ....
        
        //transform to position, draw, then call on children
        gl.glTranslated(this.x, this.y, 0);
        gl.glRotated(this.angle, 0, 0, 1); //because 2D, everything rotates about the z axis
        gl.glScaled(this.size, this.size, 1);
        
        drawSelf(gl);
        
        if (this.equals(GameObject.ROOT)) { // the root
        	ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
        	for (GameObject o: objects) { 	// then draws everything
        		if (!o.equals(GameObject.ROOT)) //doesn't draw itself because thats called recursive
        			o.draw(gl);
        	}
        }
        gl.glPopMatrix(); //.... then put it back 
    }
}
