import javax.media.opengl.GL2;

//class for just cleaning up some of the files (mainly methods that do simple things)
public class Helper {
	
//	public static final double SIZE_MUL = 0.707107; // 1 /sqrt(2)

	public static final int NOTHING = 0;
	public static final int BOUNCE = 1;
	public static final int SPLAT = 2;
	
	public static int sgn(int a) {
		if (a > 0) return 1;
		else if (a < 0) return -1;
		else return GameEngine.rand.nextInt(2)*2 -1; //i was trying to make it move when it didn't want to;
	}
	public static double sgn(double a) {
		if (a > 0) return 1;
		else if (a < 0) return -1;
		else return GameEngine.rand.nextDouble()*2 -1; //i was trying to make it move when it didn't want to
	}
	
	
	public static void square (GL2 gl) {
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(0, 0);
			gl.glVertex2d(-0.5, -0.5);
			gl.glTexCoord2d(1, 0);
			gl.glVertex2d(0.5, -0.5);
			gl.glTexCoord2d(1, 1);
			gl.glVertex2d(0.5, 0.5);
			gl.glTexCoord2d(0, 1);
			gl.glVertex2d(-0.5, 0.5);
		gl.glEnd();
	}
	
	//for things that collide off the play arena walls
	public static void keepInside(MovingObject a, int type) {
		if (a.x > GameEngine.boardWidth-(a.size/2)) {
			if (type == BOUNCE) a.dx = -a.dx;	
			else if (type == SPLAT)	a.dx = 0; 
			a.x = GameEngine.boardWidth-(a.size/2);
		} else if (a.x < -GameEngine.boardWidth+(a.size/2)) {
			if (type == BOUNCE) a.dx = -a.dx;	
			else if (type == SPLAT)	a.dx = 0;
			a.x = -GameEngine.boardWidth+(a.size/2);
		}
		
		if (a.y > GameEngine.boardHeight-(a.size/2)) {
			if (type == BOUNCE) a.dy = -a.dy;	
			else if (type == SPLAT)	a.dy = 0;
			a.y = GameEngine.boardHeight-(a.size/2);
		} else if (a.y < -GameEngine.boardHeight+(a.size/2)) {
			if (type == BOUNCE) a.dy = -a.dy;	
			else if (type == SPLAT)	a.dy = 0;
			a.y = -GameEngine.boardHeight+(a.size/2);
		}
	}
	
}
