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
	
	public void update(double dt) {
		super.update(dt);
		
		for (HomingDiamond d: HomingDiamond.ALL_THIS) {
			if (!d.equals(this)) { //because that would be silly
				double distX = d.x - x;
				double distY = d.y - y;
				if ((distX*distX) + (distY*distY) < (size*size)+(d.size*d.size)) {
					dx -= Helper.sgn(distX)/2;
					dy -= Helper.sgn(distY)/2;
				}
			}
		}
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > MAX_SPEED) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.DIAMOND].getTextureId()); //get id of the dot file
		
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
