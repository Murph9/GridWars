package game.objects;
import java.util.ArrayList;

import game.logic.*;

import javax.media.opengl.GL2;


/**The name should be a good hint
 * @author Jake Murphy
 */
public class PowerUp extends MovingObject {

	public static final int SHOT_SPEED = 0, SHOT_COUNT = 1;
	public static final int EXTRA_BOMB = 2, EXTRA_LIFE = 3;
	public static final int SIDE_SHOT = 4, REAR_SHOT = 5;
	public static final int TEMP_SHIELD = 6;
	public static final int SUPER_SHOT = 7, BOUNCY_SHOT = 8;
	
	public final static ArrayList<PowerUp> ALL_THIS = new ArrayList<PowerUp>();
	
	
	private int type;
	
	PowerUp (int type) {
		super(1, GameEngine.WHITE);
		this.type = type;
		ALL_THIS.add(this);
	}
	
	public void update(double dt) {
		x = dx*dt;
		y = dy*dt;
		
		selfCol();
		blackHole();
	}
	
	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		ALL_THIS.remove(this);
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
	
	public void kill() { //really should never gives points directly
		super.amHit(false);
		switch (type) {
		case SHOT_SPEED:
			GameEngine.curGame.incBulletSpeed();	break;
		case SHOT_COUNT:
			GameEngine.curGame.incBulletCount();	break;
		case EXTRA_BOMB:
			GameEngine.curGame.incBombCount();		break;
		case EXTRA_LIFE:
			GameEngine.curGame.incLives();			break;
		case SIDE_SHOT:
			GameEngine.curGame.gotSideShot();		break;
		case REAR_SHOT:
			GameEngine.curGame.gotRearShot();		break;
		case TEMP_SHIELD:
			GameEngine.curGame.gotShield();			break;
		case SUPER_SHOT:
			GameEngine.curGame.gotSuperShot();		break;
		case BOUNCY_SHOT:
			GameEngine.curGame.gotBouncyShot();		break;
		}
	}
	
	public void drawSelf(GL2 gl) {
		switch (type) {
		case SHOT_SPEED:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_SPEED].getTextureId()); 	break;
		case SHOT_COUNT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_BULLET].getTextureId()); 	break;
		case EXTRA_BOMB:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_BOMB].getTextureId()); 	break;
		case EXTRA_LIFE:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_LIFE].getTextureId()); 	break;
		case SIDE_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SIDE_SHOT].getTextureId()); 		break;
		case REAR_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.REAR_SHOT].getTextureId()); 		break;
		case TEMP_SHIELD:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.TEMP_SHIELD].getTextureId()); 	break;
		case SUPER_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SUPER_SHOT].getTextureId()); 	break;
		case BOUNCY_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BOUNCY_SHOT].getTextureId()); 	break;
		}
		
    	super.drawSelf(gl);
	}
}
