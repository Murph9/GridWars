import java.util.ArrayList;

import javax.media.opengl.GL2;


public class HomingButterfly extends HomingObject {

	public final static ArrayList<HomingButterfly> ALL_THIS = new ArrayList<HomingButterfly>();
	
	public static final int MAX_SPEED = 5;
	public static final int score = 10;
	
	HomingButterfly(double size, double[] colour) {
		super(size, colour, MAX_SPEED);
		ALL_THIS.add(this);
	}
	
	public void selfCol() {
		for (HomingButterfly d: HomingButterfly.ALL_THIS) {
			if (!d.equals(this)) { //because that would be silly
				double distX = d.x - x;
				double distY = d.y - y;
				double dist = (distX*distX) + (distY*distY);
				if (dist < 0.5*((size*size)+(d.size*d.size))) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BUTTERFLY].getTextureId()); //get id of the dot file
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);

    	Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
		GameEngine.curGame.addScore(score);
	}
}