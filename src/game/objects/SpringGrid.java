package game.objects;

import game.logic.Helper;

import javax.media.opengl.GL2;

public class SpringGrid extends GameObject{
	
	private int xCount;
	private int yCount;
	
	private double xinc; //for calculating the correct position later
	private double yinc;
	
	private Cell grid[][];
	
	public SpringGrid(int xCount, int yCount, int width, int height) {
		this.xCount = xCount;
		this.yCount = yCount;
		
		xinc = (double)width/(double)(xCount-1);
		yinc = (double)height/(double)(yCount-1);
		
		grid = new Cell[xCount][yCount];
		for (int i = 0; i < xCount; i++) {
			for (int j = 0; j < yCount; j++) {
				grid[i][j] = new Cell((i-xCount/2)*2*xinc + xinc, (j-yCount/2)*2*yinc + yinc);
			}
		}
	}
	
	public void update(double dt) { //please stop touching this method, it doesn't like it
		for (int i = 1; i < xCount-1; i++) {
			for (int j = 1; j < yCount-1; j++) {
				
				double tempX = 0;
				tempX += grid[i-1][j].x;
				tempX += grid[i][j-1].x;
				tempX += grid[i][j+1].x;
				tempX += grid[i+1][j].x;
				tempX /= 4;
				
				double tempY = 0;
				tempY += grid[i-1][j].y;
				tempY += grid[i][j-1].y;
				tempY += grid[i][j+1].y;
				tempY += grid[i+1][j].y;
				tempY /= 4;
		
				grid[i][j].update(dt, tempX-grid[i][j].x, tempY-grid[i][j].y);
			}
		}
	}
	
	@Override
	public double[] getCollisionPosition() { //never collides with anything important
		return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
	}
	
	//these methods are taken from http://maxgames.googlecode.com/svn/trunk/vectorzone/gridparttrail.bmx
	/**Pull grid for a gravity look
	 * @param x x origin of pull
	 * @param y y origin of pull
	 * @param size - effect of pull size
	 * @param strength - strength of the pull
	 */
	public void pullGrid(double x, double y, double size, double strength) { //pull at location
		for (int i = 0; i < xCount-1; i++) {
			for (int j = 1; j < yCount-1; j++) {
				Cell c = grid[i][j]; //ease of use
				
				double diffx = c.x-x;
				double diffy = c.y-y;
				double dist = Math.sqrt(diffx*diffx+diffy*diffy);

				double diffox = c.ox-x;
				double diffoy = c.oy-y;
				double disto = Math.sqrt(diffox*diffox+diffoy*diffoy);

				if (dist > 0.1 && dist < size && disto < size) {
					c.dx -= strength*(diffx/dist);
					c.dy -= strength*(diffy/dist); 
				}
				if (dist < 0.3) {
					c.x = x;
					c.y = y;
					c.dx = 0;
					c.dy = 0;
						
				}
			}
		}
	}
	
	/**Push grid for an explosion look
	 * @param x x origin of pull
	 * @param y y origin of pull
	 * @param size - effect of pull size
	 * @param strength - strength of the pull
	 */
	public void pushGrid(double x, double y, double size, double strength) { //push from location x,y TODO doesn't quite work yet
		for (int i = 0; i < xCount-1; i++) {
			for (int j = 1; j < yCount-1; j++) {
				
				Cell curCell = grid[i][j]; //ease of use
				
				double diffx = curCell.ox-x;
				double diffy = curCell.oy-y;
				
				if (diffx*diffx + diffy*diffy < size*size) {
				
//					double diffxo = curCell.ox - curCell.x;
//					double diffyo = curCell.oy - curCell.y;
					
					double dist = diffx*diffx + diffy*diffy;
//					double disto = diffxo*diffxo + diffyo*diffyo;
					
					if (dist > 0.02){// && disto < 0.1) { //only push it if its not inside, but not also far from original position
						curCell.dx += strength*diffx; //don't know why we check the 'dist' variable here
						curCell.dy += strength*diffy;
					}
				}
			}
		}//TODO this method doesn't work quite yet, try looking at engine.killall to test (line ~500)
		
	}
	
	public void shockwaveGrid(double x, double y, double size, double strength) { //shockwave at location
		for (int i = 0; i < xCount-1; i++) {
			for (int j = 1; j < yCount-1; j++) {
				Cell c = grid[i][j];
				double diffx = c.x - x;
				double diffy = c.y - y;
				if (diffx*diffx + diffy*diffy < size) {
					c.dx += diffx*strength;
					c.dy += diffy*strength;
				}
			}
		}
	}
	
