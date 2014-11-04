import java.util.ArrayList;

import javax.media.opengl.GL2;


public class AnnoyingSeeker extends HomingObject {

	public final static ArrayList<AnnoyingSeeker> ALL_THIS = new ArrayList<AnnoyingSeeker>();
	
	public static final int HOME_SPEED = 10;
	public static final int score = 10;
	
	AnnoyingSeeker(double size, double[] colour) {
		super(size, colour, HOME_SPEED);
		ALL_THIS.add(this);
	}
	
	public void drawSelf (GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId()); //get id of the dot file
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(0, 0);
			gl.glVertex2d(-0.5, -0.5);
			gl.glTexCoord2d(1, 0);
			gl.glVertex2d(0.5, -0.5);
			gl.glTexCoord2d(1, 1);
			gl.glVertex2d(0.5, 0.5);
			gl.glTexCoord2d(0, 1);
			gl.glVertex2d(-0.5, 0.5);
		gl.glEnd();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
	}
}