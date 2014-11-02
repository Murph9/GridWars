import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL2;
import javax.swing.Timer;


public class Player extends MovingObject implements KeyListener {

	public static double MAX_SPEED = 7;
	
	public static double DRAG = 1.04; //please don't change these numbers again
	
	private boolean xPosAccel = false,
					xNegAccel = false,
					yPosAccel = false,
					yNegAccel = false;
	
	private Timer timer;
	
	Player(double size, double[] colour) {
		super(size, colour);
		
		double[] myPos = getPosition();
		double[] s = Mouse.theMouse.getPosition();
		double angle = Math.toDegrees(Math.atan2((s[1]-myPos[1]), (s[0]-myPos[0])));
		setRotation(angle);
		
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newBullet();
			}
		};
		this.timer = new Timer(300, taskPerformer);
		this.timer.start();
		
	}
	
	private void newBullet() { //creats a PlayerBullet
		PlayerBullet b1 = new PlayerBullet(0.4, GameEngine.GREEN);
		PlayerBullet b2 = new PlayerBullet(0.4, GameEngine.GREEN);

		double[] myPos = getPosition();
		double angle = getRotation();
		
		double[] temp = new double[2];

		temp[0] = (Math.cos(Math.toRadians(angle))*size/2 + myPos[0]);
		temp[1] = (Math.sin(Math.toRadians(angle))*size/2 + myPos[1]);
		
		b1.setPosition(new double[] {Math.cos(Math.toRadians(angle+30))*size/2 + myPos[0], Math.sin(Math.toRadians(angle+20))*size/2 + myPos[1]});
		b2.setPosition(new double[] {Math.cos(Math.toRadians(angle-30))*size/2 + myPos[0], Math.sin(Math.toRadians(angle-20))*size/2 + myPos[1]});
		
		b1.setRotation(getRotation());
		b2.setRotation(getRotation()); //yay
		
		b1.setSpeed((mySpeedX/2+2*Math.cos(Math.toRadians(angle)))*MAX_SPEED, (mySpeedY/2+2*Math.sin(Math.toRadians(angle))*MAX_SPEED));
		b2.setSpeed((mySpeedX/2+2*Math.cos(Math.toRadians(angle)))*MAX_SPEED, (mySpeedY/2+2*Math.sin(Math.toRadians(angle))*MAX_SPEED));
	}

	public void update(double dt) {
		double[] pos = getPosition();
		
		//movement stuff
		pos[0] += mySpeedX*dt*MAX_SPEED; //add it
    	pos[1] += mySpeedY*dt*MAX_SPEED;
		
    	//border collision stuff
		if (pos[0] > GameEngine.boardWidth-(size/2)) {
			mySpeedX = 0;
			pos[0] = GameEngine.boardWidth-(size/2);
		} else if (pos[0] < -GameEngine.boardWidth+(size/2)) {
			mySpeedX = 0;
			pos[0] = -GameEngine.boardWidth+(size/2);
		}
		
		if (pos[1] > GameEngine.boardHeight-(size/2)) {
			mySpeedY = 0;
			pos[1] = GameEngine.boardHeight-(size/2);
		} else if (pos[1] < -GameEngine.boardHeight+(size/2)) {
			mySpeedY = 0;
			pos[1] = -GameEngine.boardHeight+(size/2);
		}
		
		setPosition(pos);
		
		//do rotation stuff
		double[] s = Mouse.theMouse.getPosition();
		double angle = Math.toDegrees(Math.atan2((s[1]-pos[1]), (s[0]-pos[0])));
		if (angle > 0) angle += 360; // Math.toDegrees(2*Math.PI); //see here fix found
		setRotation(angle);
		
		//do accel (for nextTime stuff)
		if(xPosAccel) { mySpeedX = 1; }
		else if(xNegAccel) { mySpeedX = -1; }
		else { mySpeedX /= DRAG; }
		if(yPosAccel) { mySpeedY = 1; }
		else if(yNegAccel) { mySpeedY = -1; }
		else { mySpeedY /= DRAG; }
		
		if (xPosAccel || xNegAccel || yPosAccel || yNegAccel) {
			double dist = Math.sqrt(mySpeedX*mySpeedX + mySpeedY*mySpeedY);
	    	if (dist != 0) { //divide by zero errors are bad
	    		mySpeedX /= dist;
	    		mySpeedY /= dist; //now they are normalised
	    	}
		}
	}

	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.PLAYER].getTextureId());
		
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
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		
		double[] s = Mouse.theMouse.getPosition();
		double[] myPos = getPosition();
		gl.glRotated(-getRotation(), 0, 0, 1);
		
		gl.glLineStipple(1, (short)0xAA); // # [1]
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glBegin(GL2.GL_LINES); //helpful line
			gl.glVertex2d(0,0);
			gl.glVertex2d(s[0]-myPos[0], s[1]-myPos[1]); //mouse position
		gl.glEnd();
		
		gl.glDisable(GL2.GL_LINE_STIPPLE);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			xPosAccel = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
			xNegAccel = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			yPosAccel = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
			yNegAccel = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			xPosAccel = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
			xNegAccel = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			yPosAccel = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
			yNegAccel = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) { }

}
