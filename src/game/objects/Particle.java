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
	
	private boolean ifBlackHoleParticle;
	
	Particle() {
		this(2, GameEngine.WHITE, 0.7, 1.065); //default settings
	}
	
	Particle(int a) {
		this(2, GameEngine.WHITE, 0.7, 1.065);
	}
	
	public Particle(double thickness, double[] colour, double time, double drag) {
		super(1, colour);
		ALL_THIS.add(this);
		
		this.thickness = thickness;
		this.speed = 1;
		this.decayTimer = GameEngine.rand.nextDouble()*time + 0.3;
		this.inOrbit = false;
		this.drag = drag;
		
		ifBlackHoleParticle = false;
		
		if (!GameEngine.curSettings.ifParticles()) {
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
		if (x > GameEngine.curSettings.getBoardWidth()) {
			dx = -dx;
			x = GameEngine.curSettings.getBoardWidth();
		} else if (x < -GameEngine.curSettings.getBoardWidth()) {
			dx = -dx;
			x = -GameEngine.curSettings.getBoardWidth();
		}
		
		if (y > GameEngine.curSettings.getBoardHeight()) {
			dy = -dy;
			y = GameEngine.curSettings.getBoardHeight();
		} else if (y < -GameEngine.curSettings.getBoardHeight()) {
			dy = -dy;
			y = -GameEngine.curSettings.getBoardHeight();
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
		if (!ifBlackHoleParticle) {
			return;
		}
		for (BlackHole h: BlackHole.ALL_THIS) {
			if (!h.isInert()) {
				double ddx = h.x - x;
				double ddy = h.y - y;
				double dist = Math.sqrt(ddx*ddx + ddy*ddy);
				
				
				if (dist < h.size*BlackHole.SUCK_RADIUS/2 && dist > 0.01) {
					inOrbit = true;
					
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
	
	public void drawSelf(GL2 gl) {
		gl.glColor4d(colour[0], colour[1], colour[2], colour[3]);
		
		gl.glPointSize((float)thickness);
		gl.glBegin(GL2.GL_POINTS);
			gl.glVertex2d(0,0);
		gl.glEnd();
	}
}

