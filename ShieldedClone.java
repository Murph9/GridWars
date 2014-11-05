import java.util.LinkedList;

import javax.media.opengl.GL2;


public class ShieldedClone extends MovingObject {

	public final static LinkedList<ShieldedClone> ALL_THIS = new LinkedList<ShieldedClone>();
	
	public static final int score = 100;
	private static final int MAX_ANGLE_CHANGE = 120;
	private double lastAngle;
	private static int MAX_SPEED = 11;
	
	private Shield shield;
	
	ShieldedClone(double size, double[] colour) {
		super(size*0.9, colour);
		lastAngle = 0;
		
		shield = new Shield(size, colour);
		ALL_THIS.add(this);
	}

	@Override
	public void update(double dt) {
		
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

		if (normDiff > MAX_ANGLE_CHANGE*1.5*dt) { //decide whether shield is on or off
			shield.setIfActive(false);
		} else {
			shield.setIfActive(true);
		}
		
		angle = lastAngle;
		shield.angle = lastAngle;
		
		//bounce off walls now
		if (x > GameEngine.boardWidth-(size/2)) { 
			dx = -dx;
			x = GameEngine.boardWidth-(size/2);
		} else if (x < -GameEngine.boardWidth+(size/2)) {
			dx = -dx;
			x = -GameEngine.boardWidth+(size/2);
		}
		
		if (y > GameEngine.boardHeight-(size/2)) {
			dy = -dy;
			y = GameEngine.boardHeight-(size/2);
		} else if (y < -GameEngine.boardHeight+(size/2)) {
			dy = -dy;
			y = -GameEngine.boardHeight+(size/2);
		}
		
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		shield.x = x+Math.cos(Math.toRadians(lastAngle))*0.8;
		shield.y = y+Math.sin(Math.toRadians(lastAngle))*0.8;
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
