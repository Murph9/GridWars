package game.objects;
import java.util.ArrayList;

import game.logic.Engine;
import game.logic.Helper;

import javax.media.opengl.GL2;


public class Spawner extends GameObject {
	
	public final static ArrayList<Spawner> ALL_THIS = new ArrayList<Spawner>();
	
	private int health; //hits remaining and size of the larger shape
	
	private double countDown; //count down until next spawn
	private double spawnRate; //the time interval of the spawns
	private int spawnType; //Engine.<type>
	
	/**Makes a spawner that spawns objects of the given type (from Engine)
	 * @param type Value of given object from Engine's public values
	 * @param health Number of shots to kill it
	 * @param rate How often in seconds it shoots
	 */
	public Spawner(double spawnTimer, int type, int health, double rate) {
		size = 1;
		this.health = health;
		countDown = rate;
		this.spawnRate = rate;
		this.spawnType = type;
		ALL_THIS.add(this);
		
		this.spawnTimer = spawnTimer;
		score = 200; //just big ok, its meant to be 3 times the original object
		
		switch(spawnType) {
		case Engine.CLONE:
		case Engine.BLACKHOLE:
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
			try { //none of these options can exist as spawners
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.amHit(false); //full delete
			ALL_THIS.remove(this);
			break;
		}
	}
	
	public double getSize() {
		return size + (double)health/50;
	}
	
	public void update(double dt) {
		if (spawnTimer > 0) {
			spawnTimer -= dt;
			return;
		}
		
		countDown -= dt;
		if (countDown < 0) {
			countDown = spawnRate;
			//then spawn object
			GameObject o = null;
			switch(spawnType) {
			case Engine.SPINNER:
				o = new SimpleSpinner(0); 	break;
			case Engine.DIAMOND:
				o = new HomingDiamond(0); 	break;
			case Engine.SQUARE:
				o = new SplitingSquare(0); 	break;
			case Engine.SNAKEHEAD:
				o = new SnakeHead(0); 	break;
			case Engine.BUTTERFLY:
				o = new HomingButterfly(0); 	break;
			case Engine.CIRCLE:
				o = new HomingCircle(0); 	break;
			case Engine.SHY:
				o = new ShySquare(0); 	break;
			case Engine.TRIANGLE:
				o = new ConnectedTriangle(0); 	break;
			}
		
			o.x = x;
			o.y = y;
		}
	}
	
	
	public void amHit(boolean isPoints) {
		health--;
		if (health < 0) {
			ALL_THIS.remove(this);
			super.amHit(isPoints);
		}
	}

	/**draw shape, then draw bigger shape (of size + (double)health/10 larger)
	 */
	public void drawSelf(GL2 gl) {
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[spawnType].getTextureId()); //fix please
		gl.glColor4d(Engine.WHITE[0], Engine.WHITE[1], Engine.WHITE[2], Engine.WHITE[3]);
		Helper.square(gl);
		
		gl.glPushMatrix();
		gl.glScaled(size + (double)health/50, size + (double)health/50, 1);
		Helper.square(gl);
		gl.glPopMatrix();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

}
