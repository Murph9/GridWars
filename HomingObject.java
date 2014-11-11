
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
		
		Helper.keepInside(this, Helper.SPLAT);
		
		blackHole();
		selfCol();
		
		double[] a = new double[]{GameEngine.getPlayerPos()[0]-x,GameEngine.getPlayerPos()[1]-y};
		
		double dist = a[0]*a[0] + a[1]*a[1];
		if (dist != 0) {
			dx += a[0]/dist;
			dy += a[1]/dist;
		}
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised yay
		}
	}
}

	