import javax.media.opengl.GL2;


public class Helper {
	
//	public static final double SIZE_MUL = 0.707107; // 1 /sqrt(2)

	public static int sgn(int a) {
		if (a > 0) return 1;
		else if (a < 0) return -1;
		else return 0;
	}
	public static int sgn(double a) {
		if (a > 0) return 1;
		else if (a < 0) return -1;
		else return 0;
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
	
}
