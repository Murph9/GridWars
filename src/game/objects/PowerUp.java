package game.objects;
import java.util.ArrayList;

import game.logic.*;

import javax.media.opengl.GL2;


/**The name should be a good hint
 * @author Jake Murphy
 */
public class PowerUp extends MovingObject {

	public final static ArrayList<PowerUp> ALL_THIS = new ArrayList<PowerUp>();
	public final static int OBJECT_TIMEOUT = 10; //seconds 
	
	private double decayTimer;
	private int type;
	
	public PowerUp (int type) {
		super(1, Engine.WHITE);
		
		this.type = type;
		this.score = 0;
		this.decayTimer = OBJECT_TIMEOUT;
		
		ALL_THIS.add(this);
		
		SoundEffect.SHOOT.play(10, 0);
	}
	
	public void update(double dt) {
		if (spawnTimer > 0) {
			spawnTimer -= dt;
			return;
		}
		decayTimer -= dt;
		if (decayTimer < 0) {
			amHit(false); //just disappear
		}
		
		x += dx*dt;
		y += dy*dt;
		
		Helper.keepInside(this, Helper.BOUNCE);
		
		selfCol();
		blackHole();
		
		double speed = dx*dx + dy*dy;
		if (speed != 0 && speed > 1) {
			dx /= speed;
			dy /= speed;
		}
	}

	public void selfCol() {
		for (PowerUp p : PowerUp.ALL_THIS) {
			if (!p.equals(this)) { //because that would be silly
				double distX = p.x - x;
				double distY = p.y - y;
				double dist = distX*distX + distY*distY + 0.0001;
				if (dist < (size*size + p.size*p.size)) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}
	
	//ifpoints = if player hit it
	public void amHit(boolean ifPoints) {
		super.amHit(false); //never gives points
		ALL_THIS.remove(this);
		if (ifPoints) {
			used();
		}
	}
	
	public void used() {
		super.amHit(false);
		
		switch (type) {
		case Engine.EXTRA_SPEED:
			Engine.gameState.incBulletSpeed();	break;
		case Engine.EXTRA_BULLET:
			Engine.gameState.incBulletCount();	break;
		case Engine.EXTRA_BOMB:
			Engine.gameState.incBombCount();	break;
		case Engine.EXTRA_LIFE:
			Engine.gameState.incLives();		break;
		case Engine.SIDE_SHOT:
			Engine.gameState.gotSideShot();		break;
		case Engine.REAR_SHOT:
			Engine.gameState.gotRearShot();		break;
		case Engine.TEMP_SHIELD:
			Engine.gameState.gotShield();		break;
		case Engine.SUPER_SHOT:
			Engine.gameState.gotSuperShot();	break;
		case Engine.BOUNCY_SHOT:
			Engine.gameState.gotBouncyShot();	break;
		}
	}
	
	public void drawSelf(GL2 gl) {
		switch (type) {
		case Engine.EXTRA_SPEED:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.EXTRA_SPEED].getTextureId()); 	break;
		case Engine.EXTRA_BULLET:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.EXTRA_BULLET].getTextureId()); 	break;
		case Engine.EXTRA_BOMB:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.EXTRA_BOMB].getTextureId()); 	break;
		case Engine.EXTRA_LIFE:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.EXTRA_LIFE].getTextureId()); 	break;
		case Engine.SIDE_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.SIDE_SHOT].getTextureId()); 		break;
		case Engine.REAR_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.REAR_SHOT].getTextureId()); 		break;
		case Engine.TEMP_SHIELD:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.TEMP_SHIELD].getTextureId()); 	break;
		case Engine.SUPER_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.SUPER_SHOT].getTextureId()); 	break;
		case Engine.BOUNCY_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.BOUNCY_SHOT].getTextureId()); 	break;
		}
		
    	super.drawSelf(gl);
	}
}
