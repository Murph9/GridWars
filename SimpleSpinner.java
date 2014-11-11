import java.util.ArrayList;

import javax.media.opengl.GL2;


public class SimpleSpinner extends MovingObject {
	
	public final static ArrayList<SimpleSpinner> ALL_THIS = new ArrayList<SimpleSpinner>();
	public final static int score = 25;
	
	public int rotSpeed = 180; //if not set
	public static final int myMaxSpeed = 2;
	
	
	SimpleSpinner(double size, double[] colour) {
		super(size, colour);
		this.rotSpeed = (GameEngine.rand.nextInt(180)+90)*(GameEngine.rand.nextInt(2)*2-1);
		dx = GameEngine.rand.nextDouble();
		dy = GameEngine.rand.nextDouble();
		ALL_THIS.add(this);
	}

	public void update(double dt) {
		x += dx*dt;
		y += dy*dt;
		
		angle += dt*rotSpeed;
		
		Helper.keepInside(this, Helper.BOUNCE);
		
		blackHole();
		selfCol();
		
		double speed = Math.sqrt(dx*dx+dy*dy);
		if (speed != 0 && speed > myMaxSpeed) {
			dx /= speed;
			dy /= speed;
		}
	}
	
	public void selfCol() {
		for (SimpleSpinner s: SimpleSpinner.ALL_THIS) {
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
	
	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
		GameEngine.score.addScore(score);
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SPINNER].getTextureId());
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);
		
		Helper.square(gl);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
