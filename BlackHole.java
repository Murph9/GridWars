import java.util.ArrayList;

import javax.media.opengl.GL2;

//now instead of the blackhole class handling sucking everything in, they handle it themselves
	//but this does require that all objects have a blackhole method ...
public class BlackHole extends MovingObject {

	public static final ArrayList<BlackHole> ALL_THIS = new ArrayList<BlackHole>();
	public static final int SUCK_RADIUS = 8;
	
	private boolean isInert;
	private int numCount; //number of objects consumed, snakes are weird here
				//as in NumNumNumNum
	
	private int maxNum;
	private int hitPoints;
	
	BlackHole(double size, double[] colour) {
		super(size, colour);
		isInert = true;
		numCount = 0;
		maxNum = 100;
		hitPoints = 20;
		ALL_THIS.add(this);
	}

	public boolean isFull() {
		return numCount > maxNum;
	}
	
	public void giveObject() {
		numCount++;
		hitPoints += 2;
		size += 0.02;
		if (numCount > maxNum) {
			actuallyDestroy();
		}
	}
	
	public void shotAt() { isInert = false; }
	public boolean isInert() { return isInert; }
	
	@Override
	public void update(double dt) {
		//not moving (for now)
	}

	//function handles most of blackhole's things
	public void destroy() {
		isInert = false;
		hitPoints--;
		size -= 0.01;
		if (hitPoints <= 0) {
			actuallyDestroy();
		}
	}
	
	private void actuallyDestroy() {
		super.destroy();
		ALL_THIS.remove(this);
		for (int i = 0; i < numCount; i++) {
//			AnnoyingButterfly a = new AnnoyingButterfly(0.6, GameEngine.BLUE);
		}
		GameEngine.score.addScore(1); //the wiki says: 150 + (5/2)N(N+1)
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId());
		if (isInert) {
			gl.glColor3d(GameEngine.PURPLE[0], GameEngine.PURPLE[1], GameEngine.PURPLE[2]);
		} else {
			gl.glColor3d(colour[0], colour[1], colour[2]);
		}
		
		Helper.square(gl);
		
		gl.glScaled(8,8,1);
		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

}
