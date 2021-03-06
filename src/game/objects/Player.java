package game.objects;

import game.logic.*;

import java.awt.event.*;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.swing.JOptionPane;


public class Player extends MovingObject implements KeyListener, MouseListener {

	public final static double SHOOT_RATE = 0.14;
	public final static double ACCEL = 1;
	public final static double MAX_SPEED = 6.5;
	
	public double playerDrag = 1.04; //user setting
	
	private boolean xPosAccel, xNegAccel, yPosAccel, yNegAccel;
	
	private double shootTimer;
	private boolean shooting;
	
	private double shieldAngle;
	
	public Player(double size, double[] colour) {
		super(size, colour);
		
		double[] myPos = getPosition();
		double[] s = Engine.getMousePos();
		double angle = Math.toDegrees(Math.atan2((s[1]-myPos[1]), (s[0]-myPos[0])));
		setRotation(angle);
		
		xPosAccel = xNegAccel = yPosAccel = yNegAccel = false;
		
		shootTimer = SHOOT_RATE;
		
		playerDrag = 1 + ((double)Engine.settings.inertia)/100; //its defined to be hundredth of the drag
		
		this.shooting = true;
		this.spawnTimer = 0; //doesn't have spawn delay
	}
	
	private void shoot() { //creates PlayerBullets, large
		if (!shooting) {
			return;
		}
		if (!Engine.canSpawn()) {
			return;
    	}
		
		int num = Engine.gameState.getBulletCount();
		double speed = MAX_SPEED*Engine.gameState.getBulletSpeed();
		
		switch(num) {
		case 4:
			MovingObject b0a = new PlayerBullet(0.35, Engine.LIGHT_YELLOW);
				b0a.x = x; b0a.y = y;
				
				b0a.dx = (dx/2+2*Math.cos(Math.toRadians(angle+5)))*speed;
				b0a.dy = (dy/2+2*Math.sin(Math.toRadians(angle+5)))*speed;

			MovingObject b0b = new PlayerBullet(0.35, Engine.LIGHT_YELLOW);
				b0b.x = x; b0b.y = y;
				
				b0b.dx = (dx/2+2*Math.cos(Math.toRadians(angle-5)))*speed;
				b0b.dy = (dy/2+2*Math.sin(Math.toRadians(angle-5)))*speed;
				
			//note the lack of break; here, important (if it was more than 4 i would have a formula)
		case 2:
			MovingObject b1 = new PlayerBullet(0.35, Engine.LIGHT_YELLOW);
				b1.x = x; b1.y = y;
				
				b1.dx = (dx/2+2*Math.cos(Math.toRadians(angle+2)))*speed;
				b1.dy = (dy/2+2*Math.sin(Math.toRadians(angle+2)))*speed;
			
			MovingObject b2 = new PlayerBullet(0.35, Engine.LIGHT_YELLOW);
				b2.x = x; b2.y = y; 
				
				b2.dx = (dx/2+2*Math.cos(Math.toRadians(angle-2)))*speed;
				b2.dy = (dy/2+2*Math.sin(Math.toRadians(angle-2)))*speed;
			break;
		case 3:
			MovingObject b3 = new PlayerBullet(0.35, Engine.LIGHT_YELLOW);
			MovingObject b4 = new PlayerBullet(0.35, Engine.LIGHT_YELLOW);
			MovingObject b5 = new PlayerBullet(0.35, Engine.LIGHT_YELLOW);
			
			b3.x = x; b3.y = y;
			b4.x = x; b4.y = y;
			b5.x = x; b5.y = y;
			
			//velocity
			b3.dx = (dx/2+2*Math.cos(Math.toRadians(angle+5)))*speed;
			b3.dy = (dy/2+2*Math.sin(Math.toRadians(angle+5)))*speed;
			
			b4.dx = (dx/2+2*Math.cos(Math.toRadians(angle)))*speed;
			b4.dy = (dy/2+2*Math.sin(Math.toRadians(angle)))*speed;
			
			b5.dx = (dx/2+2*Math.cos(Math.toRadians(angle-5)))*speed;
			b5.dy = (dy/2+2*Math.sin(Math.toRadians(angle-5)))*speed;
			break;
		}
		
		
		if (Engine.gameState.ifRearShot() > 0) {
			MovingObject b12 = new PlayerBullet(0.35, Engine.YELLOW);
			b12.x = -Math.cos(Math.toRadians(angle))*size/2 + x;
			b12.y = -Math.sin(Math.toRadians(angle))*size/2 + y;
			
			b12.dx = (dx/2-2*Math.cos(Math.toRadians(angle)))*speed;
			b12.dy = (dy/2-2*Math.sin(Math.toRadians(angle)))*speed;
		}
		
		if (Engine.gameState.ifSideShot() > 0) {
			MovingObject b12 = new PlayerBullet(0.35, Engine.YELLOW);
			b12.x = Math.cos(Math.toRadians(angle+90))*size/2 + x;
			b12.y = Math.sin(Math.toRadians(angle+90))*size/2 + y;
			
			b12.dx = (dx/2+2*Math.cos(Math.toRadians(angle+90)))*speed;
			b12.dy = (dy/2+2*Math.sin(Math.toRadians(angle+90)))*speed;
			
			MovingObject b13 = new PlayerBullet(0.35, Engine.YELLOW);
			b13.x = Math.cos(Math.toRadians(angle-90))*size/2 + x;
			b13.y = Math.sin(Math.toRadians(angle-90))*size/2 + y;
			
			b13.dx = (dx/2+2*Math.cos(Math.toRadians(angle-90)))*speed;
			b13.dy = (dy/2+2*Math.sin(Math.toRadians(angle-90)))*speed;
		}
	}

