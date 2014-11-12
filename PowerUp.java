import javax.media.opengl.GL2;


/**The name should be a good hint
 * @author Jake Murphy
 */
public class PowerUp extends GameObject {

	public static final int FASTER = 0;
	public static final int MORE = 1;
	
	public static final int BOMB = 2;
	public static final int LIFE = 3;
	
	public static final int SIDE_SHOT = 4;
	public static final int REAR_SHOT = 5;
	
	public static final int TEMP_SHIELD = 6;
	
	public static final int SUPER = 7;
	public static final int BOUNCY = 8;
	
	private int type;
	
	
	PowerUp (int type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public void destroy() {
		super.destroy();
		switch (type) {
		case FASTER:
			GameEngine.curGame.incBulletSpeed();	break;
		case MORE:
			GameEngine.curGame.incBulletCount();	break;
		case BOMB:
			GameEngine.curGame.incBombCount();		break;
		case LIFE:
			GameEngine.curGame.incLives();			break;
		case SIDE_SHOT:
			GameEngine.curGame.gotSideShot();		break;
		case REAR_SHOT:
			GameEngine.curGame.gotRearShot();		break;
		case TEMP_SHIELD:
			GameEngine.curGame.gotShield();			break;
		case SUPER:
			GameEngine.curGame.gotSuperShot();		break;
		case BOUNCY:
			GameEngine.curGame.gotBouncyShot();		break;
		}
	}
	
	public void drawSelf(GL2 gl) {
		switch (type) {
		case FASTER:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_SPEED].getTextureId()); 	break;
		case MORE:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_BULLET].getTextureId()); 	break;
		case BOMB:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_BOMB].getTextureId()); 	break;
		case LIFE:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.EXTRA_LIFE].getTextureId()); 	break;
		case SIDE_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SIDE_SHOT].getTextureId()); 		break;
		case REAR_SHOT:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.REAR_SHOT].getTextureId()); 		break;
		case TEMP_SHIELD:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.TEMP_SHIELD].getTextureId()); 	break;
		case SUPER:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SUPER_SHOT].getTextureId()); 	break;
		case BOUNCY:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.BOUNCY_SHOT].getTextureId()); 	break;
		}
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);

    	Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
