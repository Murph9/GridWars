import javax.media.opengl.GL2;

//Just draws a simple border around the outside
//Should never touch the mySize field
public class Border extends GameObject {

	private int width;
	private int height;
	
	Border(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void drawSelf(GL2 gl) {
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		
		gl.glColor3d(0.3, 0.3, 0.3);
		gl.glBegin(GL2.GL_LINES);
		for (int i = -width; i < width; i+=2) {
			gl.glVertex2d(i,height);
			gl.glVertex2d(i,-height);
		}
		for (int i = -height; i < height; i+=2) {
			gl.glVertex2d(-width, i);
			gl.glVertex2d(width, i);
		}
		gl.glEnd();
		
		gl.glColor3d(1.0, 1.0, 1.0);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(-width, -height);
			gl.glVertex2d(width, -height);
			gl.glVertex2d(width, height);
			gl.glVertex2d(-width, height);
		gl.glEnd();
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}
}