	public void update(double dt) {
		x += dx*dt*MAX_SPEED; //add it
    	y += dy*dt*MAX_SPEED;
    	
    	Helper.keepInside(this, Helper.SPLAT);
		
    	//shoot:
    	shootTimer -= dt;
    	if (shootTimer < 0) {
    		shoot();
    		shootTimer = SHOOT_RATE;
    	}
    	
		//do rotation stuff
		double[] s = Engine.getMousePos();
		this.angle = Math.toDegrees(Math.atan2((s[1]-y), (s[0]-x)));
		if (angle > 0) angle += 360;

		double thresh = Engine.rand.nextDouble();
		if (Math.sqrt(dx*dx + dy*dy) > thresh) { //only matters if going less than max speed
			MovingObject q = new Particle(2, Engine.BLUE, 1, Particle.DEFAULT_DRAG); //also not really sure why blue but hey..
			q.x = x; 
			q.dx = (-dx*4 - (s[0] -x)/2)*(Engine.rand.nextDouble()*2);
			
			q.y = y; 
			q.dy = (-dy*4 - (s[1] -y)/2)*(Engine.rand.nextDouble()*2);
			
			MovingObject r = new Particle(2, Engine.BLUE, 1, Particle.DEFAULT_DRAG);
			r.x = x+0.1*(Engine.rand.nextDouble()*2 -1); 
			r.y = y+0.1*(Engine.rand.nextDouble()*2 -1);
		}
		
		double shield = Engine.gameState.ifTempShield();
		if (shield > 0) {
			if (shield > 1) shield = 1;
			shieldAngle += dt*100*shield; //slow down of shield
		}
		
		//acceleration calculations 
		if		(xPosAccel) { dx += ACCEL; }
		else if	(xNegAccel) { dx -= ACCEL; }
		else 				{ dx /= playerDrag; }
		
		if		(yPosAccel) { dy += ACCEL; }
		else if	(yNegAccel) { dy -= ACCEL; }
		else 				{ dy /= playerDrag; }
		
    	blackHole();
    	selfCol();
    	
    	double speed = Math.sqrt(dx*dx + dy*dy);
    	if (speed != 0 && speed > 1) {
    		dx /= speed;
    		dy /= speed; //now they are normalised
    	}
	}
	
	/**In this class it is for checking if a life is going to be lost 
	 * rather than pushing on other similar objects like all the other selfCol()
	 */
	public void selfCol() {
		if (Engine.gameState.ifTempShield() > 0) { //if shield active, can still get powerups, but not other objects
			ArrayList<PowerUp> list = new ArrayList<PowerUp>(PowerUp.ALL_THIS);
			for (PowerUp p: list) {
				double[] pos = p.getCollisionPosition();
				double distX = pos[0] - x;
				double distY = pos[1] - y;
				if ((distX*distX) + (distY*distY) < ((size/2)*(size/2) + (p.size/2)*(p.size/2))) {
					p.amHit(true);
				}
			}
			return; //because we are done checking for powerups
		}
		ArrayList<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);
		
		for (GameObject o: objects) {
			if (o instanceof PlayerBullet || o instanceof Player || o instanceof Border || o instanceof Camera || o instanceof Shield || o instanceof Particle) {
				continue; //nothing, can't hit these things TODO make sure these have a collision position of really far away
			} else {
				double[] pos = o.getCollisionPosition();
				double distX = pos[0] - x;
				double distY = pos[1] - y;
				if ((distX*distX) + (distY*distY) < ((size/2)*(size/2) + (o.size/2)*(o.size/2))) {
					if (o instanceof PowerUp) {
						o.amHit(true);
					} else {
						Engine.lostLife(o);
					}
				}
			}
		}
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
			
			if (dist < h.size*BlackHole.SUCK_RADIUS) {
				dx += Math.min(h.size,(h.size*BlackHole.SUCK_RADIUS-dist))*distx*0.013/dist;
				dy += Math.min(h.size,(h.size*BlackHole.SUCK_RADIUS-dist))*disty*0.013/dist;
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.PLAYER].getTextureId());
		
		gl.glColor4d(Engine.WHITE[0], Engine.WHITE[1], Engine.WHITE[2], Engine.WHITE[3]);
		Helper.square(gl);
		
		double shield = Engine.gameState.ifTempShield();
		if (shield > 0) {
			if (shield > 1) shield = 1;
			gl.glColor4d(shield, shield, shield, 0.5); //decay of shield..
			
			gl.glPushMatrix();
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.PLAYER_SHEILD].getTextureId());
			
			gl.glRotated(shieldAngle - angle,0,0,1);
			gl.glScaled(2, 2, 1);
			Helper.square(gl);
			
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.PLAYER_SHEILD].getTextureId());
			
			gl.glRotated(-shieldAngle - angle,0,0,1);
			gl.glScaled(2, 2, 1);
			Helper.square(gl);
			
			gl.glPopMatrix();
		}
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		
		double[] s = Engine.getMousePos();
		double[] myPos = getPosition();
		gl.glRotated(-angle, 0, 0, 1);
		
		gl.glLineStipple(1, (short)0xAA); //google obviously
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
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { //escape is a good pause button I think
			Engine.togglePause();
		}
		if (e.getKeyCode() == KeyEvent.VK_1) {
			Engine.toggleDebug();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_Q && Engine.isPaused()) { //quit on a 'q' press while paused
			int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to quit?", "Confirm",
			        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if (dialogResult == JOptionPane.YES_OPTION) {
				Engine.gameQuit();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			shooting = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(!Engine.canSpawn()) {
			return;
		}
		if (arg0.getButton() == MouseEvent.BUTTON3) {
			Engine.gameState.useBomb();
		}
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			shooting = true;
		}
	}

}
