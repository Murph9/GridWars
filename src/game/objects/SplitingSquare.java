package game.objects;
import game.logic.*;

import java.util.ArrayList;

import javax.media.opengl.GL2;


public class SplitingSquare extends HomingObject {
	
	public final static ArrayList<SplitingSquare> ALL_THIS = new ArrayList<SplitingSquare>();
	public static double MAX_SPEED = 3;
	
	private int ORBIT_SPEED = 6;
	
	private boolean hasSplit;
	private double orbitAngle;
	private double orbitRadius;
		//remember this object type rotates about the same point
		//and this point moves towards the player
	private boolean isPosOrbit;
		//rotation direction
	
	public SplitingSquare(double spawnTimer) {
		this(spawnTimer, 1, Engine.RED, 0, 1, true);
	}
	
	private SplitingSquare(double x, double y, double angle, boolean rotDirection, double offset) {
		super(0.7, Engine.RED, SplitingSquare.MAX_SPEED);
		
		orbitAngle = angle;
		orbitRadius = 0.8;
		isPosOrbit = rotDirection;
		ALL_THIS.add(this);
		
		hasSplit = true;
		score = 100;
		this.x = x+Math.cos(angle+offset)*0.8;
		this.y = y+Math.sin(angle+offset)*0.8;
		
		//needs no sound effect as its the child spawn
	}
	
	/**if the size is 1 is a 'parent square', else its a 'child square'
	 * The angle is the intial angle the square starts the rotation about the point
	 * And the radius is how big of a circle it rotates about the centre
	 */
	SplitingSquare(double spawnTimer, double size, double[] colour, double angle, double radius, boolean rotDirection) {
		super(size, colour, SplitingSquare.MAX_SPEED);
		hasSplit = false;
		orbitAngle = angle;
		orbitRadius = radius;
		isPosOrbit = rotDirection;
		ALL_THIS.add(this);
		
		this.spawnTimer = spawnTimer;
		score = 50; //starts big then changes score to small when hit once
//		SoundEffect.SHOOT.play(10, 0);
		new SoundEffect(SoundEffect.SPAWN, 10 ,0).start();
	}
	
	public double getOrbitAngle() {
		return orbitAngle;
	}
	public void setSplitStatus() {
		hasSplit = true;
		score = 100;
	}
	
	public double[] getCollisionPosition() { //because this object rotates about a point that moves
		if (hasSplit) {
			double[] temp = new double[2];
			temp[0] = x + Math.cos(orbitAngle)*orbitRadius;
			temp[1] = y + Math.sin(orbitAngle)*orbitRadius;
			return temp;
		} else {
			return super.getPosition();
		}
	}
	

	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		if (!hasSplit) {
			double angle = this.getOrbitAngle();
			
			new SplitingSquare(x, y, angle, false, 60);
			new SplitingSquare(x, y, angle, true, -30);
			new SplitingSquare(x, y, angle, true, -120);
		}
		ALL_THIS.remove(this);
	}
	
	public void update(double dt) {
		super.update(dt);
		selfCol();
		
		if (hasSplit) {
			if (isPosOrbit) {
				orbitAngle += dt*ORBIT_SPEED;
			} else {
				orbitAngle -= dt*ORBIT_SPEED;
			}
		}
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised
		}
	}
	
	public void selfCol() {
		for (SplitingSquare s: SplitingSquare.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				double dist = distX*distX + distY*distY + 0.0001;
				if (dist < (size*size + s.size*s.size)) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}

	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.SQUARE].getTextureId());
		
		if (hasSplit) {
			gl.glTranslated(Math.cos(orbitAngle)*orbitRadius, Math.sin(orbitAngle)*orbitRadius, 0);
		}
		
    	super.drawSelf(gl);
	}
}
