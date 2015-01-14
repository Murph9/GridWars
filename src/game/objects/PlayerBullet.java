package game.objects;
import game.logic.*;
import java.util.ArrayList;

import javax.media.opengl.GL2;


public class PlayerBullet extends MovingObject {

	public final static ArrayList<PlayerBullet> ALL_THIS = new ArrayList<PlayerBullet>();
	
	PlayerBullet() {
		this(0.35, GameEngine.LIGHT_YELLOW);
	}
	
	PlayerBullet(double size, double[] colour) {
		super(size, colour);
		PlayerBullet.ALL_THIS.add(this);
	}
	
	public void update(double dt) {
		x += dx*dt;
		y += dy*dt;

		angle = Math.toDegrees(Math.atan2(dy, dx));
		
		boolean hasCollided = false; //wall collision
		
		//can't use the helper function here );
		if (x > GameEngine.curGame.getBoardWidth()-(size/2)) {
			dx = -dx;
			x = GameEngine.curGame.getBoardWidth()-(size/2);
			hasCollided = true;
		} else if (x < -GameEngine.curGame.getBoardWidth()+(size/2)) {
			dx = -dx;
			x = -GameEngine.curGame.getBoardWidth()+(size/2);
			hasCollided = true;
		}
		
		if (y > GameEngine.curGame.getBoardHeight()-(size/2)) {
			dy = -dy;
			y = GameEngine.curGame.getBoardHeight()-(size/2);
			hasCollided = true;
		} else if (y < -GameEngine.curGame.getBoardHeight()+(size/2)) {
			dy = -dy;
			y = -GameEngine.curGame.getBoardHeight()+(size/2);
			hasCollided = true;
		}

		if (hasCollided && !GameEngine.curGame.ifBouncyShot()) {
			this.amHit(false);
			return;
		}
		
		selfCol();
	}
	
	public void selfCol() {
			//this is the class that handles deleting objects when bullets hit them, 
			//every other class just checks against the same type. This checks EVERYTHING, yes everything
		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
		
		for (GameObject o: objects) {
			if (o instanceof PlayerBullet || o instanceof Player || o instanceof Border || o instanceof Camera || o.equals(GameObject.ROOT) || o instanceof PowerUp || o instanceof Particle) {
				continue; //nothing, can't hit these things
			} else if (o instanceof BlackHole) {
				//special stuff (reflecting off their field)
				BlackHole h = (BlackHole) o;
				double distx = h.x - x;
				double disty = h.y - y;
				double dist = Math.sqrt(distx*distx + disty*disty) + 0.0001;
				
				if (dist < BlackHole.SUCK_RADIUS && !h.isInert()) {
					dx -= distx/(dist*4);
					dy -= disty/(dist*4);
				}
				if (dist < h.size/2) {
					amHit(false);
					h.amHit();
				}
			} else {
				double[] pos = o.getCollisionPosition();
				if (o.getCollisionPosition()[0] == Double.MAX_VALUE) {
					continue;
				}
				double distX = pos[0] - x;
				double distY = pos[1] - y;
				if ((distX*distX) + (distY*distY) < ((size/2)*(size/2) + (o.getSize()/2)*(o.getSize()/2))) {
					if (!GameEngine.curGame.ifSuperShot()) {
						this.amHit(false);
					}
					if (o instanceof SnakeBody || o instanceof Shield) {
					} else {
						o.amHit(true); //gives points
					}
					break;
				}
			}
		}
	}

	public void amHit(boolean isPoints) { //never gives points
		super.amHit(false);
		ALL_THIS.remove(this);
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BULLET].getTextureId());
		
    	if (GameEngine.curGame.ifBouncyShot()) {
    		gl.glColor3d(GameEngine.GREEN[0], GameEngine.GREEN[1], GameEngine.GREEN[2]);
    	}

    	if (GameEngine.curGame.ifSuperShot()) {
    		gl.glColor3d(GameEngine.RED[0], GameEngine.RED[1], GameEngine.RED[2]);
    	}

    	super.drawSelf(gl);
	}
}
