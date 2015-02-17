package game.objects;
import game.logic.*;

import java.util.ArrayList;

import javax.media.opengl.GL2;


public class ConnectedTriangle extends MovingObject {

	public final static ArrayList<ConnectedTriangle> ALL_THIS = new ArrayList<ConnectedTriangle>();
	
	private final int MAX_SPEED = 4;
	private final int LINK_STRENGTH = 20;
	
	private ConnectedTriangle partner;
	private int strength;
	private int rotSpeed = 1;
	
	public ConnectedTriangle() {
		this(1, Engine.ORANGE, null);
	}
	
	/**@param in Partner if spawned with one (for whatever reason), but they can find them later
	 */
	ConnectedTriangle(double size, double[] colour, ConnectedTriangle in) {
		super(size, colour);
		ALL_THIS.add(this);
		strength = 0; //TODO, balance :D
		partner = in;
		score = 150;
		SoundEffect.SHOOT.play(10, 0);
	}

	@Override
	public void selfCol() {
		for (ConnectedTriangle d: ConnectedTriangle.ALL_THIS) {
			if (!d.equals(this)) { //because that would be silly
				double distX = d.x - x;
				double distY = d.y - y;
				double dist = (distX*distX) + (distY*distY) + 0.0001;
				
				if (dist < size*size*4*2 && d.partner == null && this.partner == null) { //if close and after a partner
					this.partner = d;
					this.strength = LINK_STRENGTH;
					d.partner = this;
					d.strength = LINK_STRENGTH;
				}
				
				if (dist < 0.5*((size*size)+(d.size*d.size))) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}

	@Override
	public void update(double dt) {
		if (this.spawnTimer > 0) {
			this.spawnTimer -= dt;
			return;
		}
		
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		
		Helper.keepInside(this, Helper.SPLAT);
		
		angle += rotSpeed;
		
		selfCol();
		
		if (partner == null) {
			double[] a = new double[]{Engine.getPlayerPos()[0]-x,Engine.getPlayerPos()[1]-y};
			double dist = a[0]*a[0] + a[1]*a[1];
			if (dist != 0) {
				dx += a[0]/dist;
				dy += a[1]/dist;
			}
			blackHole();
		} else {
			double[] a = new double[]{partner.x-x,partner.y-y};
			double dist = a[0]*a[0] + a[1]*a[1] + 0.0001; //no divide by zero for me
			
			double b = size*size*4 + partner.size*partner.size*4;
			dx += a[0]*(dist - b)/300; //change if you want it to do normal things
			dy += a[1]*(dist - b)/300;
		}
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised yay
		}
	}
	
	public void amHit(boolean ifPoints) {
		if (partner == null) {
			super.amHit(ifPoints);
			ALL_THIS.remove(this);
		} else {
			strength--;
			if (strength <= 0) {
				super.amHit(ifPoints);
				
				partner.strength = 0;
				partner.partner = null;
				ALL_THIS.remove(this);
			}
		}
	}
	
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.TRIANGLE].getTextureId());
		gl.glColor4d(colour[0], colour[1],colour[2],colour[3]);
		if (partner != null) {
			gl.glPushMatrix();
			gl.glRotated(60,0,0,1);
			Helper.square(gl);
			gl.glPopMatrix();
		}
		Helper.square(gl);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		
		if (partner != null) { //yes i know this will be drawn twice, im fine with that
			gl.glPushMatrix();
			gl.glRotated(-angle, 0, 0, 1);
			gl.glLineWidth((int) (0.2+(double)strength/8));
			gl.glColor3d(0.2+(double)strength/15, 0.2+(double)strength/15, 0.2+(double)strength/15);
			gl.glBegin(GL2.GL_LINES);
				gl.glVertex2d(0,0);
				gl.glVertex2d(partner.x-x, partner.y-y);				
			gl.glEnd();
			gl.glPopMatrix();
		}
	}
}
