import javax.media.opengl.GL2;


public class ShieldedClone extends MovingObject {

	private static final int MAX_ANGLE_CHANGE = 120;
	private double lastAngle;
	private static int MAX_SPEED = 11;
	
	private Shield shield;
	
	ShieldedClone(double size, double[] colour) {
		super(size*0.9, colour);
		lastAngle = 0;
		
		shield = new Shield(size, colour);
	}

	@Override
	public void update(double dt) {
		double[] pos = getPosition();
		
		//do rotation stuff
		double[] playerPos = GameEngine.getPlayerPos();
		double angleToPlayer = Math.toDegrees(Math.atan2((playerPos[1]-pos[1]), (playerPos[0]-pos[0])));

		lastAngle = lastAngle % 360;
		
		double absDiff = Math.abs(angleToPlayer - lastAngle + 360) % 360; //find the difference (positive)
		double normDiff = absDiff > 180 ? 360 - absDiff : absDiff; //normDiff is now between 0 and 180
		
		if (normDiff > MAX_ANGLE_CHANGE*dt) { //if the absolute difference is greater than MAX_ANGLE_CHANGE*dt
			if (absDiff > 180) {
				lastAngle -= MAX_ANGLE_CHANGE*dt;
			} else {
				lastAngle += MAX_ANGLE_CHANGE*dt;
			}
			
			mySpeedX /= 1.015; //simulate drag if not trying to move forward
			mySpeedY /= 1.015;

		} else { //normal works so far
			if (normDiff < MAX_ANGLE_CHANGE/2) {//move forward because you are close
				mySpeedX = Math.cos(Math.toRadians(lastAngle));
				mySpeedY = Math.sin(Math.toRadians(lastAngle));
			} else {
				lastAngle = angleToPlayer;
			}
		}

		
		if (normDiff > MAX_ANGLE_CHANGE*1.5*dt) { //decide whether shield is on or off
			shield.setIfActive(false);
		} else {
			shield.setIfActive(true);
		}
		
		setRotation(lastAngle);
		shield.setRotation(lastAngle);
		
		//bounce off walls now
		if (pos[0] > GameEngine.boardWidth-(size/2)) { 
			mySpeedX = -mySpeedX;
			pos[0] = GameEngine.boardWidth-(size/2);
		} else if (pos[0] < -GameEngine.boardWidth+(size/2)) {
			mySpeedX = -mySpeedX;
			pos[0] = -GameEngine.boardWidth+(size/2);
		}
		
		if (pos[1] > GameEngine.boardHeight-(size/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = GameEngine.boardHeight-(size/2);
		} else if (pos[1] < -GameEngine.boardHeight+(size/2)) {
			mySpeedY = -mySpeedY;
			pos[1] = -GameEngine.boardHeight+(size/2);
		}
		
		pos[0] += mySpeedX*dt*MAX_SPEED;
		pos[1] += mySpeedY*dt*MAX_SPEED;
		setPosition(pos);
		
		double[] shieldPos = new double[]{pos[0]+Math.cos(Math.toRadians(lastAngle))*0.8, pos[1]+Math.sin(Math.toRadians(lastAngle))*0.8};
		shield.setPosition(shieldPos);
	}
	
	
	public void destroy() {
		shield.destroy();
		super.destroy();
	}
	
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.PLAYER].getTextureId());
		
		gl.glColor3d(colour[0], colour[1], colour[2]);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(0, 0);
			gl.glVertex2d(-size/2, -size/2);
			gl.glTexCoord2d(1, 0);
			gl.glVertex2d(size/2, -size/2);
			gl.glTexCoord2d(1, 1);
			gl.glVertex2d(size/2, size/2);
			gl.glTexCoord2d(0, 1);
			gl.glVertex2d(-size/2, size/2);
		gl.glEnd();
		
	}

	@Override
	public int score() {
		return 100;
	}

}
