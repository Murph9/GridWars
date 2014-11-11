import java.util.ArrayList;

import javax.media.opengl.GL2;


public class HomingDiamond extends HomingObject {
	
	public final static ArrayList<HomingDiamond> ALL_THIS = new ArrayList<HomingDiamond>();
	
	public static final int MAX_SPEED = 5;
	public static final int score = 50;
	
	HomingDiamond(double size, double[] colour) {
		super(size, colour, MAX_SPEED);
		ALL_THIS.add(this);
	}
	
	//this works perfectly:, copy this if you need good collision of objects (not bouncing) 
	public void selfCol() {
		for (HomingDiamond d: HomingDiamond.ALL_THIS) {
			if (!d.equals(this)) { //because that would be silly
				double distX = d.x - x;
				double distY = d.y - y;
				double dist = (distX*distX) + (distY*distY);
				if (dist < 0.5*((size*size)+(d.size*d.size))) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist)); //try changing 12 for good effect
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.DIAMOND].getTextureId());
		
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