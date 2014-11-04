
import java.util.HashSet;

import javax.media.opengl.GL2;


public class SimpleSpinner extends MovingObject {
	
	public final static HashSet<SimpleSpinner> ALL_THIS = new HashSet<SimpleSpinner>();
	public int rotSpeed = 180; //if not set
	
	SimpleSpinner(double size, double[] colour, int rotationSpeed) {
		super(size, colour);
		this.rotSpeed = rotationSpeed;
		ALL_THIS.add(this);
	}

	public void update(double dt) {
		double[] pos = getPosition();
		pos[0] += mySpeedX*dt;
		pos[1] += mySpeedY*dt;
		
		double rot = getRotation();
		rot += dt*rotSpeed;
		setRotation(rot);
		
		if (pos[0] > GameEngine.boardWidth-(size/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = GameEngine.boardWidth-(size/2);
		} else if (pos[0] < -GameEngine.boardWidth+(size/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = -GameEngine.boardWidth+(size/2);
		}
		
		if (pos[1] > GameEngine.boardHeight-(size/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = GameEngine.boardHeight-(size/2);
		} else if (pos[1] < -GameEngine.boardHeight+(size/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = -GameEngine.boardHeight+(size/2);
		}
		
		
		
		setPosition(pos);
	}
	
	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SPINNER].getTextureId());
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(0, 0);
			gl.glVertex2d(-size/2, -size/2);
			gl.glTexCoord2d(1, 0);
			gl.glVertex2d(size/2, -size/2);
			gl.glTexCoord2d(1, 1);
			gl.glVertex2d(size/2, size/2);
			gl.glTexCoord2d(0, 1);
			gl.glVertex2d(-size/2, size/2);
		gl.glEnd();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

	//it gets not so 'simplespinner' here (only object in the whole game that bounces)
//	@Override
//	public void collision(GameObject o) {
//		if (o instanceof PlayerBullet) {
//			this.destroy();
//		} else if (o instanceof SimpleSpinner) {
//			
//		}
//	}

}
