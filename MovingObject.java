import java.util.ArrayList;


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
		ArrayList<BlackHole> objects = new ArrayList<BlackHole>(BlackHole.ALL_THIS);
		
		for (BlackHole h: objects) {
			if (h.isInert()) {
				continue;
			}
			double distx = h.x - x;
			double disty = h.y - y;
			double dist = Math.sqrt(distx*distx + disty*disty);
			
			if (dist < h.size*BlackHole.SUCK_RADIUS/2) {
				dx += (h.size*BlackHole.SUCK_RADIUS-dist+2)*2*distx/dist;
				dy += (h.size*BlackHole.SUCK_RADIUS-dist+2)*2*disty/dist;
				
				if (dist < size*h.size/2) {
					destroy();
					h.giveObject();
				}
			}
		}
	}
	
	//Every subclass must have these methods:
		//how do i add a private method here?
	public abstract void update(double dt);
}