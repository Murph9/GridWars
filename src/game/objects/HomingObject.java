package game.objects;

import game.logic.*;

public abstract class HomingObject extends MovingObject {

	protected double MAX_SPEED;
	
	HomingObject(double size, double[] colour, double speed) {
		super(size, colour);
		this.MAX_SPEED = speed;
	}

	protected void setSpeed(int speed) { //because objects have different speeds
		this.MAX_SPEED = speed;
	}
	
	
	public void update(double dt) {
		if (this.spawnTimer > 0) {
			this.spawnTimer -= dt;
			return;
		}
		
		x += dx*dt*MAX_SPEED; //because we always want it going full speed
		y += dy*dt*MAX_SPEED;
		
		Helper.keepInside(this, Helper.SPLAT);
		
		blackHole();
		selfCol();
		
		double[] playerPos = Engine.getPlayerPos();
		dx += (playerPos[0]-x)/2;
		dy += (playerPos[1]-y)/2;
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised yay
		}
	}
}
