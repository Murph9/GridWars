package game.logic;


/**
 * Stores all the settings that don't change over the course of the current game.
 * Made from the checkboxes and textboxes in the menu.
 * @author Jake Murphy
 */

public class GameSettings {
	
	public String name;
	public Engine.Difficulty diff;
	public int gridXCount;
	public int gridYCount;
	
	public int inertia;
	public int particlePercent;
	
	public int pixelWidth, pixelHeight;
	public int boardWidth, boardHeight;
	public double scale;
	
	public boolean ifSound;
	public boolean ifParticles;
	public boolean ifAliasing;
	
	public boolean ifDebug;
	
	GameSettings() {
		name = "me";
		diff = Engine.Difficulty.easy;
		
		pixelWidth = 1024;
		pixelHeight = 728;
		boardWidth = 16;
		boardHeight = 12;
		particlePercent = 100;
		scale = 10;
		ifParticles = true;
		ifAliasing = true;
		ifSound = true;
		
		
		gridXCount = 100;
		gridYCount = 100;
		inertia = 4;
	}
	
	/** init
	 * @param pixelWidth
	 * @param pixelHeight
	 * @param boardWidth
	 * @param boardHeight
	 * @param scale
	 */
	GameSettings(int pixelWidth, int pixelHeight, int boardWidth, int boardHeight, double scale) {
		super();
		
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
		
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.scale = scale;
	}
}
