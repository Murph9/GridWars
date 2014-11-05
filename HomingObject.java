
public abstract class HomingObject extends MovingObject {

	protected float speed = 2; //if not given a speed
	
	HomingObject(double size, double[] colour, float speed) {
		super(size, colour);
		this.speed = speed;
	}

	protected void setSpeed(int speed) { //because objects have different speeds
		this.speed = speed;
	}
	
	public void update(double dt) {
		x += dx*dt*speed;
		y += dy*dt*speed;
		
		double[] p = GameEngine.getPlayerPos();

		dx = p[0]-x;
		dy = p[1]-y;

		double dist = Math.sqrt(dx*dx + dy*dy);
		if (dist != 0) { //divide by zero errors are bad
			dx /= dist;
			dy /= dist; //now they are normalised
		}
		
		if (x > GameEngine.boardWidth-(size/2)) {
			dx = GameEngine.boardWidth-(size/2);
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
}

	