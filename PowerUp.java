import javax.media.opengl.GL2;


/**The name should be a good hint
 * @author Jake Murphy
 */
public class PowerUp extends GameObject {

	public static final int SHOT_SPEED = 0, SHOT_COUNT = 1;
	public static final int EXTRA_BOMB = 2, EXTRA_LIFE = 3;
	public static final int SIDE_SHOT = 4, REAR_SHOT = 5;
	public static final int TEMP_SHIELD = 6;
	public static final int SUPER_SHOT = 7, BOUNCY_SHOT = 8;
	
	private int type;
	
	PowerUp (int type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;
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
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);

    	Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
