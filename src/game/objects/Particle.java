package game.objects;
import java.util.ArrayList;

import game.logic.*;

import javax.media.opengl.GL2;


public class Particle extends MovingObject {

	public static final double DEFAULT_DRAG = 1.065;
	public final static ArrayList<Particle> ALL_THIS = new ArrayList<Particle>();
	
	private boolean inOrbit;
	private double decayTimer;
	private double speed;
	private double drag;
	
	private double thickness;
	
	Particle() {
		this(2, GameEngine.WHITE, 0.7, 1.065); //default settings
	}
	
	public Particle(double thickness, double[] colour, double time, double drag) {
		super(1, colour);
		ALL_THIS.add(this);
		
		this.thickness = thickness;
		this.speed = 1;
		this.decayTimer = GameEngine.rand.nextDouble()*time + 0.3;
		this.inOrbit = false;
		this.drag = drag;
		
		if (!GameEngine.curGame.ifParticles()) {
			amHit(false);
		}
	}

	@Override
	public void update(double dt) {
		x += dx*dt*speed;
		y += dy*dt*speed;
		
		this.angle = Math.atan2(dy, dx);
		
		//can't use the helper function here ;(
			//because the size of the object is used as 'small'
		if (x > GameEngine.curGame.getWidth()) {
			dx = -dx;
			x = GameEngine.curGame.getWidth();
		} else if (x < -GameEngine.curGame.getWidth()) {
			dx = -dx;
			x = -GameEngine.curGame.getWidth();
		}
		
		if (y > GameEngine.curGame.getHeight()) {
			dy = -dy;
			y = GameEngine.curGame.getHeight();
		} else if (y < -GameEngine.curGame.getHeight()) {
			dy = -dy;
			y = -GameEngine.curGame.getHeight();
		}
		
		if (!inOrbit) {
			decayTimer -= dt;
			dx /= drag; //default drag is the best looking...
			dy /= drag;
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
	
	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		Particle.ALL_THIS.remove(this);
	}
	
	//this is if you want orbiting particles (looks kind of weird at the moment)
		//causes lag and needs refining 
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
//		} 
	}
	
	public void drawSelf(GL2 gl) {
		gl.glColor4d(colour[0], colour[1], colour[2], colour[3]);
		
		gl.glPointSize((float)thickness);
		gl.glBegin(GL2.GL_POINTS);
			gl.glVertex2d(0,0);
		gl.glEnd();
	}
}

