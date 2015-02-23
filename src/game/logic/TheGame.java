package game.logic;

import java.awt.BorderLayout;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

/**Main menu init and game init, with any scores/settings
 * @author Jake Murphy
 */

public class TheGame {

	/**The frame for the menu*/
	private GameMenuGUI GUIFrame; 
	
	/**The frame for the game*/
	private JFrame gameFrame;
	
	private GLJPanel gamePanel; //used by the Engine
	private FPSAnimator animator;
	private Engine engine;
	
	private static final int DEFAULT_BOARD_WIDTH = 16; //at least 4x4
	private static final int DEFAULT_BOARD_HEIGHT= 12; //here is default
	
	private static final int DEFAULT_PIXEL_WIDTH = 1024; //at least 800x600
	private static final int DEFAULT_PIXEL_HEIGHT= 768; //default
	
 	public static void main(String[] args) {
		TheGame system = new TheGame();
		system.initMenu();
	}
	
	//draw the menu
	public void initMenu() {
		gameFrame = new JFrame();
		gameFrame.setTitle("GridWars");
		
		GUIFrame = new GameMenuGUI(this);
		GUIFrame.setTitle("GridWars");
		
		//////////////////////////////////////////////////
		////Other Things
		GUIFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GUIFrame.setName("Menu - Jake Murphy");
		
		GUIFrame.pack();
		GUIFrame.repaint();
		
		GUIFrame.setMinimumSize(GUIFrame.getSize()); //best GUI fix ever (and only one line), must be called after pack()
		GUIFrame.requestFocus();
		GUIFrame.setLocationRelativeTo(null);
		GUIFrame.setVisible(true);
	}
	
	//because its in the same file the settings dont't need to be passed in [:D]
	public void initGame(String difficulty) {
		GUIFrame.setVisible(false);
		
		if (!GUIFrame.hasValidScreenValues()) { //check if numbers
			System.err.println("Screen width and height must be integers");
			GUIFrame.setVisible(true);
			return;
		}
		
		if (!GUIFrame.hasValidBoardValues()) {
			System.err.println("Board width and height must be integers");
			GUIFrame.setVisible(true);
			return;
		}
		//if either of these don't work .get#####Height() won't work, then need to work
		
		GLProfile.initSingleton();
        GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		
		int pixelWidth = GUIFrame.getPixelWidth();
		int pixelHeight = GUIFrame.getPixelHeight();
		
		int boardWidth = GUIFrame.getBoardWidth();
		int boardHeight = GUIFrame.getBoardHeight();
		
		if (pixelWidth >= 800 && pixelHeight >= 600) { //the game really only works on resolutions bigger than this
			this.GUIFrame.setSize(pixelWidth, pixelHeight);
		} else {
			pixelWidth = TheGame.DEFAULT_PIXEL_WIDTH;
			pixelHeight = TheGame.DEFAULT_PIXEL_HEIGHT;
			System.out.println("setting defualt pixel sizes");
		}
		
		if (boardWidth >= 4 && boardHeight >= 4) { //the game really only works on game fields bigger than this
			this.GUIFrame.setSize(boardWidth, boardHeight);
		} else {
			boardWidth = TheGame.DEFAULT_BOARD_WIDTH;
			boardHeight = TheGame.DEFAULT_BOARD_HEIGHT;
			System.out.println("setting defualt game board sizes");
		}
		
		GameSettings set = GUIFrame.getSettings();
		
        //then write settings to file
        FileHelper.writeSettings(set);
        
        this.engine = new Engine(new SpawnHandler(difficulty), new GameState(difficulty), set);
        
        //////////////////////////////
        //JOGL Stuff:
        
		gamePanel = new GLJPanel(glcapabilities);
		gamePanel.addGLEventListener(engine);
		
		gameFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		gameFrame.setSize(pixelWidth, pixelHeight);
		
		gameFrame.setName("GridWars - Jake Murphy");
		gameFrame.setVisible(true);
		gameFrame.setFocusable(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        gamePanel.addKeyListener(Engine.player);
        gamePanel.addMouseListener(Engine.player);
        
        gamePanel.addMouseMotionListener(Mouse.theMouse);
        gamePanel.addMouseListener(Mouse.theMouse);
        
        gamePanel.requestFocus();
        gameFrame.setLocationRelativeTo(null); //middle of the screen
        
        animator = new FPSAnimator(60); //why is it fine that its 0 here? [infinite FPS!!]
        animator.add(gamePanel);
        animator.start();
    }
	
	public static void reloadMenu(GameState state, String name) {
		System.out.println("(lost all lives)\n   - reloadMenu");
		FileHelper.writeScore(Engine.EASY_D, state.getScore(), "ME*", (int)state.getTime());
		FileHelper.addToStats(state);
		
		
	}

}
