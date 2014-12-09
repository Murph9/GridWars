import javax.media.opengl.GL2;


public class Particle extends MovingObject {

	private boolean inOrbit;
	private double decayTimer;
	private double speed;
	
	private double thickness;
	
	Particle(double thickness, double[] colour, double time) {
		super(1, colour);
		this.thickness = thickness;
		speed = 1;
		decayTimer = GameEngine.rand.nextDouble()*time + 0.3;
		inOrbit = false;
	}

	@Override
	public void update(double dt) {
		x += dx*dt*speed;
		y += dy*dt*speed;
		
		this.angle = Math.atan2(dy, dx);
		
		//can't use the helper function here ;(, because the size of the object is used as 0
		if (x > GameEngine.boardWidth) {
			dx = -dx;
			x = GameEngine.boardWidth;
		} else if (x < -GameEngine.boardWidth) {
			dx = -dx;
			x = -GameEngine.boardWidth;
		}
		
		if (y > GameEngine.boardHeight) {
			dy = -dy;
			y = GameEngine.boardHeight;
		} else if (y < -GameEngine.boardHeight) {
			dy = -dy;
			y = -GameEngine.boardHeight;
		}
		
		if (!inOrbit) {
			decayTimer -= dt;
			dx /= 1.065; //what drag should i have ?
			dy /= 1.065;
		}
		
		if (decayTimer <= 0) {
			amHit(false);
		}
		
		blackHole();
	}

	@Override
	public void selfCol() {
//		doesn't exist in this
	}
	
	public void blackHole() {
//		for (BlackHole h: BlackHole.ALL_THIS) {
//			if (!h.isInert()) {
//				double distx = h.x - x;
//				double disty = h.y - y;
//				double dist = Math.sqrt(distx*distx + disty*disty);
//				
//				if (dist < h.size*BlackHole.SUCK_RADIUS) {
//					decayTimer = 1; //so we don't decay
//					
//					if (dist < h.size*BlackHole.SUCK_RADIUS/2) {
//						dy -= (h.x - x)*2 + h.dy;
//						dx += (h.y - y)*2 + h.dx;
//					} else {
//						dy -= (h.x - x) + h.dy;
//						dx += (h.y - y) + h.dx;
//					}
//					double speed = Math.sqrt(dx*dx + dy*dy);
//					if (speed > this.speed*3) {
//						dx /= speed;
//						dy /= speed;
//					}
//				}
//			}
//		} //this is if you want orbiting particles (looks kind of weird at the moment)
	}
	
	public void drawSelf(GL2 gl) {
		gl.glColor4d(colour[0], colour[1], colour[2], colour[3]);
		
		gl.glPointSize((float)thickness);
		gl.glBegin(GL2.GL_POINTS);
			gl.glVertex2d(0,0);
		gl.glEnd();
	}

}
