package game.objects;
import game.logic.*;

import java.util.ArrayList;

import javax.media.opengl.GL2;


public class ShySquare extends MovingObject {

	public final static ArrayList<ShySquare> ALL_THIS = new ArrayList<ShySquare>();
	
	private static final int MAX_SPEED = 4;
	private float dodgeRange = 6;
	private float dodgeSpeed = 3f;
	
	public ShySquare(double spawnTimer) {
		this(spawnTimer, 0.8, Engine.GREEN);
	}
	
	ShySquare(double spawnTimer, double size, double[] colour) {
		super(size, colour);
		ALL_THIS.add(this);
		score = 100;
		
		this.spawnTimer = spawnTimer;
		SoundEffect.SHOOT.play(10, 0);
	}

	@Override
	public void update(double dt) {
		if (this.spawnTimer > 0) {
			this.spawnTimer -= dt;
			return;
		}
		
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		
		Helper.keepInside(this, Helper.SPLAT);
		
		double[] spos = Engine.getPlayerPos();
		dx += (spos[0]-x)/2;
		dy += (spos[1]-y)/2;

		blackHole();
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised
		}

		ArrayList<GameObject> objects = new ArrayList<GameObject>(PlayerBullet.ALL_THIS);
		for (GameObject o: objects) {
			double distance = (x - o.x)*(x - o.x) + (y - o.y)*(y - o.y);
			if (distance < dodgeRange && distance != 0) {
				dx += (x-o.x)/(distance);
				dy += (y-o.y)/(distance);
			}
		}
		
		speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > dodgeSpeed) {
			dx /= speed;
			dy /= speed;
		}
		
		selfCol();
	}

	public void selfCol() {
		for (ShySquare s: ShySquare.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				double dist = distX*distX + distY*distY + 0.0001;
				if (dist < 0.5*(size*size + s.size*s.size)) {
					dx -= Helper.sgn(distX)/(2+Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(2+Math.sqrt(dist));
				}
			}
		}
	}
	
	public void amHit(boolean isPoints) {
		super.amHit(isPoints);
		ALL_THIS.remove(this);
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.SHY].getTextureId()); //get id of the dot file
		
    	super.drawSelf(gl);
	}

}
