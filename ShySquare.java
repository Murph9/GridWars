import java.util.ArrayList;

import javax.media.opengl.GL2;


public class ShySquare extends MovingObject {

	private float speed = 4;
	private float dodgeRange = 4;
	
	ShySquare(double size, double[] colour) {
		super(size, colour);
		
	}

	@Override
	public void update(double dt) {
		double[] myPos = getPosition();
		myPos[0] += mySpeedX*dt*speed;
		myPos[1] += mySpeedY*dt*speed;
		
		double[] s = GameEngine.getPlayerPos();

		mySpeedX = s[0]-myPos[0];
		mySpeedY = s[1]-myPos[1];

		double dist = Math.sqrt(mySpeedX*mySpeedX + mySpeedY*mySpeedY);
		if (dist != 0) { //divide by zero errors are bad
			mySpeedX /= dist;
			mySpeedY /= dist; //now they are normalised
		}

		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);

		for (GameObject o: objects) {
			if (o instanceof PlayerBullet) {
				double[] bullPos = o.getPosition();
				double distance = (myPos[0] - bullPos[0])*(myPos[0] - bullPos[0]) + (myPos[1] - bullPos[1])*(myPos[1] - bullPos[1]);
				if (distance < dodgeRange) {
					mySpeedX += (myPos[0]-bullPos[0])*speed/(2*distance);
					mySpeedY += (myPos[1]-bullPos[1])*speed/(2*distance);
				}
			}
		}
		
		
		if (myPos[0] > GameEngine.boardWidth-(size/2)) {
			mySpeedX = 0; //no bounce
			myPos[0] = GameEngine.boardWidth-(size/2);
		} else if (myPos[0] < -GameEngine.boardWidth+(size/2)) {
			mySpeedX = 0;
			myPos[0] = -GameEngine.boardWidth+(size/2);
		}
		
		if (myPos[1] > GameEngine.boardHeight-(size/2)) {
			mySpeedY = 0;
			myPos[1] = GameEngine.boardHeight-(size/2);
		} else if (myPos[1] < -GameEngine.boardHeight+(size/2)) {
			mySpeedY = 0;
			myPos[1] = -GameEngine.boardHeight+(size/2);
		}
		
		setPosition(myPos); //set it
	}

	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.DIAMOND].getTextureId()); //get id of the dot file
		
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

	
	
	@Override
	public int score() {
		return 100;
	}
	
}
