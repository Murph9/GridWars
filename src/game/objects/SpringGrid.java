package game.objects;

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
	
	public void update(double dt) {
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
		
				grid[i][j].update(dt, tempX, tempY);
			}
		}
	}
	
	@Override
	public double[] getCollisionPosition() { //never collides with anything important
		return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
	}
	
	public void drawSelf(GL2 gl) {
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glLineWidth(1); //a setting option maybe?
		
		gl.glColor4d(0.1, 0.1, 0.1, 0.5);
		gl.glBegin(GL2.GL_LINES);
			
			for (int i = 0; i < xCount-1; i++) { //grid lines across
				for (int j = 1; j < yCount-1; j++) {
					gl.glVertex2d(grid[i][j].x, grid[i][j].y);
					gl.glVertex2d(grid[i+1][j].x, grid[i+1][j].y);
				}
			}
			
			for (int i = 1; i < xCount-1; i++) { //grid lines vertical
				for (int j = 0; j < yCount-1; j++) {
					gl.glVertex2d(grid[i][j].x, grid[i][j].y);
					gl.glVertex2d(grid[i][j+1].x, grid[i][j+1].y);
				}
			}
			
		gl.glEnd();
		
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}

	//these methods are taken from http://maxgames.googlecode.com/svn/trunk/vectorzone/gridparttrail.bmx
	/**
	 * @param x location of the pull in x,y
	 * @param y
	 * @param size - effect of pull size
	 * @param strength - strength of the pull
	 */
	public void pull(double x, double y, int size, double strength) { //pull at location
		//blackholes pull(x,y,5+size,4)
		
		for (int i = 0; i < xCount-1; i++) { //grid lines across
			for (int j = 1; j < yCount-1; j++) {
				Cell curCell = grid[i][j]; //ease of use
				
				double diffx = curCell.x-x;
				double diffy = curCell.y-y;
				
				double diffxo = curCell.ox - curCell.x;
				double diffyo = curCell.oy - curCell.y;
				
				double dist = Math.sqrt(diffx*diffx+diffy*diffy);
				double disto = Math.sqrt(diffxo*diffxo + diffyo*diffyo);

				if (dist > 0 && dist < size) {
					if (disto < size) { //don't pull things that are to far away
						curCell.dx = -(diffx/dist)*strength;
						curCell.dy = -(diffy/dist)*strength;
					}
				}
			}
		}
	}
	
	public void Push(double x, double y, double effectSize, double strength) { //push from location x,y
		//player bullets push(x+dx*2,y+dy*2,3,0.025)
		
		for (int i = 0; i < xCount-1; i++) { //grid lines across
			for (int j = 1; j < yCount-1; j++) {
				
				Cell curCell = grid[i][j]; //ease of use
				
				double diffx = curCell.ox-x;
				double diffy = curCell.oy-y;
				
				if (diffx*diffx + diffy*diffy < effectSize*effectSize) {
				
					double diffxo = curCell.ox - curCell.x;
					double diffyo = curCell.oy - curCell.y;
					
					double dist = diffx*diffx + diffy*diffy;
					double disto = diffxo*diffxo + diffyo*diffyo;
					
//					if (dist > 1 && disto < 4) { //?
						curCell.dx += strength*diffx;
						curCell.dy += strength*diffy;
//					}
				}
			}
		}
		
	}
	
	public void Shockwave(double x, double y) { //shockwave at location
		for (int i = 0; i < xCount-1; i++) { //grid lines across
			for (int j = 1; j < yCount-1; j++) {
				grid[i][j].disrupt((grid[i][j].x-x), (grid[i][j].y-y));
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
	
	
		class Cell { //does NOT extend gameobject, as putting extra things in there is slow
		double x, y, dx, dy = 0;
		double ox, oy = 0; //original position of grid point
		
		Cell(double i, double j) {
			x = ox = i;
			y = oy = j;
		}

		public void update(double dt, double xrestore, double yrestore) {
			if (Math.abs(xrestore-x) > 0.04) //if restoring force is greater than 0.04, help it
				dx += (xrestore-x);
			if (Math.abs(yrestore-y) > 0.04) 
				dy += (yrestore-y);

			if (Math.abs(ox-x) > 0.02) { //if original position difference is really close, do stuff
				x += (ox-x)*0.02;
				dx += (ox-x)/2;
			} else {
				x = ox;
			}
			if (Math.abs(oy-y) > 0.02) {
				y += (oy-y)*0.02;
				dy += (oy-y)/2;
			} else {
				y = oy;
			}
			
			dx *= 0.95;
			dy *= 0.95; //strong decay because bounciness
			
			x += dx*dt;
			y += dy*dt;
		}
		
		public void disrupt(double xx, double yy) {
//			if (Math.abs(xx) > 8) xx = xx/16;
//			if (Math.abs(yy) > 8) yy = yy/16; //what is this bit for?
			//TODO not working
			
			dx += xx;
			dy += yy;
			
			double speed = dx*dx + dy*dy;
			if (speed > 7) { //TODO HARDCODED NUMBER
				dx /= speed;
				dy /= speed;
			}
		}
	}
}
