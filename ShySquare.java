import java.util.ArrayList;

import javax.media.opengl.GL2;


public class ShySquare extends MovingObject {

	public final static ArrayList<ShySquare> ALL_THIS = new ArrayList<ShySquare>();
	
	public static final int score = 100;
	
	private float speed = 4;
	private float dodgeRange = 4;
	private float dodgeSpeed = 1f;
	
	ShySquare(double size, double[] colour) {
		super(size, colour);
		ALL_THIS.add(this);
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

		ArrayList<GameObject> objects = new ArrayList<GameObject>(PlayerBullet.ALL_THIS);

		for (GameObject o: objects) {
			double[] bullPos = o.getPosition();
			double distance = (myPos[0] - bullPos[0])*(myPos[0] - bullPos[0]) + (myPos[1] - bullPos[1])*(myPos[1] - bullPos[1]);
			if (distance < dodgeRange) {
				mySpeedX += (myPos[0]-bullPos[0])*dodgeSpeed/(distance);
				mySpeedY += (myPos[1]-bullPos[1])*dodgeSpeed/(distance);//yes its some random number
			}
		}
		
		
		if (myPos[0] > GameEngine.boardWidth-(mySize/2)) {
			mySpeedX = 0; //no bounce
			myPos[0] = GameEngine.boardWidth-(mySize/2);
		} else if (myPos[0] < -GameEngine.boardWidth+(mySize/2)) {
			mySpeedX = 0;
			myPos[0] = -GameEngine.boardWidth+(mySize/2);
		}
		
		if (myPos[1] > GameEngine.boardHeight-(mySize/2)) {
			mySpeedY = 0;
			myPos[1] = GameEngine.boardHeight-(mySize/2);
		} else if (myPos[1] < -GameEngine.boardHeight+(mySize/2)) {
			mySpeedY = 0;
			myPos[1] = -GameEngine.boardHeight+(mySize/2);
		}
		
		setPosition(myPos); //set it
	}

	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SHY].getTextureId()); //get id of the dot file
		
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
