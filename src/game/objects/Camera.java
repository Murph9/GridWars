package game.objects;

import game.logic.Engine;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The camera is a GameObject that can be moved, rotated and scaled like any other.
 * 		 Implments the setView() and reshape() methods.
 * @author malcolmr
 */
public class Camera extends GameObject {

    private static final int GAME_SCALE_CONSTANT = 86; // for 1024/12 which is a good game scale
	private float[] myBackground;

    public Camera() {
        this.myBackground = new float[4];
    }
    
    public float[] getBackground() {
        return myBackground;
    }

    public void setBackground(float[] background) {
        myBackground = background;
    }
    
    public double[] getCollisionPosition() {
    	return new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
    }
    
    public void update (double dt) { //thought causing
    	double[] pos = Engine.getPlayerPos();
    	
    	double xdiff = this.x - pos[0];
    	double ydiff = this.y - pos[1];
    	this.x -= xdiff;//20; //thoughts about a slow moving camera should be squashed as it feels weird
    	this.y -= ydiff;//20;
    	
    	double pixelWidth = Engine.settings.getPixelHeight();
    	this.size = pixelWidth/GAME_SCALE_CONSTANT;
    	
    	double pixelAspect = pixelWidth/(double)(Engine.settings.getPixelHeight());
    	int height = Engine.settings.getBoardHeight() + 1;
    	int width = Engine.settings.getBoardWidth() + 1; //+1 is for buffer size of the game field
    	
    	if (this.y > (height - this.size)) {
    		this.y = (height - this.size); //want to keep the camera from this.size from the edge
    	}
    	if (this.y < (-height + this.size)) {
    		this.y = (-height + this.size);
    	}
    	if (height - this.size < 0) {
    		this.y = 0;
    	}

    	
    	if (this.x > (width - this.size*pixelAspect)) {
    		this.x = (width - this.size*pixelAspect); //so camera is size*aspect from the edge
    	}
    	if (this.x < (-width + this.size*pixelAspect)) {
    		this.x = (-width + this.size*pixelAspect);
    	}
    	if (width - this.size*pixelAspect < 0) {
    		this.x = 0;
    	}
    }
    
    public void drawSelf(GL2 gl) {
    	//nothing, because you can't see it...
    }
    
    
    // ===========================================
    public void setView(GL2 gl) {
    	gl.glClearColor((float)myBackground[0], (float)myBackground[1], (float)myBackground[2], (float)myBackground[3]);
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    	
        gl.glLoadIdentity();
        
        gl.glScaled(1/size, 1/size, 1);
    	gl.glRotated(-angle, 0, 0, 1);

    	gl.glTranslated(-x, -y, 0);
    }

    public void reshape(GL2 gl, int x, int y, int width, int height) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU glu = new GLU();
        double ar = (double)width / (double)height; // aspect = width / height

       	glu.gluOrtho2D( -1.0*ar, 1.0*ar, -1.0, 1.0); //because we want constant aspect ratio
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}
