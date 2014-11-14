import javax.media.opengl.GL2;


public class Particle extends MovingObject {

	private double decayTimer;
	private double speed;
	
	private double thickness;
	
	Particle(double thickness, double[] colour, double x, double y, double dx, double dy, double time) {
		super(1, colour);
		this.thickness = thickness;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.angle = Math.atan2(dy, dx);
		speed = 1; //whatever
		decayTimer = time;
	}

	@Override
	public void update(double dt) {
		x += dx*dt*speed;
		y += dy*dt*speed;
		
		//can't use the helper function here );
		if (x > GameEngine.boardWidth) {
			dx = -dx;
			x = GameEngine.boardWidth;
		} else if (x < -GameEngine.boardWidth) {
			dx = -dx;
			x = -GameEngine.boardWidth;
		}
		
		if (y > GameEngine.boardHeight) {
			dy = -dy;
			y = GameEngine.boardHeight;
		} else if (y < -GameEngine.boardHeight) {
			dy = -dy;
			y = -GameEngine.boardHeight;
		}
		
		decayTimer -= dt;
		dx /= 1.1;
		dy /= 1.1;
		
		if (decayTimer <= 0) {
			destroy();
		}
		
//		blackHole();
	}

	@Override
	public void selfCol() {
//		doesn't exist in this
	}
	
	public void blackHole() {
		
	}
	
	public void drawSelf(GL2 gl) {
		gl.glColor3d(colour[0], colour[1], colour[2]);
		
		gl.glLineWidth((float)thickness);
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex2d(0,0);
			gl.glVertex2d(Math.cos(angle)*0.2, Math.sin(angle)*0.2);
		gl.glEnd();
		gl.glLineWidth(1);
	}

}
