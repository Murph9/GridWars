package game.objects;
import game.logic.*;

import java.util.LinkedList;

import javax.media.opengl.GL2;


public class ShieldedClone extends MovingObject {

	public final static LinkedList<ShieldedClone> ALL_THIS = new LinkedList<ShieldedClone>();
	public static double MAX_SPEED = 6;
	
	private static final int MAX_ANGLE_CHANGE = 180;
	private double lastAngle;
	
	private Shield shield;
	
	public ShieldedClone(double spawnTimer) {
		this(spawnTimer, 1.1, Engine.RED);
	}
	
	ShieldedClone(double spawnTimer, double size, double[] colour) {
		super(size, colour);
		lastAngle = (Engine.rand.nextDouble()*2-1)*180; //random angle between -180 and 180
		
		shield = new Shield(size*1.2, colour); //bigger, get it?
		ALL_THIS.add(this);
		
		score = 100;
		this.spawnTimer = spawnTimer;
		SoundEffect.SHOOT.play(10, 0);
	}

	@Override
	public void update(double dt) {
		if (this.spawnTimer > 0) {
			this.spawnTimer -= dt;
			shield.spawnTimer = 0;
			shield.x = x+0.8;
			shield.y = y; //position is hardcoded to the right of the clone
			return;
		}
		
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		shield.x = x+Math.cos(Math.toRadians(lastAngle))*0.8;
		shield.y = y+Math.sin(Math.toRadians(lastAngle))*0.8;

		//now bounces off walls
		Helper.keepInside(this, Helper.BOUNCE);
		
		moveToPlayer(dt);
		blackHole(); //note the order is reversed here.
		selfCol();

		double speed = Math.sqrt(dx*dx + dy*dy);
		speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) {
			dx /= speed;
			dy /= speed;
		}
		
	}
	
	public void selfCol() {
		for (ShieldedClone s: ShieldedClone.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				double dist = (distX*distX) + (distY*distY) + 0.001; //yeah...
				if (dist < 0.5*(size*size)+(s.size*s.size)) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}

	private void moveToPlayer(double dt) {
		//do rotation stuff
		double[] playerPos = Engine.getPlayerPos();
		double angleToPlayer = Math.toDegrees(Math.atan2((playerPos[1]-y), (playerPos[0]-x)));

		
		//TODO still kind of turns the wrong way sometimes, LOOK AT SNAKE BODY ANGLE CODE
		
		lastAngle = lastAngle % 360;
		
		double absDiff = Math.abs(angleToPlayer - lastAngle + 360) % 360; //find the difference (positive)
		double normDiff = absDiff > 180 ? 360 - absDiff : absDiff; //normDiff is now between 0 and 180
		if (normDiff > MAX_ANGLE_CHANGE*dt) { //if the absolute difference is greater than MAX_ANGLE_CHANGE*dt
			if (absDiff > 180) {
				lastAngle -= MAX_ANGLE_CHANGE*dt;
			} else {
				lastAngle += MAX_ANGLE_CHANGE*dt;
			}
			
			dx /= 1.05; //simulate drag if not trying to move forward
			dy /= 1.05;

		} else { //normal works so far
			if (normDiff < MAX_ANGLE_CHANGE/2) {//move forward because you are close to look at player
				dx = Math.cos(Math.toRadians(lastAngle));
				dy = Math.sin(Math.toRadians(lastAngle));
			} else {
				lastAngle = angleToPlayer;
			}
		}
		angle = lastAngle;
		shield.angle = lastAngle;
		
		if (normDiff > MAX_ANGLE_CHANGE*1.5*dt) { //decide whether shield is on or off
			shield.setIfActive(false);
		} else {
			shield.setIfActive(true);
		}
		
	}
	
	public void amHit(boolean ifPoints) {
		shield.amHit(false); //never has any points
		super.amHit(ifPoints);
		ALL_THIS.remove(this);
	}
	
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.PLAYER].getTextureId());
		
		super.drawSelf(gl);
	}

}
