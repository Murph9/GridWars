import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;


//Types of GameObjects(1)
//(1)-> abstract Moving Object(2)
//(2)-> abstract Homing Object(3)

//lots of others...
public class GameObject {

	public final static List<GameObject> ALL_OBJECTS = new ArrayList<GameObject>();
	
	// the root of the scene tree
	public final static GameObject ROOT = new GameObject();
	
	private double myRotation;
	private double myScale;
	private double[] myTranslation;
	
	protected double[] colour;
	
	protected double size = 1;
	

	GameObject() {
		myRotation = 0;
		myScale = 1;
	    myTranslation = new double[2];
	    myTranslation[0] = 0;
	    myTranslation[1] = 0;
	    
	    this.colour = new double[]{1,1,1};
	    
    	ALL_OBJECTS.add(this);
    	
	}
    
	
	public double getRotation() {
        return myRotation;
    }
    public double getScale() {
        return myScale;
    }
    public double[] getPosition() {
        double[] t = new double[2];
        t[0] = myTranslation[0];
        t[1] = myTranslation[1];
        return t;
    }
    public double[] getColour() {
    	double[] t = new double[3];
    	t[0] = colour[0];
    	t[1] = colour[1];
    	t[2] = colour[2];
    	return t;
    }
    public double getSize() {
    	return size;
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
        myRotation = rot;
    }
    public void setScale(double scale) {
        myScale = scale;
    }
    public void setPosition(double[] translate) {
    	myTranslation[0] = translate[0];
    	myTranslation[1] = translate[1];
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
        gl.glTranslated(myTranslation[0], myTranslation[1], 0);
        gl.glRotated(this.myRotation, 0, 0, 1); //because 2D
        gl.glScaled(this.myScale, this.myScale, 1); //no shearing here thanks
        
        drawSelf(gl);
        
        if (this.equals(GameObject.ROOT)) { // the root
        	List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
        	for (GameObject o: objects) { 	// then draws everything
        		if (!o.equals(GameObject.ROOT))
        			o.draw(gl);
        	}
        }
        gl.glPopMatrix(); //.... then put it back 
    }
}
