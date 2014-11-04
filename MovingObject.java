
public abstract class MovingObject extends GameObject {

	protected double mySpeedX = 0; //default
	protected double mySpeedY = 0;
	
	MovingObject(double size, double[] colour) {
		this.colour = colour;
		this.mySize = size;
	}

	public void setSpeed(double x, double y) {
		mySpeedX = x;
		mySpeedY = y;
	}
	public double[] getSpeed() {
		return new double[]{mySpeedX, mySpeedY};
	}
	
	public void blackHole() {
		//does things (that every object does)
	}
	
	//Every subclass must have these methods:
		//how do i add a private method here?
	public abstract void update(double dt);
}