package game.logic;


/**
 * Stores all the settings that don't change over the course of the current game.
 * Made from the checkboxes and textboxes in the menu.
 * @author Jake Murphy
 */

public class GameSettings {
	
	private String name;
	private String diff;
	private int gridXCount;
	private int gridYCount;
	
	private int pixelWidth, pixelHeight;
	private int boardWidth, boardHeight;
	private double scale;
	
	private boolean ifSound;
	private boolean ifParticles;
	private boolean ifAliasing;
	
	private boolean ifDebug;
	
	private int particlePercent;
	
	GameSettings() {
		this(1024, 728, 16, 12, 10); //default
	}
	
	/** init
	 * @param pixelWidth
	 * @param pixelHeight
	 * @param boardWidth
	 * @param boardHeight
	 * @param scale
	 */
	GameSettings(int pixelWidth, int pixelHeight, int boardWidth, int boardHeight, double scale) {
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
		
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		
		this.scale = scale;
		
		ifSound = true;
		ifAliasing = true; //default, change with setters if needed
		ifParticles = true;
		
		ifDebug = false;
		particlePercent = 100;
	}
	
	public void setIfSound(boolean ni) {
		ifSound = ni;
	}
	public void setIfParticles(boolean ni) {
		ifParticles = ni; 
			//really don't know why its called ni (even with the holy grail reference) [can't type "in"] 
	}
	public void setIfAliasing(boolean ni) {
		ifAliasing = ni;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setDebug(boolean in) {
		this.ifDebug = in;
	}
	
	public void setParticlePercentage(int count) {
		if (count > 100) count = 100;
		if (count < 0) count = 0; //its a percentage
		particlePercent = count;
	}
	
	public void setDifficulty(String diff) {
		this.diff = diff;
	}
	public void setGridXCount(int in) { 
		this.gridXCount = in; 
	}
	public void setGridYCount(int in) { 
		this.gridYCount = in; 
	}
	
	public int getPixelWidth()  { return pixelWidth;  }
	public int getPixelHeight() { return pixelHeight; }
	public int getBoardWidth()  { return boardWidth;  }
	public int getBoardHeight() { return boardHeight; }
	
	public double getScale() { return scale; }
	public String getName()  { return name;  }
	public String getDifficulty()  { return diff; }
	
	public int getGridXCount() { return gridXCount; }
	public int getGridYCount() { return gridYCount; }
	
	public boolean ifSound()      { return ifSound;     }
	public boolean ifParticles()  { return ifParticles; }
	public boolean ifAliasing()   { return ifAliasing;  }
	
	public boolean ifDebug() { return ifDebug; }
	
	public int getParticlePercentage() { return particlePercent; }


	public void setPixelWidth(int width) {
		pixelWidth = width;
	}
	public void setPixelHeight(int height) {
		pixelHeight= height;
	}
}
