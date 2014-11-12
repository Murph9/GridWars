import javax.media.opengl.GL2;


/**The name should be a good hint
 * @author Jake Murphy
 */
public class PowerUp extends GameObject {

	public static final int FASTER = 0;
	public static final int MORE = 1;
	
	public static final int BOMB = 2;
	public static final int LIFE = 3;
	
	public static final int SIDE_SHOOT = 4;
	public static final int BACK_SHOOT = 5;
	
	public static final int SUPER = 6;
	public static final int BOUNCY = 7;
	
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
		case SIDE_SHOOT:
//			GameEngine.curGame.incBulletSpeed();	break;
		case BACK_SHOOT:
//			GameEngine.curGame.incBulletSpeed();	break;
		}
	}
	
	public void drawSelf(GL2 gl) {
//		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId()); //get id of the dot file
		switch (type) {
		case FASTER:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId()); 	break; //e.g.
		case MORE:
			GameEngine.curGame.incBulletCount();	break;
		case BOMB:
			GameEngine.curGame.incBombCount();		break;
		case LIFE:
			GameEngine.curGame.incLives();			break;
		case SIDE_SHOOT:
//			GameEngine.curGame.incBulletSpeed();	break;
		case BACK_SHOOT:
//			GameEngine.curGame.incBulletSpeed();	break;
		}
		
		
    	gl.glColor3d(colour[0], colour[1], colour[2]);

    	Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
