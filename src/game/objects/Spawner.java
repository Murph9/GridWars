package game.objects;
import game.logic.Engine;
import game.logic.Helper;

import javax.media.opengl.GL2;


public class Spawner extends GameObject {
	
	private int health; //hits remaining and size of the larger shape
	private double countDown; //count down until next spawn
	private double rate; //the time interval of the spawns
	private int type; //GameEngine.<type>
	
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
		
		score = 200; //just big ok, yes its this cost
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
			case Engine.SPINNER:
				o = new SimpleSpinner(); 	break;
			case Engine.DIAMOND:
				o = new SimpleSpinner(); 	break;
			case Engine.SQUARE:
				o = new SimpleSpinner(); 	break;
			case Engine.CLONE:
				o = new SimpleSpinner(); 	break;
			case Engine.SNAKEHEAD:
				o = new SimpleSpinner(); 	break;
			case Engine.BUTTERFLY:
				o = new SimpleSpinner(); 	break;
			case Engine.SEEKER:
				o = new SimpleSpinner(); 	break;
			case Engine.SHY:
				o = new SimpleSpinner(); 	break;
			case Engine.TRIANGLE:
				o = new SimpleSpinner(); 	break;
				
			case Engine.PLAYER: 
			case Engine.BULLET:
			case Engine.SNAKEBODY:
			case Engine.EXTRA_BULLET:
			case Engine.EXTRA_SPEED:
			case Engine.TEMP_SHIELD:
			case Engine.EXTRA_BOMB:
			case Engine.EXTRA_LIFE:
			case Engine.BOUNCY_SHOT:
			case Engine.SUPER_SHOT:
			case Engine.REAR_SHOT:
				try {
					throw new Exception(); //we don't want this
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		
			o.setPosition(new double[]{(Engine.rand.nextInt(2)*2-1)*(Engine.settings.getPixelWidth()-0.5), 
					(Engine.rand.nextInt(2)*2-1)*(Engine.settings.getPixelHeight()-0.5)});
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
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[type].getTextureId()); //fix please
		
		Helper.square(gl);
		
		gl.glPushMatrix();
		gl.glScaled(size + (double)health/75, size + (double)health/75, 1);
		Helper.square(gl);
		gl.glPopMatrix();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

}
