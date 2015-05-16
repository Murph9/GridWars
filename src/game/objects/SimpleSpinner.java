package game.objects;
import game.logic.*;

import java.util.ArrayList;

import javax.media.opengl.GL2;

//TODO have a few colours to pick from (stops it being so bland)

public class SimpleSpinner extends MovingObject {
	
	public final static ArrayList<SimpleSpinner> ALL_THIS = new ArrayList<SimpleSpinner>();
	public static double MAX_SPEED = 3;
	
	public int rotSpeed = 180; //if not set
	
	public SimpleSpinner(double spawnTimer) {
		this(spawnTimer, 1, Engine.PURPLE);
	}
	
	SimpleSpinner(double spawnTimer, double size, double[] colour) {
		super(size, colour);
		this.rotSpeed = (Engine.rand.nextInt(180)+90)*(Engine.rand.nextInt(2)*2-1);
		dx = Engine.rand.nextDouble()*2-1; //rand between -1 and 1
		dy = Engine.rand.nextDouble()*2-1;
		ALL_THIS.add(this);
		
		score = 25;
		this.spawnTimer = spawnTimer;
//		SoundEffect.SHOOT.play(10, 0);
		new SoundEffect(SoundEffect.SPAWN, 10 ,0).start();
	}

	public void update(double dt) {
		if (this.spawnTimer > 0) {
			this.spawnTimer -= dt;
			return;
		}
		
		x += dx*dt;
		y += dy*dt;
		
		angle += dt*rotSpeed;
		
		Helper.keepInside(this, Helper.BOUNCE);
		
		blackHole();
		selfCol();
		
		double speed = Math.sqrt(dx*dx+dy*dy);
		if (speed != 0 && speed > MAX_SPEED) {
			dx /= speed;
			dy /= speed;
		}
	}
	
	public void selfCol() {
		for (SimpleSpinner s: SimpleSpinner.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				double dist = distX*distX + distY*distY + 0.0001;
				if (dist < (size*size)+(s.size*s.size)) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}
	
	public void amHit(boolean isPoints) {
		super.amHit(isPoints);
		ALL_THIS.remove(this);
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.SPINNER].getTextureId());
		
    	super.drawSelf(gl);
	}
}
