package game.objects;
import game.logic.*;
import java.util.LinkedList;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;


/** Extends GameObject with a decay that draws text at a global position.
 * @author Jake Murphy
 */
public class TextPopup extends GameObject {
	
	public static final LinkedList<TextPopup> ALL_THIS = new LinkedList<TextPopup>();
	
	private double decayTime;
	private String score;
	
	public TextPopup(double[] colour, String score, double time, double x, double y) {
		this.colour = colour;
		this.decayTime = time;
		this.score = score;
		this.x = x;
		this.y = y;
		ALL_THIS.add(this);
	}
	
	public TextPopup(double[] colour, int score, double time, double x, double y) {
		this.colour = colour;
		this.decayTime = time;
		this.score = Integer.toString(score*GameEngine.gameState.getMultiplier());
		this.x = x;
		this.y = y;
		ALL_THIS.add(this);
	}

	public double[] getCollisionPosition() { //will never collide
		return new double[]{Double.MAX_VALUE, Double.MAX_VALUE, 0};
	}
	
	public void update(double dt) {
		decayTime -= dt;
		
		if (decayTime < 0) {
			amHit(false);
		}
	}
	
	public void amHit(boolean hit) {
		super.amHit(hit);
		ALL_THIS.remove(this);
	}
	
	public void draw(GL2 gl) { //see last page of link
		GLUT glut = new GLUT();
		gl.glPushMatrix();
		gl.glTranslated(x, y, 0); // position the text
		gl.glScalef(0.004f, 0.004f, 0.004f); 
			// reduce rendering size, because its quite large otherwise
		
		gl.glColor4d(colour[0], colour[1], colour[2], colour[3]);
		gl.glLineWidth(2f);
		
		// render the text using a stroke font
		for (int i = 0; i < score.length(); i++) {
			char ch = score.charAt(i);
			glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, ch);
		}

		gl.glPopMatrix(); // restore model view
	}
}

//http://fivedots.coe.psu.ac.th/~ad/jg2/ch16/jogl2.pdf
