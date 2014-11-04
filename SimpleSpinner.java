import java.util.ArrayList;

import javax.media.opengl.GL2;


public class SimpleSpinner extends MovingObject {
	
	public final static ArrayList<SimpleSpinner> ALL_THIS = new ArrayList<SimpleSpinner>();
	public int rotSpeed = 180; //if not set
	
	SimpleSpinner(double size, double[] colour) {
		super(size, colour);
		this.rotSpeed = (GameEngine.rand.nextInt(270)+90)*(GameEngine.rand.nextInt(2)*2-1);
		ALL_THIS.add(this);
	}

	public void update(double dt) {
		double[] pos = getPosition();
		pos[0] += mySpeedX*dt;
		pos[1] += mySpeedY*dt;
		
		double rot = getRotation();
		rot += dt*rotSpeed;
		setRotation(rot);
		
		if (pos[0] > GameEngine.boardWidth-(mySize/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = GameEngine.boardWidth-(mySize/2);
		} else if (pos[0] < -GameEngine.boardWidth+(mySize/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = -GameEngine.boardWidth+(mySize/2);
		}
		
		if (pos[1] > GameEngine.boardHeight-(mySize/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = GameEngine.boardHeight-(mySize/2);
		} else if (pos[1] < -GameEngine.boardHeight+(mySize/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = -GameEngine.boardHeight+(mySize/2);
		}
		
		blackHole();
		
		for (SimpleSpinner s: ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = mySpeedX - s.mySpeedX;
				double distY = mySpeedY - s.mySpeedY;
				if (distX*distX + distY*distY < mySize*s.mySize) {
//					mySpeedX
				}
			}
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
}
