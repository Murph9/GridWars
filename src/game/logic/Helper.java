package game.logic;
import game.objects.*;

import javax.media.opengl.GL2;

//class for just cleaning up some of the files (mainly methods that do simple long things)
public class Helper {
	
	public static final int NOTHING = 0;
	public static final int BOUNCE = 1;
	public static final int SPLAT = 2;
	
	public static int sgn(int a) {
		if (a > 0) return 1;
		else if (a < 0) return -1;
		else return (GameEngine.rand.nextInt(2)*2 -1); //i was trying to make them move when they didn't want to
	}
	public static double sgn(double a) {
		if (a > 0) return 1;
		else if (a < 0) return -1;
		else return (GameEngine.rand.nextInt()*2 -1); //yes i know i used nextInt(), it must be full or nothing
	}
	
	/**Draw a gl square centered about the position of the object
	 * @param gl
	 */
	public static void square (GL2 gl) {
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
	}
	
	/**for things that collide off the play arena walls
	 * @param a The object to bounce
	 * @param type if bouncy (use the static fields: NOTHING, BOUNCE, SPLAT )
	 */
	public static void keepInside(MovingObject a, int type) {
		if (a.x > GameEngine.curGame.getBoardWidth()-(a.size/2)) {
			if (type == BOUNCE) a.dx = -a.dx;
			else if (type == SPLAT)	a.dx = 0;
			a.x = GameEngine.curGame.getBoardWidth()-(a.size/2);
		} else if (a.x < -GameEngine.curGame.getBoardWidth()+(a.size/2)) {
			if (type == BOUNCE) a.dx = -a.dx;
			else if (type == SPLAT)	a.dx = 0;
			a.x = -GameEngine.curGame.getBoardWidth()+(a.size/2);
		}
		
		if (a.y > GameEngine.curGame.getBoardHeight()-(a.size/2)) {
			if (type == BOUNCE) a.dy = -a.dy;
			else if (type == SPLAT)	a.dy = 0;
			a.y = GameEngine.curGame.getBoardHeight()-(a.size/2);
		} else if (a.y < -GameEngine.curGame.getBoardHeight()+(a.size/2)) {
			if (type == BOUNCE) a.dy = -a.dy;
			else if (type == SPLAT)	a.dy = 0;
			a.y = -GameEngine.curGame.getBoardHeight()+(a.size/2);
		}
	}

	//because we aren't changing scale, and we only translate
		//we could use some fancy o1, o2, and scale to work this out
	//but remember we don't have any gl objects here....
	public static double[] getScreenPos(double x, double y, double scale) {
		double a = GameEngine.getMousePos()[0];
		double b = GameEngine.getMousePos()[1]; //translation of screen

		double[][] matrix = {
			{scale,0,0,a},
			{0,scale,0,b},
			{0,0,scale,0},
			{0,0,0,1    }};

			//the aspect can be computed from here:
		int[] t = new int[] {GameEngine.curGame.getPixelWidth(), GameEngine.curGame.getPixelHeight()}; 
		double ar = (double)t[0] / (double)t[1];
        double[][] viewProj = null;
		if (ar >= 1) {
			double[][] mat2 = {
					{1,0,0,0},
					{0,-1,0,0},
					{0,0,1,0},
					{0,0,0,ar}};
			viewProj = multiply(matrix, mat2);
        } else {
        	double[][] mat2 = {
					{1,0,0,0},
					{0,-1,0,0},
					{0,0,ar,0},
					{0,0,0,1}};
        	viewProj = multiply(matrix, mat2);
        }
		
        double[] vector = new double[]{x, -y, 0, 1};
        double[] pos = multiply(viewProj, vector);
        
        int winX = (int) (((pos[0] + 1)) * t[0]/25);
        int winY = (int) (((pos[1] + 1)) * t[1]/25);
		return new double[] {winX, winY};
	}
	
	
	//// helper functions
	public static double[][] multiply(double[][] p, double[][] q) {
        double[][] m = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                m[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                   m[i][j] += p[i][k] * q[k][j];
                }
            }
        }
        return m;
    }
	
	public static double[] multiply(double[][] m, double[] v) {
        double[] u = new double[3];
        for (int i = 0; i < 3; i++) {
            u[i] = 0;
            for (int j = 0; j < 3; j++) {
                u[i] += m[i][j] * v[j];
            }
        }
        return u;
    }
}