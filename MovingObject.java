
public abstract class MovingObject extends GameObject {

	protected double mySpeedX = 0; //default
	protected double mySpeedY = 0;
	
	MovingObject(double size, double[] colour) {
		this.size = size;
		this.colour = colour;
	}

	public void setSpeed(double x, double y) {
		mySpeedX = x;
		mySpeedY = y;
	}
	public double[] getSpeed() {
		return new double[]{mySpeedX, mySpeedY};
	}
	
	/**Every subclass must have this method, so it moves.
	 */
	public abstract void update(double dt);
}
