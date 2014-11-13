import java.util.ArrayList;

import javax.media.opengl.GL2;


public class ConnectedTriangle extends MovingObject {

	public final static ArrayList<ConnectedTriangle> ALL_THIS = new ArrayList<ConnectedTriangle>();
	
	private final int MAX_SPEED = 3;
	private static final int score = 150;
	private final int LINK_STRENGTH = 20;
	
	private ConnectedTriangle partner;
	private int strength;
	private int rotSpeed = 1;
	
	/**@param in Partner if spawned with one, not needed as they find them on the fly
	 */
	ConnectedTriangle(double size, double[] colour, ConnectedTriangle in) {
		super(size, colour);
		ALL_THIS.add(this);
		strength = 0;
		partner = in;
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
		x += dx*dt*MAX_SPEED;
		y += dy*dt*MAX_SPEED;
		
		Helper.keepInside(this, Helper.SPLAT);
		
		angle += rotSpeed;
		
		selfCol();
		
		if (partner == null) {
			double[] a = new double[]{GameEngine.getPlayerPos()[0]-x,GameEngine.getPlayerPos()[1]-y};
			double dist = a[0]*a[0] + a[1]*a[1];
			if (dist != 0) {
				dx += a[0]/dist;
				dy += a[1]/dist;
			}
			blackHole();
		} else {
			double[] a = new double[]{partner.x-x,partner.y-y};
			double dist = a[0]*a[0] + a[1]*a[1] + 0.0001;
			if (dist > size*size*4 + partner.size*partner.size*4) {
				dx += a[0]/dist;
				dy += a[1]/dist;
			} else if (dist < size*size*4 + partner.size*partner.size*4) {
				dx -= a[0]/dist;
				dy -= a[1]/dist;
			}
		}
		
		double speed = Math.sqrt(dx*dx + dy*dy);
		if (speed != 0 && speed > 1) { //divide by zero errors are bad
			dx /= speed;
			dy /= speed; //now they are normalised yay
		}
	}
	
	public void destroy() {
		if (partner == null) {
			super.destroy();
			ALL_THIS.remove(this);
			GameEngine.curGame.addScore(score);
		} else {
			strength -= 1; //TODO balance
			partner.strength -= 1;
			if (strength <= 0) {
				super.destroy();
				ALL_THIS.remove(this);
				GameEngine.curGame.addScore(score);
				partner.strength = 0;
				partner.partner = null;
			}
		}
	}
	
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, GameEngine.textures[GameEngine.TRIANGLE].getTextureId());
		if (partner == null) {
			gl.glColor3d(colour[0], colour[2], colour[1]);
		} else {
			gl.glColor3d(colour[0], colour[1], colour[2]);
		}

		Helper.square(gl);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		
		if (partner != null) {
			gl.glPushMatrix();
			gl.glRotated(-angle, 0, 0, 1);
			gl.glColor3d(strength*colour[0], strength*colour[1], strength*colour[2]);
			gl.glBegin(GL2.GL_LINES);
				gl.glVertex2d(0,0);
				gl.glVertex2d(partner.x-x, partner.y-y);				
			gl.glEnd();
			gl.glPopMatrix();
		}
	}
}
