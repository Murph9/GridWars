import java.util.ArrayList;
import java.util.LinkedList;

import javax.media.opengl.GL2;


//Types of GameObjects(1)
//(1)-> abstract Moving Object(2)
//(2)-> abstract Homing Object(3)

//lots of others...
public class GameObject {

	public final static LinkedList<GameObject> ALL_OBJECTS = new LinkedList<GameObject>();
	
	// the root of the scene tree
	public final static GameObject ROOT = new GameObject();
	
	protected double angle;
	protected double size;
	protected double x;
	protected double y;
	
	protected double[] colour;
	
	GameObject() {
		angle = 0;
		size = 1;
	    x = 0;
	    y = 0;
	    
	    this.colour = new double[]{1,1,1};
    	ALL_OBJECTS.add(this);
	}
    
	
	public double getRotation() {
        return angle;
    }
    public double getSize() {
        return size;
    }
    public double[] getPosition() {
        double[] t = new double[2];
        t[0] = x;
        t[1] = y;
        return t;
    }
    public double[] getColour() {
    	double[] t = new double[3];
    	t[0] = colour[0];
    	t[1] = colour[1];
    	t[2] = colour[2];
    	return t;
    }
    
    /**Useful for functions that move differently to their position
     * Change if the objects' location is different to myTranslation
     * This is the method called by GameEngine
     * @return
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
    }
    
    public void destroy() {
        ALL_OBJECTS.remove(this);
    }
    
    //things to use in sub classes:
    public void drawSelf(GL2 gl) {
        // do nothing
    }
    public void update(double dt) {
    	// do nothing
    }
    
    
    /**Draw the object and all of its descendants recursively.
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
        		if (!o.equals(GameObject.ROOT))
        			o.draw(gl);
        	}
        }
        gl.glPopMatrix(); //.... then put it back 
    }
}
