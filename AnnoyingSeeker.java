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
	
	public void update(double dt) {
		super.update(dt);
		
		for (AnnoyingSeeker s: AnnoyingSeeker.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				if ((distX*distX) + (distY*distY) < (size*size)+(s.size*s.size)) {
					dx -= Helper.sgn(distX);
					dy -= Helper.sgn(distY);
				}
			}
		}
	}
	
	public void drawSelf (GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId()); //get id of the dot file
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);

    	Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
		GameEngine.score.addScore(score);
	}
}