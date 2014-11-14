import javax.media.opengl.GL2;


public class Spawner extends GameObject {
	
	private int health; //hits remaining and size of the larger shape
	private double countDown; //count down until next spawn
	private double rate; //the time interval of the spawns
	
	Spawner (int health, double rate) {
		size = 1;
		this.health = health;
		countDown = rate;
		this.rate = rate;
	}
	
	public void update(double dt) {
		countDown -= dt;
		if (countDown < 0) {
			countDown = rate;
			//spawn object
		}
	}
	
	
	public void destroy() {
		health--;
		if (health < 0) {
			super.destroy();
		}
	}
	
	public void drawSelf(GL2 gl) {
		//draw shape, then draw bigger shape (of size + (double)health/10 larger)
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SQUARE].getTextureId());
		
		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
