import java.util.ArrayList;

import javax.media.opengl.GL2;


public class HomingButterfly extends HomingObject {

	public final static ArrayList<HomingButterfly> ALL_THIS = new ArrayList<HomingButterfly>();
	
	public static final int MAX_SPEED = 5;
	
	HomingButterfly(double size, double[] colour) {
		super(size, colour, MAX_SPEED);
		ALL_THIS.add(this);
		score = 10;
	}
	
	public void selfCol() {
		for (HomingButterfly d: HomingButterfly.ALL_THIS) {
			if (!d.equals(this)) { //because that would be silly
				double distX = d.x - x;
				double distY = d.y - y;
				double dist = (distX*distX) + (distY*distY) + 0.0001;
				if (dist < 0.5*((size*size)+(d.size*d.size))) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}
	
	
	public void blackHole() {
		//don't think it actually does anything in this
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BUTTERFLY].getTextureId()); //get id of the dot file
    	super.drawSelf(gl);
	}

	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		ALL_THIS.remove(this);
	}
}