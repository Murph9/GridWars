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

	///////////////////////////////////////////
	@Override
	public void update(double dt) {
		double[] beforePos = before.getPosition();
		double[] dir = new double[] {x-beforePos[0], y - beforePos[1]};
		
		double angle = Math.toDegrees(Math.atan2((beforePos[1]-y), (beforePos[0]-x)));
		double beforeAngle = before.getRotation();
		setRotation((angle+beforeAngle)/2);
		
		double dist = (x-beforePos[0])*(x-beforePos[0]) + (y-beforePos[1])*(y-beforePos[1]);
		
		if (dist > 2*size*size) {
			x = beforePos[0];
			y = beforePos[1];
		} else if (dist > 0.7*size*size) {
			x -= dir[0]*dt*MAX_SPEED;
			y -= dir[1]*dt*MAX_SPEED;
		}
		
		if (after != null) {
			after.update(dt);
		}
}
	
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SNAKEBODY].getTextureId());
		gl.glColor3d(GameEngine.BROWN[0], GameEngine.BROWN[1], GameEngine.BROWN[2]);
		Helper.square(gl);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
