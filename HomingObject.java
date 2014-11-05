
public abstract class HomingObject extends MovingObject {

	protected float MAX_SPEED = 2; //if not given a speed
	
	HomingObject(double size, double[] colour, float speed) {
		super(size, colour);
		this.MAX_SPEED = speed;
	}

	protected void setSpeed(int speed) { //because objects have different speeds
		this.MAX_SPEED = speed;
	}
	
	public void update(double dt) {
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		
		if (x > GameEngine.boardWidth-(size/2)) {
			dx = 0;
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
		
		blackHole();
		
		double[] p = GameEngine.getPlayerPos();
		dx += p[0]-x;
		dy += p[1]-y;
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0) {
			dx /= speed;
			dy /= speed; //now they are normalised
		}
	}
}

	