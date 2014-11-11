import java.util.LinkedList;

import javax.media.opengl.GL2;


public class ShieldedClone extends MovingObject {

	public final static LinkedList<ShieldedClone> ALL_THIS = new LinkedList<ShieldedClone>();
	
	public static final int score = 100;
	private static final int MAX_ANGLE_CHANGE = 120;
	private double lastAngle;
	private static int MAX_SPEED = 7;
	
	private Shield shield;
	
	ShieldedClone(double size, double[] colour) {
		super(size*0.9, colour);
		lastAngle = 0;
		
		shield = new Shield(size, colour);
		ALL_THIS.add(this);
	}

	@Override
	public void update(double dt) {
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
		if (speed != 0 && speed > MAX_SPEED) {
			dx /= speed;
			dy /= speed;
		}
		
	}
	
	public void selfCol() {
		for (ShieldedClone s: ShieldedClone.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				if ((distX*distX) + (distY*distY) < (size*size)+(s.size*s.size)) {
					dx -= Helper.sgn(distX)/2;
					dy -= Helper.sgn(distY)/2;
				}
			}
		}
	}
	
	private void moveToPlayer(double dt) {
		//do rotation stuff
		double[] playerPos = GameEngine.getPlayerPos();
		double angleToPlayer = Math.toDegrees(Math.atan2((playerPos[1]-y), (playerPos[0]-x)));

		lastAngle = lastAngle % 360;
		
		double absDiff = Math.abs(angleToPlayer - lastAngle + 360) % 360; //find the difference (positive)
		double normDiff = absDiff > 180 ? 360 - absDiff : absDiff; //normDiff is now between 0 and 180
		if (normDiff > MAX_ANGLE_CHANGE*dt) { //if the absolute difference is greater than MAX_ANGLE_CHANGE*dt
			if (absDiff > 180) {
				lastAngle -= MAX_ANGLE_CHANGE*dt;
			} else {
				lastAngle += MAX_ANGLE_CHANGE*dt;
			}
			
			dx /= 1.02; //simulate drag if not trying to move forward
			dy /= 1.02;

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
	
	public void destroy() {
		shield.destroy();
		super.destroy();
		ALL_THIS.remove(this);
		GameEngine.score.addScore(score);
	}
	
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.PLAYER].getTextureId());
		
		gl.glColor3d(colour[0], colour[1], colour[2]);

		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

}
