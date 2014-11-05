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
		x += dx*dt*speed;
		y += dy*dt*speed;
		
		double[] s = GameEngine.getPlayerPos();

		dx = s[0]-x;
		dy = s[1]-y;

		double dist = Math.sqrt(dx*dx + dy*dy);
		if (dist != 0) { //divide by zero errors are bad
			dx /= dist;
			dy /= dist; //now they are normalised
		}

		ArrayList<GameObject> objects = new ArrayList<GameObject>(PlayerBullet.ALL_THIS);

		for (GameObject o: objects) {
			double distance = (x - o.x)*(x - o.x) + (y - o.y)*(y - o.y);
			if (distance < dodgeRange && distance != 0) {
				dx += (x-o.x)*dodgeSpeed/(distance);
				dy += (y-o.y)*dodgeSpeed/(distance);
			}
		}
		
		if (x > GameEngine.boardWidth-(size/2)) {
			dx = 0; //no bounce
			x = GameEngine.boardWidth-(size/2);
		} else if (x < -GameEngine.boardWidth+(size/2)) {
			dx = 0;
			x = -GameEngine.boardWidth+(size/2);
		}
		
		if (y > GameEngine.boardHeight-(size/2)) {
			dy = 0;
			y = GameEngine.boardHeight-(size/2);
		} else if (y < -GameEngine.boardHeight+(size/2)) {
			dy = 0;
			y = -GameEngine.boardHeight+(size/2);
		}
	}

	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
		GameEngine.score.addScore(score);
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SHY].getTextureId()); //get id of the dot file
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);
		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

}
