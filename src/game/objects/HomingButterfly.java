package game.objects;
import game.logic.*;

import java.util.ArrayList;

import javax.media.opengl.GL2;


public class HomingButterfly extends HomingObject {

	public final static ArrayList<HomingButterfly> ALL_THIS = new ArrayList<HomingButterfly>();
	public static double MAX_SPEED = 6;
	
	public HomingButterfly(double spawnTimer) {
		this(spawnTimer, 0.6, Engine.BLUE);
	}
	
	HomingButterfly(double spawnTimer, double size, double[] colour) {
		super(size, colour, MAX_SPEED);
		ALL_THIS.add(this);
		score = 10;
		
		this.spawnTimer = spawnTimer;

		new SoundEffect(Engine.SPAWN, 10 ,0).start();
	}
	
	public void selfCol() {
		for (HomingButterfly d: HomingButterfly.ALL_THIS) {
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
		gl.glBindTexture(GL2.GL_TEXTURE_2D, Engine.textures[Engine.BUTTERFLY].getTextureId()); //get id of the dot file
    	super.drawSelf(gl);
	}

	public void amHit(boolean ifPoints) {
		super.amHit(ifPoints);
		ALL_THIS.remove(this);
	}
}