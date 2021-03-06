package game.objects;
import game.logic.*;
import javax.media.opengl.GL2;


public class SnakeBody extends MovingObject implements SnakeObject {

	public static final int score = 0;
	private boolean waitaFrame;
	
	private SnakeObject before;
	private SnakeObject after; //if next == end then this == tail
	
	//this is kind of recursive (with length reducing each time)
	SnakeBody(double size, double[] colour, SnakeObject before, int length) {
		super(size, Engine.BROWN);
		this.before = before;
		
		if (length > 0) {
			after = new SnakeBody(size/1.038, colour, this, length-1);
		} else {
			after = null;
		}
		x = Integer.MAX_VALUE;
		y = Integer.MAX_VALUE;
		spawnTimer = 0;
		waitaFrame = false;
	}
	
	public void amHit(boolean isPoints) {
		super.amHit(false); //never gives points
		if (after != null) {
			after.iDied();
		}
	}
	
	@Override
	public SnakeObject getBefore() { return before; }
	@Override
	public SnakeObject getAfter() { return after; }

	///////////////////////////////////////////
	@Override
	public void update(double dt) {
		if (before == null) {
			if (waitaFrame) {
				amHit(false);
			} else {
				waitaFrame = true;
			}
			return;
		}
		
		spawnTimer = before.getSpawnTimer();
		
		double[] beforePos = before.getPosition();
		double[] dir = new double[] {x-beforePos[0], y - beforePos[1]};
		
		double angle = Math.toDegrees(Math.atan2((beforePos[1]-y), (beforePos[0]-x)));
		double beforeAngle = before.getRotation();
		if (angle > beforeAngle +180) { //if on the other side of the spectrum make it closer
			beforeAngle += 360;
		}
		if (beforeAngle > angle +180) {
			angle += 360;
		}
		angle = (angle+beforeAngle)/2;
		
		double dist = (dir[0])*(dir[0]) + (dir[1])*(dir[1]);
		
		if (dist > 16*size*size) {
			x = beforePos[0];
			y = beforePos[1];
		} else if (dist > 0.7*size*size) {
			x -= dir[0]*dt*Math.sqrt(dist)*MAX_SPEED;
			y -= dir[1]*dt*Math.sqrt(dist)*MAX_SPEED;
		}
		
		if (after != null) {
			after.update(dt);
		}
	}
	
	public void selfCol() {
		//nothing, snakes don't really have a problem with each other
	}
	
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.SNAKEBODY].getTextureId());
		super.drawSelf(gl);
	}

	@Override
	public double getSpawnTimer() {
		return spawnTimer;
	}

	@Override
	public void iDied() {
		before = null;
	}
}
