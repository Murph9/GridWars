import javax.media.opengl.GL2;


public class Shield extends MovingObject {

	private boolean isProtecting;
	
	Shield(double size, double[] colour) {
		super(size, colour);
		isProtecting = true;
	}

	@Override
	public void update(double dt) {
		//not sure yet, actually not sure it has to do anything
	}
	
	public double[] getCollisionPosition() { //if its
		if (isProtecting) {
			return getPosition();
		} else {
			return new double[]{Double.MAX_VALUE,Double.MAX_VALUE}; 
				//ok it will break if you have a playing field that big, so don't.
		}
	}
	
	public boolean getActive() { return isProtecting; }
	public void setIfActive(boolean in) { 
		isProtecting = in;
	}
	
	public void drawSelf(GL2 gl) {
		if (!isProtecting) return; //doesn't work if not protecting
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SHIELD].getTextureId());
		
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
}
