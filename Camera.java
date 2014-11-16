
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The camera is a GameObject that can be moved, rotated and scaled like any other.
 * 		 Implments the setView() and reshape() methods.
 *       The methods you need to complete are at the bottom of the class
 * @author malcolmr
 */
public class Camera extends GameObject {

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

    public void update (double dt) { //thought causing
    	double[] myPos = getPosition();
    	double[] pos = GameEngine.getPlayerPos();
    	myPos[0] = (pos[0])/3; //to feel move like a game here
    	myPos[1] = (pos[1])/3;
    	setPosition(myPos);
    }
    
    public void drawSelf(GL2 gl) {
    	//nothing, because you can't see it...
    }
    
    // ===========================================
    public void setView(GL2 gl) {
    	gl.glClearColor((float)myBackground[0], (float)myBackground[1], (float)myBackground[2], (float)myBackground[3]);
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    	
        gl.glLoadIdentity();
        
        gl.glScaled(1/getSize(), 1/getSize(), 1);
    	gl.glRotated(-getRotation(), 0, 0, 1);

    	double[] myTranslation = getPosition();
    	myTranslation[0] *= -1;
    	myTranslation[1] *= -1;
    	gl.glTranslated(myTranslation[0], myTranslation[1], 0);
    }

    public void reshape(GL2 gl, int x, int y, int width, int height) {
        // aspect = width / height
    	//from lecture slides:
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU glu = new GLU();
        double ar = (double)width / (double)height;
        if (ar >= 1) {
        	glu.gluOrtho2D( -1.0*ar, 1.0*ar, -1.0, 1.0);
        } else {
        	glu.gluOrtho2D(-1.0, 1.0, -1.0/ar, 1.0/ar);
        }
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}
