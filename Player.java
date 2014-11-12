import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

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
		double[] s = GameEngine.getMousePos();
		double angle = Math.toDegrees(Math.atan2((s[1]-myPos[1]), (s[0]-myPos[0])));
		setRotation(angle);
		
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newBullet();
			}
		};
		this.timer = new Timer(100, taskPerformer);
		this.timer.start();
		
	}
	
	private void newBullet() { //creats PlayerBullets, large
		int num = GameEngine.curGame.getBulCount();
		double speed = MAX_SPEED*GameEngine.curGame.getBulletSpeed();
		
		switch(num) {
		case 2:
			MovingObject b1 = new PlayerBullet(0.35, GameEngine.YELLOW);
			MovingObject b2 = new PlayerBullet(0.35, GameEngine.YELLOW);

			//position
			b1.x = Math.cos(Math.toRadians(angle+20))*size/2 + x;
			b1.y = Math.sin(Math.toRadians(angle+20))*size/2 + y;
			
			b2.x = Math.cos(Math.toRadians(angle-20))*size/2 + x;
			b2.y = Math.sin(Math.toRadians(angle-20))*size/2 + y;
			
			//velocity
			b1.dx = (dx/2+2*Math.cos(Math.toRadians(angle+3)))*speed;
			b1.dy = (dy/2+2*Math.sin(Math.toRadians(angle+3)))*speed;
			
			b2.dx = (dx/2+2*Math.cos(Math.toRadians(angle-3)))*speed;
			b2.dy = (dy/2+2*Math.sin(Math.toRadians(angle-3)))*speed;
			break;
		case 3:
			MovingObject b3 = new PlayerBullet(0.35, GameEngine.YELLOW);
			MovingObject b4 = new PlayerBullet(0.35, GameEngine.YELLOW);
			MovingObject b5 = new PlayerBullet(0.35, GameEngine.YELLOW);
			
			b3.x = Math.cos(Math.toRadians(angle+30))*size/2 + x;
			b3.y = Math.sin(Math.toRadians(angle+30))*size/2 + y;

			b4.x = Math.cos(Math.toRadians(angle))*size/2 + x;
			b4.y = Math.sin(Math.toRadians(angle))*size/2 + y;
			
			b5.x = Math.cos(Math.toRadians(angle-30))*size/2 + x;
			b5.y = Math.sin(Math.toRadians(angle-30))*size/2 + y;
			
			//velocity
			b3.dx = (dx/2+2*Math.cos(Math.toRadians(angle+5)))*speed;
			b3.dy = (dy/2+2*Math.sin(Math.toRadians(angle+5)))*speed;
			
			b4.dx = (dx/2+2*Math.cos(Math.toRadians(angle)))*speed;
			b4.dy = (dy/2+2*Math.sin(Math.toRadians(angle)))*speed;
			
			b5.dx = (dx/2+2*Math.cos(Math.toRadians(angle-5)))*speed;
			b5.dy = (dy/2+2*Math.sin(Math.toRadians(angle-5)))*speed;
			break;
		case 4:
			MovingObject b6 = new PlayerBullet(0.35, GameEngine.YELLOW);
			MovingObject b7 = new PlayerBullet(0.35, GameEngine.YELLOW);
			MovingObject b8 = new PlayerBullet(0.35, GameEngine.YELLOW);
			MovingObject b9 = new PlayerBullet(0.35, GameEngine.YELLOW);

			//position
			b6.x = Math.cos(Math.toRadians(angle+15))*size/2 + x;
			b6.y = Math.sin(Math.toRadians(angle+15))*size/2 + y;
			
			b7.x = Math.cos(Math.toRadians(angle-15))*size/2 + x;
			b7.y = Math.sin(Math.toRadians(angle-15))*size/2 + y;
			
			b8.x = Math.cos(Math.toRadians(angle+25))*size/2 + x;
			b8.y = Math.sin(Math.toRadians(angle+25))*size/2 + y;
			
			b9.x = Math.cos(Math.toRadians(angle-25))*size/2 + x;
			b9.y = Math.sin(Math.toRadians(angle-25))*size/2 + y;
			
			//velocity
			b6.dx = (dx/2+2*Math.cos(Math.toRadians(angle+2)))*speed;
			b6.dy = (dy/2+2*Math.sin(Math.toRadians(angle+2)))*speed;
			
			b7.dx = (dx/2+2*Math.cos(Math.toRadians(angle-2)))*speed;
			b7.dy = (dy/2+2*Math.sin(Math.toRadians(angle-2)))*speed;
			
			b8.dx = (dx/2+2*Math.cos(Math.toRadians(angle+5)))*speed;
			b8.dy = (dy/2+2*Math.sin(Math.toRadians(angle+5)))*speed;
			
			b9.dx = (dx/2+2*Math.cos(Math.toRadians(angle-5)))*speed;
			b9.dy = (dy/2+2*Math.sin(Math.toRadians(angle-5)))*speed;
			break;
		}
		
		
	}

	public void update(double dt) {
		//movement stuff
		x += dx*dt*MAX_SPEED; //add it
    	y += dy*dt*MAX_SPEED;
		
    	Helper.keepInside(this, Helper.SPLAT);
		
		//do rotation stuff
		double[] s = GameEngine.getMousePos();
		double angle = Math.toDegrees(Math.atan2((s[1]-y), (s[0]-x)));
		if (angle > 0) angle += 360; // Math.toDegrees(2*Math.PI); //see here fix found
		setRotation(angle);
		
		//do accel (for nextTime stuff)
		if		(xPosAccel) { dx = 1; }
		else if	(xNegAccel) { dx = -1; }
		else 				{ dx /= DRAG; }
		
		if		(yPosAccel) { dy = 1; }
		else if	(yNegAccel) { dy = -1; }
		else 				{ dy /= DRAG; }
		
		double speed = Math.sqrt(dx*dx + dy*dy);
    	if (speed != 0 && speed > MAX_SPEED) { //divide by zero errors are bad
    		dx /= speed;
    		dy /= speed; //now they are normalised
    	}
		
    	blackHole();
    	selfCol();
    	
    	speed = Math.sqrt(dx*dx + dy*dy);
    	if (speed != 0 && speed > MAX_SPEED) { //divide by zero errors are bad
    		dx /= speed;
    		dy /= speed; //now they are normalised
    	}
	}
	
	public void selfCol() {
		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
		
		for (GameObject o: objects) {
			if (o instanceof PlayerBullet || o instanceof Player || o instanceof Border || o instanceof Camera || o.equals(GameObject.ROOT) || o instanceof Shield) {
				continue; //nothing, can't hit these things
			} else {
				double[] pos = o.getCollisionPosition();
				double distX = pos[0] - x;
				double distY = pos[1] - y;
				if ((distX*distX) + (distY*distY) < ((size/2)*(size/2) + (o.size/2)*(o.size/2))) {
					destroyAll();
				}
			}
		}
	}
	
	private void destroyAll() {
		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
		for (GameObject o: objects) {
			if (o instanceof Player || o instanceof Border || o instanceof Camera || o.equals(GameObject.ROOT)) {
				continue;
			}
			if (o instanceof SplitingSquare) {
				SplitingSquare s = (SplitingSquare) o;
				s.setSplitStatus();
			}
			if (o instanceof BlackHole) {
				BlackHole b = (BlackHole) o;
				BlackHole.ALL_THIS.remove(b);
				GameObject.ALL_OBJECTS.remove(b);
			}
			o.destroy();
		}
		GameEngine.curGame.lostLife();
	}
	
	public void blackHole() {
		//does things (that every object does, so do it in here)
		ArrayList<BlackHole> objects = new ArrayList<BlackHole>(BlackHole.ALL_THIS);
		
		for (BlackHole h: objects) {
			if (h.isInert()) {
				continue;
			}
			double distx = h.x - x;
			double disty = h.y - y;
			double dist = Math.sqrt(distx*distx + disty*disty);
			
			if (dist < h.size*BlackHole.SUCK_RADIUS/2) {
				dx += Math.min(h.size,(h.size*BlackHole.SUCK_RADIUS-dist))*distx*0.013/dist;
				dy += Math.min(h.size,(h.size*BlackHole.SUCK_RADIUS-dist))*disty*0.013/dist;
				
				if (dist < size*h.size/2) {
					//bad things.. (like, i died and the such)
					System.out.print(".");
				}
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.PLAYER].getTextureId());
		
		gl.glColor3d(colour[0], colour[1], colour[2]);
		Helper.square(gl);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		
		double[] s = GameEngine.getMousePos();
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
