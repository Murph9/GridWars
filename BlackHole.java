import javax.media.opengl.GL2;



//now instead of the blackhole class handling sucking everything in, they handle it themselves
	//but this does require that all objects have a blackhole method 
public class BlackHole extends MovingObject {

	private boolean isInert;
	private int numCount; //number of objects consumed, snakes are weird here
				//as in NumNumNumNum
	
	private int maxNum;
	private int hitPoints;
	
	BlackHole(double size, double[] colour) {
		super(size, colour);
		isInert = true;
		numCount = 0;
		maxNum = 10;
		hitPoints = 10;
	}

	public boolean isFull() {
		return numCount > maxNum;
	}
	
	public void giveObject() {
		numCount++;
		hitPoints += 2;
	}
	
	public boolean isInert() {
		return isInert;
	}
	
	@Override
	public void update(double dt) {
		//not moving (for now)
	}

	//function handles most of blackhole's things
	public void destroy() {
		if (isFull()) {
			super.destroy();
			
			for (int i = 0; i < 20; i++) {
				//spawn annoying*
			}
			
		}// else if (hitPoints)
		
		
		
		
		
	}
	
	@Override
	public int score() {
		if (hitPoints <= 0) {
			return numCount*100; //some maths on how many objects its pulled in:
			//the wiki says:   150 + (5/2)N(N+1)
		} else {
			return 0;
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.CIRCLE].getTextureId());
		gl.glColor3d(colour[0], colour[1], colour[2]);
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(0, 0);
			gl.glVertex2d(-size/2, -size/2);
			gl.glTexCoord2d(1, 0);
			gl.glVertex2d(size/2, -size/2);
			gl.glTexCoord2d(1, 1);
			gl.glVertex2d(size/2, size/2);
			gl.glTexCoord2d(0, 1);
			gl.glVertex2d(-size/2, size/2);
		gl.glEnd();
	}

}
