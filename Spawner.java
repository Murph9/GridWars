import javax.media.opengl.GL2;


public class Spawner extends GameObject {
	
	public static final int SQUARE = 0, DIAMOND = 1, TRIANGLE = 2, SPLITTER = 3, SEEKER = 4;
	public static final int score = 300;
	
	private int health; //hits remaining and size of the larger shape
	private double countDown; //count down until next spawn
	private double rate; //the time interval of the spawns
	private int type;
	
	Spawner (int health, double rate, int type) {
		size = 1;
		this.health = health;
		countDown = rate;
		this.rate = rate;
		this.type = type;
	}
	
	public double getSize() {
		return size + (double)health/75;
	}
	
	public void update(double dt) {
		countDown -= dt;
		if (countDown < 0) {
			countDown = rate;
			//spawn object
			GameObject o = null;
			switch(type) {
			case SQUARE:
				o = new ShySquare(1, GameEngine.GREEN);					break;
			case DIAMOND:
				o = new HomingDiamond(1, GameEngine.LIGHT_BLUE);		break;
			case TRIANGLE:
				o = new ConnectedTriangle(1, GameEngine.GREEN, null);	break;
			case SPLITTER:
				o = new SplitingSquare(1, GameEngine.GREEN, 0, 1, true);break;
			case SEEKER:
				o = new HomingSeeker(1, GameEngine.GREEN);				break;
			}
			
			o.x = x;
			o.y = y;
		}
	}
	
	
	public void destroy() {
		health--;
		if (health < 0) {
			super.destroy();
			GameEngine.curGame.addScore(score);
		}
	}
	
	public void drawSelf(GL2 gl) {
		//draw shape, then draw bigger shape (of size + (double)health/10 larger)
		switch(type) {
		case SQUARE:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SHY].getTextureId());	break;
		case DIAMOND:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.DIAMOND].getTextureId());	break;
		case TRIANGLE:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.TRIANGLE].getTextureId());	break;
		case SPLITTER:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SQUARE].getTextureId());	break;
		case SEEKER:
			gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId());	break;
		}
		
		Helper.square(gl);
		
		gl.glPushMatrix();
		gl.glScaled(size + (double)health/75, size + (double)health/75, 1);
		Helper.square(gl);
		gl.glPopMatrix();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
