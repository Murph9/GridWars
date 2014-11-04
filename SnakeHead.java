import java.util.Random;

import javax.media.opengl.GL2;


/**Handles the moving of the SnakeObjects behind it
 * @author Jake Murphy
 */
public class SnakeHead extends MovingObject implements SnakeObject {

	public static final int score = 100;
	
	private SnakeObject after; //the next object
	
	private double angle; //angle of movement
	private boolean curAngleChange = true;
	private int curChangeRate = 0;
	private static int ANGLE_VEL = 40;
	
	SnakeHead(double size, double[] colour, int length) {
		super(size, colour);

		this.angle = 0;
		if (length < 10) {
			length = 10; //set minimum length to be 10, just because they look funny otherwise
		}
		
		after = new SnakeBody(size*0.8, colour, this, length);
	}
	
	public void destroy() { //destroy properly (and down the line)
		super.destroy();
		after.destroy();
	}
	
	
	public void update(double dt) {
		double[] pos = getPosition(); //we are the head
		
		pos[0] += Math.cos(Math.toRadians(angle))*dt*MAX_SPEED;
		pos[1] += Math.sin(Math.toRadians(angle))*dt*MAX_SPEED;
		
		if (curAngleChange) { //pick a direction 
			angle += ANGLE_VEL*dt; //change in the angle
		} else {
			angle -= ANGLE_VEL*dt;
		}
		angle = angle % 360; //clamp (because bad things happen otherwise)
		
		if (curChangeRate == 0) {
			Random r = new Random();
			curAngleChange = r.nextInt(2) == 0;
			curChangeRate = r.nextInt(100);
		} else {
			curChangeRate--;
		}

		setRotation(angle);
		
		//wall collision
		if (pos[0] > GameEngine.boardWidth-(mySize/2)) {
			pos[0] = GameEngine.boardWidth-(mySize/2);
			angle += ANGLE_VEL*dt;
		} else if (pos[0] < -GameEngine.boardWidth+(mySize/2)) {
			pos[0] = -GameEngine.boardWidth+(mySize/2);
			angle += ANGLE_VEL*dt;
		}
		if (pos[1] > GameEngine.boardHeight-(mySize/2)) {
			pos[1] = GameEngine.boardHeight-(mySize/2);
			angle += ANGLE_VEL*dt;
		} else if (pos[1] < -GameEngine.boardHeight+(mySize/2)) {
			pos[1] = -GameEngine.boardHeight+(mySize/2);
			angle += ANGLE_VEL*dt;
		}
		setPosition(pos);

		after.update(dt); //will always exist
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SNAKEHEAD].getTextureId());
		gl.glColor3d(colour[0], colour[1], colour[2]);
		
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


	@Override
	public SnakeObject getBefore() { return null; }
	@Override
	public SnakeObject getAfter() { return after; }
}