	public void resetAll() {
		for (int i = 1; i < xCount-1; i++) {
			for (int j = 1; j < yCount-1; j++) {
				grid[i][j].x = (i-xCount/2)*2*xinc + xinc;
				grid[i][j].y = (j-yCount/2)*2*yinc + yinc;
				grid[i][j].dx = 0;
				grid[i][j].dy = 0;
			}
		}
	}
	
	
	//////////////////////////////////////////////////////////////////
	public void drawSelf(GL2 gl) {
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glLineWidth(1); //a setting option maybe?
		
		gl.glColor4d(0.1, 0.1, 0.1, 0.5);
		gl.glBegin(GL2.GL_LINES);
			
			double def = 0.03; //colour offset from zero (because then you couldn't see it otherwise)
			
			double xdif = Math.abs(grid[0][0].ox - grid[1][0].ox);
			double ydif = Math.abs(grid[0][0].oy - grid[0][1].oy);
			
			for (int i = 0; i < xCount-1; i++) { //grid lines across
				for (int j = 1; j < yCount-1; j++) {
					Cell c = grid[i][j];
					Cell d = grid[i+1][j];
					double diff = Math.sqrt((c.x-d.x)*(c.x-d.x)+(c.y-d.y)*(c.y-d.y))- xdif;

					diff = Math.max(Math.min(diff, 0.5), 0); //0 < x < 0.5
					
					gl.glColor4d(diff+def, diff+def, diff+def, 0.5); //x + def

					gl.glVertex2d(c.x, c.y);
					gl.glVertex2d(grid[i+1][j].x, grid[i+1][j].y);
				}
			}
			
			for (int i = 1; i < xCount-1; i++) { //grid lines vertical
				for (int j = 0; j < yCount-1; j++) {
					Cell c = grid[i][j];
					Cell d = grid[i][j+1];
					double diff = Math.sqrt((c.x-d.x)*(c.x-d.x)+(c.y-d.y)*(c.y-d.y)) - ydif;
					
					diff = Math.max(Math.min(diff, 0.5), 0); //0 < x < 0.5
					
					gl.glColor4d(diff+def, diff+def, diff+def, 0.5); //x + def

					gl.glVertex2d(c.x, c.y);
					gl.glVertex2d(grid[i][j+1].x, grid[i][j+1].y);
				}
			}
		gl.glEnd();

		gl.glColor4d(0.1, 0.1, 0.1, 0.5);
//		gl.glPointSize(5); 
		gl.glBegin(GL2.GL_POINTS);
			for (int i = 1; i < xCount-1; i++) { //grid dots
				for (int j = 0; j < yCount-1; j++) {
					Cell c = grid[i][j];
					gl.glVertex2d(c.x, c.y);
				}
			}
		gl.glEnd();
		
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}
	
	
	class Cell { //does NOT extend gameobject, as putting extra things in there would be slow
		double x, y, dx, dy = 0;
		double ox, oy = 0; //original position of the grid point, springs need this for their restoring force
		
		Cell(double i, double j) {
			x = ox = i;
			y = oy = j;
		}
		
		double xDiff() {
			return Math.abs(ox-x);
		}
		double yDiff() {
			return Math.abs(oy-y);
		}

		 //this method doesn't want to be touched either
		public void update(double dt, double xrestore, double yrestore) {
			if (Math.abs(xrestore) > 0.075) //this number bigger removes propogations in the mesh
				dx += Helper.sgn(xrestore);
			if (Math.abs(yrestore) > 0.075) //if this is <=0.035 its non-damped and doesn't stop bouncing (at 60 fps)
				dy += Helper.sgn(yrestore);

			if (Math.abs(ox-x) > 0.05) { //if original position difference is really close, do stuff
				x += (ox-x)*0.05;
				dx += (ox-x)/4;
			} else {
				x = ox;
			}
			if (Math.abs(oy-y) > 0.05) {
				y += (oy-y)*0.05;
				dy += (oy-y)/4;
			} else {
				y = oy;
			}
			
			dx *= 0.899;
			dy *= 0.899; //for some reason this is really stable
			
			x += dx*dt;
			y += dy*dt;
		}
		
		public void disrupt(double xx, double yy) {
			//placeholder for something that should be here
			//not to sure why it is here actually
		}
	}
}
