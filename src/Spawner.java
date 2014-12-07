import javax.media.opengl.GL2;


public class Spawner extends GameObject {
	
	private int health; //hits remaining and size of the larger shape
	private double countDown; //count down until next spawn
	private double rate; //the time interval of the spawns
	private int type;
	
	/**Makes a spawner that spawns objects of the given type (from GameEngine)
	 * @param type Value of given object from GameEngine's public values
	 * @param health Number of shots to kill it
	 * @param rate How often in seconds it shoots
	 */
	public Spawner(int type, int health, int rate) {
		size = 1;
		this.health = health;
		countDown = rate;
		this.rate = rate;
		this.type = type;
		
		score = 200; //just big ok
	}
	
	public double getSize() {
		return size + (double)health/75;
	}
	
	public void update(double dt) {
		countDown -= dt;
		if (countDown < 0) {
			countDown = rate;
			//then spawn object
			GameObject o = null;
			switch(type) {
			case GameEngine.SPINNER:
				o = new SimpleSpinner(); 	break;
			case GameEngine.DIAMOND:
				o = new SimpleSpinner(); 	break;
			case GameEngine.SQUARE:
				o = new SimpleSpinner(); 	break;
			case GameEngine.CLONE:
				o = new SimpleSpinner(); 	break;
			case GameEngine.SNAKEHEAD:
				o = new SimpleSpinner(); 	break;
			case GameEngine.BUTTERFLY:
				o = new SimpleSpinner(); 	break;
			case GameEngine.SEEKER:
				o = new SimpleSpinner(); 	break;
			case GameEngine.SHY:
				o = new SimpleSpinner(); 	break;
			case GameEngine.TRIANGLE:
				o = new SimpleSpinner(); 	break;
				
			case GameEngine.PLAYER: 
			case GameEngine.BULLET:
			case GameEngine.SNAKEBODY:
			case GameEngine.EXTRA_BULLET:
			case GameEngine.EXTRA_SPEED:
			case GameEngine.TEMP_SHIELD:
			case GameEngine.EXTRA_BOMB:
			case GameEngine.EXTRA_LIFE:
			case GameEngine.BOUNCY_SHOT:
			case GameEngine.SUPER_SHOT:
			case GameEngine.REAR_SHOT:
				try {
					throw new Exception(); //we don't want this
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		
			o.setPosition(new double[]{(GameEngine.rand.nextInt(2)*2-1)*(GameEngine.boardWidth-0.5), 
					(GameEngine.rand.nextInt(2)*2-1)*(GameEngine.boardHeight-0.5)});
		}
	}
	
	
	public void amHit(boolean isPoints) {
		health--;
		if (health < 0) {
			super.amHit(isPoints);
		}
	}

	/**draw shape, then draw bigger shape (of size + (double)health/10 larger)
	 */
	public void drawSelf(GL2 gl) {
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[type].getTextureId()); //fix please
		
		Helper.square(gl);
		
		gl.glPushMatrix();
		gl.glScaled(size + (double)health/75, size + (double)health/75, 1);
		Helper.square(gl);
		gl.glPopMatrix();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

}
