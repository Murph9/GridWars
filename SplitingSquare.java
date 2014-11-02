import javax.media.opengl.GL2;


public class SplitingSquare extends HomingObject {
	
	public static final int HOME_SPEED = 3;
	private int ORBIT_SPEED = 3;
	
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
		orbitAngle = angle;
		orbitRadius = radius;
		isPosOrbit = rotDirection;
	}
	
	public double getOrbitAngle() {
		return orbitAngle;
	}
	
	public double[] getCollisionPosition() { //because this object rotates about a point that moves
		double[] temp = getPosition();
		temp[0] = temp[0] + Math.cos(orbitAngle)*orbitRadius;
		temp[1] = temp[1] + Math.sin(orbitAngle)*orbitRadius;
		return temp;
	}
	
	public void update(double dt) {
		super.update(dt);
		if (isPosOrbit) {
			orbitAngle += dt*ORBIT_SPEED;
		} else {
			orbitAngle -= dt*ORBIT_SPEED;
		}
	}

	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SQUARE].getTextureId());
		
    	gl.glTranslated(Math.cos(orbitAngle)*orbitRadius, Math.sin(orbitAngle)*orbitRadius, 0);
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
}
