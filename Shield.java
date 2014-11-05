import javax.media.opengl.GL2;

public class Shield extends MovingObject {

	public static final int score = 0;
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
		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
