import javax.media.opengl.GL2;


public class SplitingSquare extends HomingObject {
	
	public static final int HOME_SPEED = 3;
	private int ORBIT_SPEED = 6;
	
	private boolean hasSplit;
	private double orbitAngle;
	private double orbitRadius;
		//remember this object type rotates about the same point
		//and this point moves towards the player
	private boolean isPosOrbit;
		//rotation direction
	
	/**if the size is 1 is a 'parent square', else its a 'child square'
	 * The angle is the intial angle the square starts the rotation about the point
	 * And the radius is how big of a circle it rotates about the centre
	 */
	SplitingSquare(double size, double[] colour, double angle, double radius, boolean rotDirection) {
		super(size, colour, SplitingSquare.HOME_SPEED);
		hasSplit = false;
		orbitAngle = angle;
		orbitRadius = radius;
		isPosOrbit = rotDirection;
	}
	
	public double getOrbitAngle() {
		return orbitAngle;
	}
	public void setSplitStatus() {
		hasSplit = true;
	}
	
	public double[] getCollisionPosition() { //because this object rotates about a point that moves
		if (hasSplit) {
			double[] temp = getPosition();
			temp[0] = temp[0] + Math.cos(orbitAngle)*orbitRadius;
			temp[1] = temp[1] + Math.sin(orbitAngle)*orbitRadius;
			return temp;
		} else {
			return super.getPosition();
		}
	}
	

	public void destroy() {
		super.destroy();
		if (!hasSplit) {
			double[] t = this.getCollisionPosition();
			double angle = this.getOrbitAngle();
			
			SplitingSquare sA = new SplitingSquare(0.75, GameEngine.RED, angle, 0.7, false);
			sA.setPosition(new double[] {t[0]+Math.cos(angle+60)*0.7, t[1]+Math.sin(angle+60)*0.7});
			sA.hasSplit = true;
			
			SplitingSquare sB = new SplitingSquare(0.75, GameEngine.RED, angle, 0.7, true);
			sB.setPosition(new double[] {t[0]+Math.cos(angle-30)*0.7, t[1]+Math.sin(angle-30)*0.7});
			sB.hasSplit = true;
			
			SplitingSquare sC = new SplitingSquare(0.75, GameEngine.RED, angle, 0.7, true);
			sC.setPosition(new double[] {t[0]+Math.cos(angle-120)*0.7, t[1]+Math.sin(angle-120)*0.7});
			sC.hasSplit = true;
		}
	}
	
	public void update(double dt) {
		super.update(dt);
		if (hasSplit) {
			if (isPosOrbit) {
				orbitAngle += dt*ORBIT_SPEED;
			} else {
				orbitAngle -= dt*ORBIT_SPEED;
			}
		}
	}

	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SQUARE].getTextureId());
		
		if (hasSplit) {
			gl.glTranslated(Math.cos(orbitAngle)*orbitRadius, Math.sin(orbitAngle)*orbitRadius, 0);
		}
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
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

	@Override
	public int score() {
		if (hasSplit) { //is small
			return 100;
		} else { //is normal
			return 50;
		}
	}
}
