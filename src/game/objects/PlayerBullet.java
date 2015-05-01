package game.objects;
import game.logic.*;
import java.util.ArrayList;

import javax.media.opengl.GL2;


//TODO bullet blackhole stuff not working

public class PlayerBullet extends MovingObject {

	public final static ArrayList<PlayerBullet> ALL_THIS = new ArrayList<PlayerBullet>();
	
	PlayerBullet() {
		this(0.35, Engine.LIGHT_YELLOW);
	}
	
	PlayerBullet(double size, double[] colour) {
		super(size, colour);
		PlayerBullet.ALL_THIS.add(this);
		this.spawnTimer = 0; //no spawn delay
	}
	
	public void update(double dt) {
		x += dx*dt;
		y += dy*dt;

		angle = Math.toDegrees(Math.atan2(dy, dx));
		
		boolean hasCollided = false; //wall collision
		
		//can't use the helper function here );
		if (x > Engine.settings.getBoardWidth()-(size/2)) {
			dx = -dx;
			x = Engine.settings.getBoardWidth()-(size/2);
			hasCollided = true;
		} else if (x < -Engine.settings.getBoardWidth()+(size/2)) {
			dx = -dx;
			x = -Engine.settings.getBoardWidth()+(size/2);
			hasCollided = true;
		}
		
		if (y > Engine.settings.getBoardHeight()-(size/2)) {
			dy = -dy;
			y = Engine.settings.getBoardHeight()-(size/2);
			hasCollided = true;
		} else if (y < -Engine.settings.getBoardHeight()+(size/2)) {
			dy = -dy;
			y = -Engine.settings.getBoardHeight()+(size/2);
			hasCollided = true;
		}

		if (hasCollided && !Engine.gameState.ifBouncyShot()) {
			this.amHit(false);
			return;
		} else {
			Engine.grid.pushGrid(x+dx*0.04, y+dy*0.04, 0.75, 3);
		}
		
		selfCol();
	}
	
	public void selfCol() {
			//this is the class that handles deleting objects when bullets hit them, 
			//every other class just checks against the same type. This checks EVERYTHING, yes everything
		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
		
		for (GameObject o: objects) {
			if (o instanceof PlayerBullet || o instanceof Player || o instanceof Border || o instanceof Camera || o instanceof PowerUp || o instanceof Particle) {
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
					if (!Engine.gameState.ifSuperShot()) {
						this.amHit(false);
					}
					h.amHit();
				}
				
			} else {
				double[] pos = o.getCollisionPosition();
				if (pos[0] == Double.MAX_VALUE || pos[1] == Double.MAX_VALUE) { //the object doesn't want to be found
					continue;
				}
				double distX = pos[0] - x;
				double distY = pos[1] - y;
				if ((distX*distX) + (distY*distY) < ((size/2)*(size/2) + (o.size/2)*(o.size/2))) {
					if (!Engine.gameState.ifSuperShot()) {
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
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.BULLET].getTextureId());
		
    	if (Engine.gameState.ifBouncyShot()) {
    		this.colour = Engine.GREEN;
    	}

    	if (Engine.gameState.ifSuperShot()) {
    		this.colour = Engine.RED;
    	}

    	super.drawSelf(gl);
	}
}
