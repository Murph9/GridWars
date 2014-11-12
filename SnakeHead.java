import java.util.Random;

import javax.media.opengl.GL2;


/**Handles the moving of the SnakeObjects behind it
 * @author Jake Murphy
 */
public class SnakeHead extends MovingObject implements SnakeObject {

	public static final int score = 100;
	public static final int MAX_SPEED = 4;
	
	private SnakeObject after; //the next object
	
	private double angle; //angle of movement
	private boolean curAngleChange = true;
	private int curChangeRate = 0;
	private static int ANGLE_VEL = 90;
	
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
		GameEngine.curGame.addScore(score);
	}
	
	
	public void update(double dt) {
		x += dx*dt;
		y += dy*dt;
		
		Helper.keepInside(this, Helper.SPLAT);

		if (curAngleChange) { //pick a direction 
			angle += ANGLE_VEL*dt; //change in the angle
		} else {
			angle -= ANGLE_VEL*dt;
		}
		angle = angle % 360; //clamp (because bad things happen otherwise)
		
		if (curChangeRate == 0) {
			Random r = new Random();
			curAngleChange = r.nextInt(2) == 0;
			curChangeRate = r.nextInt(60);
		} else {
			curChangeRate--;
		}
		setRotation(angle);
		dx = Math.cos(Math.toRadians(angle))*MAX_SPEED;
		dy = Math.sin(Math.toRadians(angle))*MAX_SPEED;
		
		blackHole();
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0) {
			dx *= MAX_SPEED/speed;
			dy *= MAX_SPEED/speed;
		}
		
		after.update(dt); //will always exist (at least on head)
	}
	
	public void selfCol() {
		//snakes do NOT collide
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SNAKEHEAD].getTextureId());
		gl.glColor3d(colour[0], colour[1], colour[2]);
		
		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}


	@Override
	public SnakeObject getBefore() { return null; }
	@Override
	public SnakeObject getAfter() { return after; }
}


