import java.util.HashSet;

import javax.media.opengl.GL2;


public class PlayerBullet extends MovingObject {

	public final static HashSet<SimpleSpinner> ALL_THIS = new HashSet<SimpleSpinner>();//not sure actually
	
	PlayerBullet(double size, double[] colour) {
		super(size, colour);
	}
	
	public void update(double dt) {
		double[] pos = getPosition();
		
		pos[0] += mySpeedX*dt;
		pos[1] += mySpeedY*dt;
		setPosition(pos); //set it
		boolean hasCollided = false;
		
		if (pos[0] > GameEngine.boardWidth-(size/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = GameEngine.boardWidth-(size/2);
			hasCollided = true;
		} else if (pos[0] < -GameEngine.boardWidth+(size/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = -GameEngine.boardWidth+(size/2);
			hasCollided = true;
		}
		
		if (pos[1] > GameEngine.boardHeight-(size/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = GameEngine.boardHeight-(size/2);
			hasCollided = true;
		} else if (pos[1] < -GameEngine.boardHeight+(size/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = -GameEngine.boardHeight+(size/2);
			hasCollided = true;
		}
		
		//this is the class that handles deleting objects when bullets hit them, 
			//every other class just checks against the same type. This checks EVERYTHING
		
		
		if (hasCollided) {
			this.destroy();
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BULLET].getTextureId());
		
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
	

//	@Override
//	public void collision(GameObject o) {
//		if (o instanceof PlayerBullet || o instanceof Player) {
//		} else {
//			this.destroy();
//		}
//	}

}
