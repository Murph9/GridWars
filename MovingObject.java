
public abstract class MovingObject extends GameObject {

	protected double dx = 0; //default
	protected double dy = 0;
	
	MovingObject(double size, double[] colour) {
		this.colour = colour;
		this.size = size;
	}

	public void setSpeed(double x, double y) {
		dx = x;
		dy = y;
	}
	public double[] getSpeed() {
		return new double[]{dx, dy};
	}
	
	public void blackHole() {
		//does things (that every object does, so do it in here)
	}
	
	//Every subclass must have these methods:
		//how do i add a private method here?
	public abstract void update(double dt);
}