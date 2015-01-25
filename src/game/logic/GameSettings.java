package game.logic;


/**
 * Stores all the settings that don't change over the course of the current game.
 * Made from the checkboxes and textboxes in the menu.
 * @author Jake Murphy
 */

public class GameSettings {
	
	private String name;
	
	private int pixelWidth, pixelHeight;
	private int boardWidth, boardHeight;
	private double scale;
	
	private boolean ifSound;
	private boolean ifParticles;
	private boolean ifAliasing;
	
	GameSettings() {
		this(1024, 728, 16, 12, 10); //default
	}
	
	/** ...
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
	}
	
	public void setIfSound(boolean ni) {
		ifSound = ni;
	}
	public void setIfParticles(boolean ni) {
		ifParticles = ni; //i really don't know why its called ni (even with the holy grail reference) 
	}
	public void setIfAliasing(boolean ni) {
		ifAliasing = ni;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPixelWidth()  { return pixelWidth;  }
	public int getPixelHeight() { return pixelHeight; }
	public int getBoardWidth()  { return boardWidth;  }
	public int getBoardHeight() { return boardHeight; }
	
	public double getScale() { return scale; }
	public String getName()  { return name;  }
	
	public boolean ifSound()       { return ifSound;     }
	public boolean ifParticles()   { return ifParticles; }
	public boolean ifAliasing()    { return ifAliasing;  }



	public void setPixelWidth(int width) {
		pixelWidth = width;
	}
	public void setPixelHeight(int height) {
		pixelHeight= height;
	}
	
}
