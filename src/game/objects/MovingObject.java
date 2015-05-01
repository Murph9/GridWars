package game.objects;
import game.logic.*;

import java.util.ArrayList;


public abstract class MovingObject extends GameObject {
	public static final double DEFFAULT_SPAWN_TIMER = 0.6; //stock spawn time
	
	public double dx = 0; //default
	public double dy = 0;
	
	
	MovingObject(double size, double[] colour) {
		super();
		this.colour = colour;
		this.size = size;
		this.spawnTimer = 0;
		
		//don't put SoundEffect object spawn here, objects might want their own sound
	}

	public void setSpeed(double x, double y) {
		dx = x;
		dy = y;
	}
	public double[] getSpeed() {
		return new double[]{dx, dy};
	}
	
	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		
		if (! (this instanceof Particle)) {
			
			if (this instanceof PlayerBullet) { //produces 4 (much more bullets than anything else)
				int offset = Engine.rand.nextInt(180);
				for (int i = 0; i < 4; i++) {
					MovingObject p = new Particle(2, colour, 0.7, Particle.DEFAULT_DRAG);
					p.x = x;
					p.y = y;
					p.dx = Engine.rand.nextDouble()*Math.cos(offset + 360*i/4)*8 + dx/2;
					p.dy = Engine.rand.nextDouble()*Math.sin(offset + 360*i/4)*8 + dy/2;
				}
			} else {
				int offset = Engine.rand.nextInt(180);
				for (int i = 0; i < 8; i++) {
					MovingObject p = new Particle(2, this.colour, 1, Particle.DEFAULT_DRAG);
					p.x = x;
					p.y = y;
					p.dx = Math.cos(offset+i*360/8)*16*Engine.rand.nextDouble();
					p.dy = Math.sin(offset+i*360/8)*16*Engine.rand.nextDouble();
				}
			}
		}
	}
	
	/**This function edits the x, y, dx and dy values of the object its called on, be careful.
	 */
	public void blackHole() {
		//does things (that every object does, so do it in here)
		ArrayList<BlackHole> objects = new ArrayList<BlackHole>(BlackHole.ALL_THIS);
		
		for (BlackHole h: objects) {
			if (h.isInert() || h.equals(this)) { //just incase a black hole targets itself..
				continue;
			}
			double distx = h.x - x;
			double disty = h.y - y;
			double dist = Math.sqrt(distx*distx + disty*disty);
			
			if (dist < h.size*BlackHole.SUCK_RADIUS) {
				dx += (h.size*BlackHole.SUCK_RADIUS-dist+2)*2*distx/dist;
				dy += (h.size*BlackHole.SUCK_RADIUS-dist+2)*2*disty/dist;
				
				if (dist < size*h.size/2 && !(this instanceof BlackHole)) {
					amHit(false);
					h.giveObject(distx, disty);
				}
			}
		}
	}
	
	//Every subclass must have these methods:
	public abstract void update(double dt); //for anything that moves (or changes over time)
	public abstract void selfCol(); //for object collision code in bullets
}