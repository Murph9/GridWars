package game.objects;

import game.logic.Engine;
import game.logic.Helper;

import java.util.ArrayList;

import javax.media.opengl.GL2;

//now instead of the blackhole class handling sucking everything in, they handle it themselves
	//but this does require that all objects have a blackhole method ...
public class BlackHole extends MovingObject {

	public static final ArrayList<BlackHole> ALL_THIS = new ArrayList<BlackHole>();
	public static double MAX_SPEED = 0.05; //yeah kinda slow

	public static final int SUCK_RADIUS = 6;
	
	private static final double MAX_SIZE = 2;
	private static final double MIN_SIZE = 0.7;
	private static final double SIZE_INC = 30;
	
	private boolean isInert;
	private int numCount; //number of objects consumed
				//as in NumNumNumNum [aggressively eating]
	
	private int particleCount; //count of orbiting particles, visual indication of size as well
	
	public BlackHole(double spawnTimer) {
		this(spawnTimer, 1, Engine.RED);
	}
	
	BlackHole(double spawnTimer, double size, double[] colour) {
		super(size, colour); //this colour is ignored in the draw method
		isInert = true;
		ALL_THIS.add(this);
		
		this.spawnTimer = spawnTimer;
		score = 150; //default score value
	}
	
	public void giveObject(double x, double y) {
		dx += x/12; //hardcoded number alert, this seems to work well though
		dy += y/12;
		
		numCount++;
		size += 1/SIZE_INC; //SIZE_INC hit points
		if (size > MAX_SIZE) { //2 is now the biggest size it can be
			delete(false); //explode
		}
	}
	
	public void shotAt() { isInert = false; }
	public boolean isInert() { return isInert; }
	
	public boolean canAcceptParticle() { 
		return (particleCount < (size-MIN_SIZE)*SIZE_INC);
	}
	
	public void giveParticle() {
		particleCount++;
	}
	
	@Override
	public void update(double dt) {
		if (this.spawnTimer > 0) {
			this.spawnTimer -= dt*2;
		}
		
		x += dx*dt;//*BlackHole.MAX_SPEED;
		y += dy*dt;//*BlackHole.MAX_SPEED;
		
		particleCount = 0;
		
		//particle effects yay - maybe fix spawning positions later
		double rand = Engine.rand.nextDouble();
		if (!isInert && rand < (size/MAX_SIZE)){
			Particle a = new Particle(3, new double[]{Engine.rand.nextDouble(),Engine.rand.nextDouble(),Engine.rand.nextDouble(),0.5}, 1, 1.04);
			double angle = Engine.rand.nextDouble()*Math.PI*2;
			
			double sin = Math.sin(angle); //because speed
			double cos = Math.cos(angle);
			
			a.x = x + sin;
			a.y = y + cos;
			a.dx = dx + cos*10;
			a.dy = dy + sin*10;
			
			a.isBblackHoleParticle();
		}
		
		Helper.keepInside(this, Helper.BOUNCE);
		
		if (!isInert)
			blackHole();
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) {
			dx /= speed;
			dy /= speed;
		}
		selfCol();
		
		speed = Math.sqrt(dx*dx + dy*dy); 
		if (speed != 0 && speed > 1) {
			dx /= speed;
			dy /= speed;
		}
	}
	
	@Override
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
			
			if (dist < h.size*BlackHole.SUCK_RADIUS/2) {
				if (dist < size*h.size/2 && !(this instanceof BlackHole)) {
					amHit(false);
					h.giveObject(distx, disty);
				}
				
				if (dist < h.size*BlackHole.SUCK_RADIUS/3) { //stolen from the particle code, it kind-of orbits them now
					dy -= (h.x - x)*2 + h.dy;
					dx += (h.y - y)*2 + h.dx;
				} else {
					dy -= (h.x - x) + h.dy;
					dx += (h.y - y) + h.dx;
				}
			}
		}
		
		Engine.grid.pullGrid(x,y, BlackHole.SUCK_RADIUS*Math.sqrt(size), 20); //pull on the grid, warning hardcoded
	}
	
	public void selfCol() {
		for (BlackHole s: BlackHole.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				double dist = distX*distX + distY*distY;
				if (dist < (size*size)+(s.size*s.size) && dist != 0) {
					dx -= Helper.sgn(distX)/(2*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(2*Math.sqrt(dist));
				}
				if (dist == 0) { //so they don't stay in each other
					dx += Engine.rand.nextDouble()*2-1;
					dy += Engine.rand.nextDouble()*2-1;
				}
			}
		}
	}

	public void amHit() {
		isInert = false;
		size -= 0.01;
		size -= 1/SIZE_INC;
		if (size < MIN_SIZE) {
			delete(true);
		}
	}
	
	//handles actually dying
	private void delete(boolean wasShot) {
		score = 150 + (5/2)*numCount*(numCount+1); //from the wiki, as you can see its a quadratic
		super.amHit(wasShot);
		ALL_THIS.remove(this);
		
		Engine.grid.pushGrid(x, y, (int)size, 10);
		
		if (wasShot) { 
			//then add score
			
		} else { // or explode
			for (int i = 0; i < 20; i++) {
				HomingButterfly a = new HomingButterfly(0);
				a.x = x+Math.cos(i);
				a.y = y+Math.sin(i);
				a.spawnTimer = 0; //because you shouldn't be close to it anyway
			}
			for (int i = 0; i < 20; i++) {
				HomingCircle s = new HomingCircle(0);
				s.x = x+Math.cos(i);
				s.y = y+Math.sin(i);
				s.spawnTimer = 0;
			}
			int offset = Engine.rand.nextInt(180);			
			for (int i = 0; i < 50; i++) {
				Particle a = new Particle(1, this.colour, 2, Particle.DEFAULT_DRAG); //little bit longer than usual
				a.x = this.x;
				a.y = this.y;
				a.dx = Math.cos(offset+i*360/8)*32*Engine.rand.nextDouble();
				a.dy = Math.cos(offset+i*360/8)*32*Engine.rand.nextDouble();
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.BLACKHOLE].getTextureId());
		if (isInert) { //Its ALWAYS purple when inert..
			colour = Engine.PURPLE;
		} else { //and always red when active...
			colour = Engine.RED;
		}
		super.drawSelf(gl);
	}

}
