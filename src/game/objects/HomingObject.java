package game.objects;

import game.logic.*;

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
		if (this.spawnTimer > 0) {
			this.spawnTimer -= dt;
			return;
		}
		
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		
		Helper.keepInside(this, Helper.SPLAT);
		
		blackHole();
		selfCol();
		
		double[] playerPos = GameEngine.getPlayerPos();
		dx += (playerPos[0]-x)/2;
		dy += (playerPos[1]-y)/2;
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised yay
		}
	}
}
