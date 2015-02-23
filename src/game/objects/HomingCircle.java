package game.objects;
import game.logic.*;

import java.util.ArrayList;

import javax.media.opengl.GL2;


public class HomingCircle extends HomingObject {

	public final static ArrayList<HomingCircle> ALL_THIS = new ArrayList<HomingCircle>();
	
	public static final int MAX_SPEED = 5;
	
	public HomingCircle(double spawnTimer) {
		this(spawnTimer, 0.6, Engine.PURPLE);
	}
	
	HomingCircle(double spawnTimer, double size, double[] colour) {
		super(size, colour, MAX_SPEED);
		ALL_THIS.add(this);
		score = 10;
		
		this.spawnTimer = spawnTimer;
	}
	
	public void selfCol() {
		for (HomingCircle d: HomingCircle.ALL_THIS) {
			if (!d.equals(this)) { //because that would be silly
				double distX = d.x - x;
				double distY = d.y - y;
				double dist = (distX*distX) + (distY*distY) + 0.0001;
				if (dist < 0.5*((size*size)+(d.size*d.size))) {
					dx -= Helper.sgn(distX)/(12*Math.sqrt(dist));
					dy -= Helper.sgn(distY)/(12*Math.sqrt(dist));
				}
			}
		}
	}
	
	public void blackHole() {
		//don't think it actually does anything in this
		
		ArrayList<BlackHole> objects = new ArrayList<BlackHole>(BlackHole.ALL_THIS);
		
		for (BlackHole h: objects) {
			if (h.isInert() || h.equals(this)) { //just incase a black hole targets itself..
				continue;
			}
			double distx = h.x - x;
			double disty = h.y - y;
			double dist = Math.sqrt(distx*distx + disty*disty);
			
			if (dist < size*h.size/2) {
				amHit(false);
				h.giveObject(distx, disty);
			}
		}
	}
	
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.CIRCLE].getTextureId()); //get id of the dot file
    	super.drawSelf(gl);
	}

	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		ALL_THIS.remove(this);
	}
}