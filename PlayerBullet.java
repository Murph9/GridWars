import java.util.ArrayList;

import javax.media.opengl.GL2;


public class PlayerBullet extends MovingObject {

	public final static ArrayList<PlayerBullet> ALL_THIS = new ArrayList<PlayerBullet>();//not sure actually
	
	PlayerBullet(double size, double[] colour) {
		super(size, colour);
		PlayerBullet.ALL_THIS.add(this);
	}
	
	public void update(double dt) {
		double[] pos = getPosition();
		
		pos[0] += mySpeedX*dt;
		pos[1] += mySpeedY*dt;
		setPosition(pos); //set it
		boolean hasCollided = false;
		
		if (pos[0] > GameEngine.boardWidth-(mySize/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = GameEngine.boardWidth-(mySize/2);
			hasCollided = true;
		} else if (pos[0] < -GameEngine.boardWidth+(mySize/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = -GameEngine.boardWidth+(mySize/2);
			hasCollided = true;
		}
		
		if (pos[1] > GameEngine.boardHeight-(mySize/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = GameEngine.boardHeight-(mySize/2);
			hasCollided = true;
		} else if (pos[1] < -GameEngine.boardHeight+(mySize/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = -GameEngine.boardHeight+(mySize/2);
			hasCollided = true;
		}
		
		//this is the class that handles deleting objects when bullets hit them, 
			//every other class just checks against the same type. This checks EVERYTHING, yes everything
		
		
		if (hasCollided) {
			this.destroy();
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BULLET].getTextureId());
		
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

//	@Override
//	public void collision(GameObject o) {
//		if (o instanceof PlayerBullet || o instanceof Player) {
//		} else {
//			this.destroy();
//		}
//	}

}
