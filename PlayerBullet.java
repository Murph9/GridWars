import java.util.ArrayList;

import javax.media.opengl.GL2;


public class PlayerBullet extends MovingObject {

	public final static ArrayList<PlayerBullet> ALL_THIS = new ArrayList<PlayerBullet>();
	
	PlayerBullet(double size, double[] colour) {
		super(size, colour);
		PlayerBullet.ALL_THIS.add(this);
	}
	
	public void update(double dt) {
		x += dx*dt;
		y += dy*dt;

		boolean hasCollided = false;
		
		//can't use the helper function here );
		if (x > GameEngine.boardWidth-(size/2)) {
			dx = -dx;
			x = GameEngine.boardWidth-(size/2);
			hasCollided = true;
		} else if (x < -GameEngine.boardWidth+(size/2)) {
			dx = -dx;
			x = -GameEngine.boardWidth+(size/2);
			hasCollided = true;
		}
		
		if (y > GameEngine.boardHeight-(size/2)) {
			dy = -dy;
			y = GameEngine.boardHeight-(size/2);
			hasCollided = true;
		} else if (y < -GameEngine.boardHeight+(size/2)) {
			dy = -dy;
			y = -GameEngine.boardHeight+(size/2);
			hasCollided = true;
		}

		if (hasCollided) {
			this.destroy();
			return;
		}
		
		selfCol();
	}
	
	public void selfCol() {
			//this is the class that handles deleting objects when bullets hit them, 
			//every other class just checks against the same type. This checks EVERYTHING, yes everything
		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
		
		for (GameObject o: objects) {
			if (o instanceof PlayerBullet || o instanceof Player || o instanceof Border || o instanceof Camera || o.equals(GameObject.ROOT)) {
				continue; //nothing, can't hit these things
			} else if (o instanceof BlackHole) {
				//special stuff (reflecting off their field)
				BlackHole h = (BlackHole) o;
				double distx = h.x - x;
				double disty = h.y - y;
				double dist = Math.sqrt(distx*distx + disty*disty);
				
				if (dist < BlackHole.SUCK_RADIUS/2 && !h.isInert()) {
					dx -= (h.size*8-dist)*0.05*distx/dist;
					dy -= (h.size*8-dist)*0.05*disty/dist;
				}
				if (dist < h.size/2) {
					destroy();
					h.destroy();
				}
			} else {
				double[] pos = o.getCollisionPosition();
				double distX = pos[0] - x;
				double distY = pos[1] - y;
				if ((distX*distX) + (distY*distY) < ((size*size)/2 + (o.size*o.size)/2)) {
					this.destroy();
					if (o instanceof SnakeBody || o instanceof Shield) {
					} else {
						o.destroy();
					}
					break;
				}
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BULLET].getTextureId());
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);
    	Helper.square(gl);
		
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
	public void destroy() {
		super.destroy();
		ALL_THIS.remove(this);
	}
}
