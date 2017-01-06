package game.objects;
import java.util.ArrayList;

import game.logic.*;

import javax.media.opengl.GL2;


public class Particle extends MovingObject {

	public static final double DEFAULT_DRAG = 1.04;
	public final static ArrayList<Particle> ALL_THIS = new ArrayList<Particle>();
	
	private boolean inOrbit;
	private double decayTimer;
	private double speed;
	private double drag;
	
	private double thickness;
	
	private boolean ifBlackHoleParticle;
	
	public Particle(double thickness, double[] colour, double time, double drag) {
		super(1, colour);
		
		
		ALL_THIS.add(this);
		
		this.thickness = thickness;
		this.speed = 1;
		this.decayTimer = Engine.rand.nextDouble()*time + 0.3;
		this.inOrbit = false;
		this.drag = drag;
		
		this.spawnTimer = 0;
		
		ifBlackHoleParticle = false;
		
		if (!Engine.settings.ifParticles) {
			amHit(false);
		}

		double d = Engine.rand.nextDouble()*100;
		if (d > Engine.settings.particlePercent) {
			amHit(false);
		}
	}

	/** Setting to prevent decay near a blackhole
	 */
	public void isBblackHoleParticle() {
		ifBlackHoleParticle = true;
	}
	
	@Override
	public void update(double dt) {
		x += dx*dt*speed;
		y += dy*dt*speed;
		
		this.angle = Math.atan2(dy, dx);
		
		//can't use the helper function here ;(
			//because the size of the object is used as 'small'
		if (x > Engine.settings.boardWidth) {
			dx = -dx;
			x = Engine.settings.boardWidth;
		} else if (x < -Engine.settings.boardWidth) {
			dx = -dx;
			x = -Engine.settings.boardWidth;
		}
		
		if (y > Engine.settings.boardHeight) {
			dy = -dy;
			y = Engine.settings.boardHeight;
		} else if (y < -Engine.settings.boardHeight) {
			dy = -dy;
			y = -Engine.settings.boardHeight;
		}
		
		if (!inOrbit) {
			decayTimer -= dt;
			dx /= drag; //default drag is the best looking...
			dy /= drag;
		}
		
		if (decayTimer <= 0) {
			amHit(false);
		}
//		decayTimer *= 1.2;
		blackHole();
	}

	@Override
	public void selfCol() {
//		doesn't exist in this
	}
	
	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		Particle.ALL_THIS.remove(this);
	}
	
	//this is if you want orbiting particles (looks kind of weird at the moment)
		//causes lag and needs refining 
	public void blackHole() {
//		decayTimer *= 1.2; //please don't they don't go away, ever
		if (!ifBlackHoleParticle) {
			return;
		}
		for (BlackHole h: BlackHole.ALL_THIS) {
			if (!h.isInert() && h.canAcceptParticle()) {
				double ddx = h.x - x;
				double ddy = h.y - y;
				double dist = Math.sqrt(ddx*ddx + ddy*ddy);
				
				
				if (dist < h.size*BlackHole.SUCK_RADIUS/2 && dist > 0.01) {
					inOrbit = true; //they don't decay when in orbit
					
					h.giveParticle();
					
					ddx /= dist;
					ddy /= dist;
					
					dx += ddx/2; //+h.dx/4;
					dy += ddy/2; //+h.dy/4;
					
				} else {
					inOrbit = false;
				}
			}
		} 
	}
	
	//this is overriden for faster speed
	public void draw(GL2 gl) {
		drawSelf(gl); //stops all of the expensive ass code that translates, rotates and scales a 4d matrix every particle
	}
	
	public void drawSelf(GL2 gl) {
		gl.glColor4d(colour[0], colour[1], colour[2], colour[3]);
		
		gl.glPointSize((float)thickness);
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(x,y);
			gl.glVertex2d(x-(dx*1/60d),y-(dy*1/60d));
		gl.glEnd();
	}
}

