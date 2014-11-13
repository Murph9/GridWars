import java.util.ArrayList;

import javax.media.opengl.GL2;

//now instead of the blackhole class handling sucking everything in, they handle it themselves
	//but this does require that all objects have a blackhole method ...
public class BlackHole extends MovingObject {

	public static final ArrayList<BlackHole> ALL_THIS = new ArrayList<BlackHole>();
	public static final int SUCK_RADIUS = 8;
	
	public static final int MAX_SPEED = 2; //yeah kinda slow
	
	private boolean isInert;
	private int numCount; //number of objects consumed
				//as in NumNumNumNum (eating)
	
	private int maxNum; //before explosion
	private int hitPoints;
	
	BlackHole(double size, double[] colour) {
		super(size, colour);
		isInert = true;
		numCount = 0;
		maxNum = 30;
		hitPoints = 20;
		ALL_THIS.add(this);
		
		dx = 0;
		dy = 0;
	}
	
	public void giveObject(double x, double y) {
		dx += x/2;//(numCount+15);
		dy += y/2;//(numCount+15);
		numCount++;
		hitPoints += 2;
		size += 0.02;
		if (numCount > maxNum) {
			actuallyDestroy(false);
		}
	}
	
	public void shotAt() { isInert = false; }
	public boolean isInert() { return isInert; }
	
	@Override
	public void update(double dt) {
		x += dx*dt;
		y += dy*dt;
		
		dx /= 1.02; //big things drag still
		dy /= 1.02;
		
		Helper.keepInside(this, Helper.BOUNCE);
		
		blackHole();
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > MAX_SPEED) {
			dx /= speed;
			dy /= speed;
		}
		selfCol();
		
		speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > MAX_SPEED) {
			dx *= MAX_SPEED/speed;
			dy *= MAX_SPEED/speed;
		}
	}
	
	public void selfCol() {
		for (BlackHole s: BlackHole.ALL_THIS) {
			if (!s.equals(this)) { //because that would be silly
				double distX = s.x - x;
				double distY = s.y - y;
				double dist = distX*distX + distY*distY + 0.0001;
				if (dist < (size*size)+(s.size*s.size)) {
					dx -= Helper.sgn(distX)/(Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(Math.sqrt(dist));
				}
			}
		}
	}

	//function handles destroying things into itself
	public void destroy() {
		isInert = false;
		hitPoints--;
		size -= 0.01;
		if (hitPoints <= 0) {
			actuallyDestroy(true);
		}
	}
	
	//handles actually dying
	private void actuallyDestroy(boolean wasShot) {
		super.destroy();
		ALL_THIS.remove(this);
		
		if (wasShot) { //then add score
			GameEngine.curGame.addScore(150 + (5/2)*numCount*(numCount+1));			
		} else { // or explode
			for (int i = 0; i < 20; i++) {
				HomingButterfly a = new HomingButterfly(0.6, GameEngine.BLUE);
				a.setPosition(new double[] {x+Math.cos(i),y+Math.sin(i)});
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId());
		if (isInert) {
			gl.glColor3d(GameEngine.PURPLE[0], GameEngine.PURPLE[1], GameEngine.PURPLE[2]);
		} else {
			gl.glColor3d(colour[0], colour[1], colour[2]);
		}
		
		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

}
