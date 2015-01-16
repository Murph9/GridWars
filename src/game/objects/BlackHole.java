package game.objects;

import game.logic.*;

import java.util.ArrayList;

import javax.media.opengl.GL2;

//now instead of the blackhole class handling sucking everything in, they handle it themselves
	//but this does require that all objects have a blackhole method ...
public class BlackHole extends MovingObject {

	public static final ArrayList<BlackHole> ALL_THIS = new ArrayList<BlackHole>();
	public static final int SUCK_RADIUS = 6;
	
	public static final int MAX_SPEED = 1; //yeah kinda slow
	
	private boolean isInert;
	private int numCount; //number of objects consumed
				//as in NumNumNumNum [aggressively eating]
	
	private int maxNum; //if hit points hits this number it will explode
	private int hitPoints; //hits it will take to destroy, increases on nums
	
	public BlackHole() {
		this(1, GameEngine.RED);
	}
	
	BlackHole(double size, double[] colour) {
		super(size, colour); //this colour is ignored in the draw method
		isInert = true;
		numCount = 0;
		maxNum = 60;
		hitPoints = 10; //starts with hit points of 10 shots to kill
		ALL_THIS.add(this);
		
		score = 150; //default score value 
	}
	
	public void giveObject(double x, double y) {
		dx += x/6; //objects give it speed
		dy += y/6;
		numCount++;
		hitPoints += 2; //because it would be too easy on 1
		size += 0.025; //so it can grow
		if (hitPoints >= maxNum) { //if its to big, explode
			delete(false); //explode
		}
	}
	
	public void shotAt() { isInert = false; }
	public boolean isInert() { return isInert; }
	
	@Override
	public void update(double dt) {
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		
		//particle effects yay
		double rand = GameEngine.rand.nextDouble();
		if (!isInert && rand < ((float)numCount/(float)maxNum)) {
			Particle a = new Particle(3, new double[]{GameEngine.rand.nextDouble(),GameEngine.rand.nextDouble(),GameEngine.rand.nextDouble(),0.5}, 1, 1.04);
			double angle = GameEngine.rand.nextDouble()*Math.PI;
			
			a.x = x + Math.sin(angle);
			a.y = y + Math.cos(angle);
			a.dx = Math.cos(angle)*10 + dx;
			a.dy = Math.sin(angle)*10 + dy;
			
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
			dx = dx/speed;
			dy = dy/speed;
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
				
				if (dist < h.size*BlackHole.SUCK_RADIUS/3) { //stolen from the particle code, it kind-of orbits
					dy -= (h.x - x)*2 + h.dy;
					dx += (h.y - y)*2 + h.dx;
				} else {
					dy -= (h.x - x) + h.dy;
					dx += (h.y - y) + h.dx;
				}
			}
		}
	}
	
	public void selfCol() {
		for (BlackHole s: BlackHole.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				double dist = distX*distX + distY*distY + 0.0001;
				if (dist < (size*size)+(s.size*s.size)) {
					dx -= Helper.sgn(distX)/(2*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(2*Math.sqrt(dist));
				}
			}
		}
	}

	//function handles destroying things into itself
	public void amHit() {
		isInert = false;
		hitPoints--;
		size -= 0.01;
		if (hitPoints <= 0) {
			delete(true); //as in shot
		}
	}
	
	//handles actually dying
	private void delete(boolean wasShot) {
		score = 150 + (5/2)*numCount*(numCount+1); //sneaky... (i only wanted to calculate it once)
		super.amHit(wasShot);
		ALL_THIS.remove(this);
		
		if (wasShot) { //then add score
		} else { // or explode
			for (int i = 0; i < 20; i++) {
				HomingButterfly a = new HomingButterfly(0.6, GameEngine.BLUE);
				a.setPosition(new double[] {x+Math.cos(i),y+Math.sin(i)});
			}
			for (int i = 0; i < 20; i++) {
				HomingSeeker s = new HomingSeeker(0.6, GameEngine.PURPLE);
				s.setPosition(new double[] {x+Math.cos(i),y+Math.sin(i)});
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BLACKHOLE].getTextureId());
		if (isInert) { //Its ALLWAYS purple when inert..
			colour = GameEngine.PURPLE;
		} else {
			colour = GameEngine.RED;
		}
		super.drawSelf(gl);
	}

}
