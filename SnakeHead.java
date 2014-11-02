import java.util.Random;

import javax.media.opengl.GL2;


/**Handles the moving of the SnakeObjects behind it
 * @author Jake Murphy
 */
public class SnakeHead extends MovingObject implements SnakeObject {

//	private int length; //count of snake objects (including itself, i think, doesn't matter too much)
	private SnakeObject after; //the next object
	
	private double angle; //angle of movement
	private boolean curAngleChange = true;
	private int curChangeRate = 0;
	
	SnakeHead(double size, double[] colour, int length) {
		super(size, colour);

		this.angle = 0;
		if (length < 2) {
			length = 2; //set minimum length to be 2, just because - also negative is bad
		}
		
		after = new SnakeBody(size*0.7, colour, this, length-1);
	}
	
	public void destroy() { //destroy properly
		super.destroy();
		after.destroy();
	}
	
	
	public void update(double dt) {
		double[] pos = getPosition(); //we are the head
		
		pos[0] += Math.cos(Math.toRadians(angle))*dt*MAX_SPEED;
		pos[1] += Math.sin(Math.toRadians(angle))*dt*MAX_SPEED;
		
		if (curAngleChange) { //pick a direction 
			angle += 0.5; //change in the angle
		} else {
			angle -= 0.5;
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
		if (pos[0] > GameEngine.boardWidth-(size/2)) {
			pos[0] = GameEngine.boardWidth-(size/2);
		} else if (pos[0] < -GameEngine.boardWidth+(size/2)) {
			pos[0] = -GameEngine.boardWidth+(size/2);
		}
		if (pos[1] > GameEngine.boardHeight-(size/2)) {
			pos[1] = GameEngine.boardHeight-(size/2);
		} else if (pos[1] < -GameEngine.boardHeight+(size/2)) {
			pos[1] = -GameEngine.boardHeight+(size/2);
		}
		setPosition(pos);

		after.update(dt); //will always exist
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.SNAKEHEAD].getTextureId());
		double[] colour = GameEngine.WHITE; //the colour is really only for the body objects :P
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


	@Override
	public SnakeObject getBefore() { return null; }
	@Override
	public SnakeObject getAfter() { return after; }

}


