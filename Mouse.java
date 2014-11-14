
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**A mouse handler for the game engine.
 * It keeps track of:
 *  1) the mouse position in world coordinates
 *  2) mouse press/release events that have happened in the past frame
 *
 * Mouse is a singleton class. There is only one instance, stored in Mouse.theMouse.

 * You need to add this object as a MouseListener and MouseMotionListener to your GLJPanel to make it work:
 * panel.addMouseListener(Mouse.theMouse);
 * panel.addMouseMotionListener(Mouse.theMouse);
 *
 * @author malcolmr
 */
public class Mouse extends MouseAdapter {

	public static final Mouse theMouse = new Mouse();
	private int[] myViewport;
	private double[] myProjMatrix;
	private double[] myMVMatrix;
	
	private MouseEvent myMouse;

	private boolean[] myPressed;
	private boolean[] myReleased;
	private boolean[] myWasPressed;
	private boolean[] myWasReleased;

	private Mouse() {
		myMouse = null;
		myViewport = new int[4];
		myProjMatrix = new double[16];
		myMVMatrix = new double[16];
		
		myMouse = null;

		myPressed = new boolean[3];
		myReleased = new boolean[3];
		myWasPressed = new boolean[3];
		myWasReleased = new boolean[3];
	}

	/**
	 * When the window is reshaped, store the new projection matrix and viewport
	 * @param gl
	 */
	public void reshape(GL2 gl) {
		gl.glGetIntegerv(GL2.GL_VIEWPORT, myViewport, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, myProjMatrix, 0);
	}

	/**
	 * When the view is updated, store the new model-view matrix.
	 * Update any mouse presses or releases that have happened.
	 * 
	 * @param gl
	 */
	public void update(GL2 gl) {
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, myMVMatrix, 0);
		
		for (int i = 0; i < 3; i++) {
			myWasPressed[i] = myPressed[i];
			myWasReleased[i] = myReleased[i];
			myPressed[i] = false;
			myReleased[i] = false;
		}
	}

	/**
	 * Get the current mouse position in world coordinates.
	 * @return
	 */
	public double[] getPosition() {
		double[] p = new double[3];
		if (myMouse != null) {
			int x = myMouse.getX();
			int y = myMouse.getY();
			GLU glu = new GLU();
			/* note viewport[3] is height of window in pixels */
			y = myViewport[3] - y - 1;

			glu.gluUnProject((double) x, (double) y, 0.0, //
					myMVMatrix, 0, myProjMatrix, 0, myViewport, 0, p, 0);

		}
		return p;
	}

	/**
	 * Store mouse movement events to record the latest mouse position.
	 * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		myMouse = e;
	}

	/**
	 * Store mouse movement events to record the latest mouse position.
	 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		myMouse = e;
	}
	
	/**
	 * Store the most recent mouse press event for each of the buttons.
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		myPressed[e.getButton()-1] = true;
	}
	
	/**
	 * Store the most recent mouse press release for each of the buttons.
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		myReleased[e.getButton()-1] = true;
	}
	
	/**
	 * Returns true if the specified mouse button was pressed this frame.
	 * @param button should be 1, 2 or 3
	 * @return
	 */
	public boolean wasPressed(int button) {
		return myWasPressed[button-1];
	}
	
	/**
	 * Returns true if the specified mouse button was released this frame.
	 * @param button should be 1, 2 or 3
	 * @return
	 */
	public boolean wasReleased(int button) {
		return myWasReleased[button-1];
	}

}
