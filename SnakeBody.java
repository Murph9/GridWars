import javax.media.opengl.GL2;


public class SnakeBody extends MovingObject implements SnakeObject {

	public static final int score = 0;
	
	private SnakeObject before;
	private SnakeObject after; //if next == end then this == tail
	
	//this is kind of recursive (with length reducing each time)
	SnakeBody(double size, double[] colour, SnakeObject before, int length) {
		super(size, colour);
		this.before = before;
		
		if (length > 0) {
			after = new SnakeBody(size/1.038, colour, this, length-1);
		} else {
			after = null;
		}
	}
	
	public void destroy() {
		super.destroy();
		if (after != null) {
			after.destroy();
		}
	}
	
	@Override
	public SnakeObject getBefore() { return before; }
	@Override
	public SnakeObject getAfter() { return after; }

	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SNAKEBODY].getTextureId());
		gl.glColor3d(GameEngine.BROWN[0], GameEngine.BROWN[1], GameEngine.BROWN[2]);
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(0, 0);
			gl.glVertex2d(-0.5, -0.5);
			gl.glTexCoord2d(1, 0);
			gl.glVertex2d(0.5, -0.5);
			gl.glTexCoord2d(1, 1);
			gl.glVertex2d(0.5, 0.5);
			gl.glTexCoord2d(0, 1);
			gl.glVertex2d(-0.5, 0.5);
		gl.glEnd();
	}
	
	
	///////////////////////////////////////////
	@Override
	public void update(double dt) {
		double[] pos = this.getPosition();
		double[] beforePos = before.getPosition();
		double[] dir = new double[] {pos[0]-beforePos[0], pos[1] - beforePos[1]};

		double angle = Math.toDegrees(Math.atan2((beforePos[1]-pos[1]), (beforePos[0]-pos[0])));
		double beforeAngle = before.getRotation();
		setRotation((angle+beforeAngle)/2);
		
		double dist = (pos[0]-beforePos[0])*(pos[0]-beforePos[0]) + (pos[1]-beforePos[1])*(pos[1]-beforePos[1]);
		
		if (dist > 2*mySize*mySize) {
			pos = beforePos;
		} else if (dist > 0.7*mySize*mySize) {
			pos[0] -= dir[0]*dt*MAX_SPEED;
			pos[1] -= dir[1]*dt*MAX_SPEED;
			setPosition(pos);
		}
		setPosition(pos);
		
		if (after != null) {
			after.update(dt);
		}
	}

}
