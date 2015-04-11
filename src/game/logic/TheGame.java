package game.logic;

import java.awt.BorderLayout;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jogamp.opengl.util.FPSAnimator;

/**Main menu init and game init, with any scores/settings
 * @author Jake Murphy
 */

public class TheGame {

	/**The frame for the menu*/
	private static GameMenu GUIFrame; 
	
	/**The frame for the game*/
	private static JFrame gameFrame;
	
	private static GLCanvas gamePanel; //used by the Engine
	private static FPSAnimator animator;
	private static Engine engine;
	
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
		gameFrame.setTitle("GridWars Game");
		
		GUIFrame = new GameMenu(this);
		GUIFrame.setTitle("GridWars Menu");
		
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
	
	//initalise the game to have working values
	public void initGame(String difficulty) {
		if (!readGUI()) return; //make sure the gui has logical values

		if (engine != null) { //i.e not the first run of the game
			gameFrame.dispose();
	        
	        gameFrame = new JFrame();
			gameFrame.setTitle("GridWars Game");
		}
		
		engine = new Engine(new SpawnHandler(difficulty), new GameState(difficulty), GUIFrame.getSettings());
		
		GLProfile.initSingleton();
        GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		
        //////////////////////////////
        //JOGL Stuff:
		gamePanel = new GLCanvas(glcapabilities);
		gamePanel.addGLEventListener(engine);
		
		gameFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		gameFrame.setSize(GUIFrame.getPixelWidth(), GUIFrame.getPixelHeight());
		
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
        
        animator = new FPSAnimator(60); //why is it fine that its 0 here? [infinite FPS!]
        animator.add(gamePanel);
        animator.start();
    }
	
	private boolean readGUI() {
		if (!GUIFrame.hasValidScreenValues()) { //check if numbers
			System.err.println("Screen width and height must be integers");
			GUIFrame.setVisible(true);
			return false;
		}
		
		if (!GUIFrame.hasValidBoardValues()) {
			System.err.println("Board width and height must be integers");
			GUIFrame.setVisible(true);
			return false;
		}
		//if either of these don't work .get#####Height() won't work, they need to work
		
		int pixelWidth = GUIFrame.getPixelWidth();
		int pixelHeight = GUIFrame.getPixelHeight();
		
		int boardWidth = GUIFrame.getBoardWidth();
		int boardHeight = GUIFrame.getBoardHeight();
		
		if (pixelWidth >= 800 && pixelHeight >= 600) { //the game really only works on resolutions bigger than this
			GUIFrame.setSize(pixelWidth, pixelHeight);
		} else {
			pixelWidth = TheGame.DEFAULT_PIXEL_WIDTH;
			pixelHeight = TheGame.DEFAULT_PIXEL_HEIGHT;
			System.out.println("setting defualt pixel sizes "+DEFAULT_PIXEL_HEIGHT +" x "+DEFAULT_PIXEL_WIDTH);
		}
		
		if (boardWidth >= 4 && boardHeight >= 4) { //the game really only works on game fields bigger than this
			GUIFrame.setSize(boardWidth, boardHeight);
		} else {
			boardWidth = TheGame.DEFAULT_BOARD_WIDTH;
			boardHeight = TheGame.DEFAULT_BOARD_HEIGHT;
			System.out.println("setting defualt game board sizes"+DEFAULT_BOARD_HEIGHT +" x "+DEFAULT_BOARD_WIDTH);
		}
		
        //then write settings to file
        FileHelper.writeSettings(GUIFrame.getSettings());
        
        GUIFrame.setVisible(false);
		return true;
	}
	
	public static void reloadMenu(GameState state, String name) {
		FileHelper.writeScore(Engine.EASY_D, state.getScore(), name, (int)state.getTime());
		FileHelper.addToStats(state);
		
		GUIFrame.setVisible(true);
		gameFrame.setVisible(false);
		
		JOptionPane.showMessageDialog(null, "\n"+state);
	}
}
