package game.logic;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jogamp.opengl.util.FPSAnimator;

//Handles menus and other things
//rather large class... TODO put the menu stuff somewhere else

/**Main menu and game initialisation.
 * 
 * @author Jake Murphy
 */

public class TheGame implements ActionListener {

	private JFrame theFrame; //reuseable frame that 
	private JPanel menuPanel; //for the menu
	
	private GLJPanel gamePanel; //for the GameEngine
	private FPSAnimator animator;
	private GameEngine engine;
	
	//settings checkboxes - add others... (thats not 'and')
	private JCheckBox sound;
	private JCheckBox particles;
	private JCheckBox antialiasing;
	
	private int boardWidth = 16, boardHeight = 12; //at least 4 please
	private double scale = 10; //you know, it kind of works, as easy as this number is 
	
	private static final int DEFAULT_PIXEL_WIDTH = 1024;
	private static final int DEFAULT_PIXEL_HEIGHT= 768;

	private JTextField gameWidth;
	private JTextField gameHeight;
	
	private GridBagConstraints gbLayout;
	private static JTextArea board;
	
	private ButtonGroup group;
	private final String easyB = "Easy", medB = "Med", hardB = "Hard";
	
	
 	public static void main(String[] args) {
		TheGame system = new TheGame();
		system.initMenu();
	}
	
	//draw the menu
	public void initMenu() {
		theFrame = new JFrame();
		
		menuPanel = new JPanel();
		menuPanel.setLayout(new GridBagLayout());
		theFrame.add(menuPanel);
		
		JPanel newGamePanel = new JPanel();
		newGamePanel.setLayout(new GridBagLayout());
		newGamePanel.setBorder(BorderFactory.createTitledBorder("New Game"));
		
		
		////////////////////////////////////////////////
		////Game Mode
		JRadioButton buttonEasy = new JRadioButton(easyB);
		buttonEasy.setActionCommand(GameEngine.EASY_D);
		
		JRadioButton buttonMed = new JRadioButton(medB);
		buttonMed.setActionCommand(GameEngine.MEDIUM_D);
		
		JRadioButton buttonHard = new JRadioButton(hardB);
		buttonHard.setActionCommand(GameEngine.HARD_D);
		
		group = new ButtonGroup();
		group.add(buttonEasy);
		group.add(buttonMed);
		group.add(buttonHard);
		
		buttonMed.setSelected(true); //because its hard coded somewhere else i think (in this file)
		
		buttonEasy.addActionListener(this);
		buttonMed.addActionListener(this);
		buttonHard.addActionListener(this);
		
		JButton go = new JButton("Go");
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();

		            if (button.isSelected()) {
		            	initGame(button.getActionCommand());
		            }
		        }
			}
		});
		
		JButton stats = new JButton("stats");
		stats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, LeaderBoard.getStats());
			}
		});
		
		GridBagConstraints a = new GridBagConstraints();
		a.fill = GridBagConstraints.HORIZONTAL;
		
		a.gridx = 0;
		a.gridy = 0;
		a.gridheight = 4;
		newGamePanel.add(go, a);
		
		a.gridheight = 1;
		a.gridx++;
		a.gridy++;
		newGamePanel.add(buttonEasy, a);
		a.gridy++;
		newGamePanel.add(buttonMed, a);
		a.gridy++;
		newGamePanel.add(buttonHard, a);
		
		a.gridwidth = 2;
		a.gridx = 0;
		a.gridy++;
		newGamePanel.add(stats, a);
		
		
		////////////////////////////////////////////////// 
		////Settings
		GridBagConstraints c2 = new GridBagConstraints();
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		
		//labels
		c2.gridx = 0;
		c2.gridy = 0;
		settingsPanel.add(new JLabel("Width"), c2);

		c2.gridx++;
		settingsPanel.add(new JLabel("Height"), c2);

		c2.fill = GridBagConstraints.HORIZONTAL;
		
		//textboxes
		c2.gridy++;
		c2.gridx = 0;
		gameWidth = new JTextField("null");
		settingsPanel.add(gameWidth, c2);
		
		c2.gridx++;
		gameHeight = new JTextField("null");
		settingsPanel.add(gameHeight, c2);

		//checkboxes
		c2.gridwidth = 2;
		c2.gridx = 0;
		c2.gridy++;
		particles = new JCheckBox("Particles");
		settingsPanel.add(particles, c2);
		
		c2.gridy++;
		antialiasing = new JCheckBox("Antialiasing");
		settingsPanel.add(antialiasing, c2);
		
		c2.gridy++;
		sound = new JCheckBox("Sound");
		settingsPanel.add(sound, c2);
		
		//////////////////////////////////////////////////
		////Heading Info + positions		
		gbLayout = new GridBagConstraints();
		gbLayout.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.insets = new Insets(5,5,5,5);
		
		JPanel headPanel = new JPanel();
		headPanel.setLayout(new GridBagLayout());
		
		JTextArea t = new JTextArea("Welcome to NotGridWars2\nArrow keys/WASD to move, right/left mouse button do things");
		t.setEditable(false);
		headPanel.add(t, gbLayout);
		
		gbLayout.gridy = 0;
		gbLayout.gridx = 0;
		gbLayout.gridwidth = 2;
		menuPanel.add(headPanel, gbLayout);
		
		gbLayout.gridy++;
		gbLayout.gridwidth = 1;
		menuPanel.add(newGamePanel, gbLayout);

		gbLayout.gridy++;
		menuPanel.add(settingsPanel, gbLayout);
		
		////Scoreboard
		board = LeaderBoard.getLeaderBoard(GameEngine.MEDIUM_D);
		
		gbLayout.gridx++;
		gbLayout.gridy = 1;
		gbLayout.gridheight = 2;
		menuPanel.add(board, gbLayout);
		
		//////////////////////////////////////////////////
		GameSettings fileSettings = LeaderBoard.readSettings();
		gameWidth.setText(""+fileSettings.getPixelWidth());
		gameHeight.setText(""+fileSettings.getPixelHeight());
		
		particles.setSelected(fileSettings.ifParticles());
		antialiasing.setSelected(fileSettings.ifAliasing());
		sound.setSelected(fileSettings.ifSound());
		
		//////////////////////////////////////////////////
		////Other Things
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.setName("Menu - Jake Murphy");
		
		theFrame.pack();
		theFrame.repaint();
		theFrame.requestFocus();
		theFrame.setLocationRelativeTo(null);
		theFrame.setVisible(true);
		
		buttonEasy.requestFocus(); //for easy pressing of space bar to continue
	}
	
	//because its in the same file the settings dont't need to be passed in [:D]
	private void initGame(String difficulty) {
		menuPanel.setVisible(false);
		
		//TODO
		//might just make a note here, the 'diff' input to this method isn't being used
			//thats because its meant to be called to the spawner
				//but the player speed relative to everything needs to be changed
		
		//matches just numbers [hopefully]
		if (!(gameWidth.getText().matches("[0-9]+") && gameHeight.getText().matches("[0-9]+"))) {
			System.err.println("Game width and height must be an integer");
			return;
		}
		

        GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		
		int pixelWidth = Integer.parseInt(gameWidth.getText());
		int pixelHeight = Integer.parseInt(gameHeight.getText());
		
		GameSettings set = new GameSettings(pixelWidth, pixelHeight, boardWidth, boardHeight, scale);
		set.setIfAliasing(antialiasing.isSelected());
		set.setIfSound(sound.isSelected());
		set.setIfParticles(particles.isSelected());
		
        //then write settings to file
        LeaderBoard.writeSettings(set);
        
        this.engine = new GameEngine(new SpawnHandler(difficulty), new GameState(difficulty), set);
        
        //////////////////////////////
        //JOGL Stuff:
        
		this.theFrame.setLocationRelativeTo(null);
		
		this.gamePanel = new GLJPanel(glcapabilities);
		this.gamePanel.addGLEventListener(engine);
		
		this.theFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		
		if (pixelWidth >= 800 && pixelHeight >= 600) { //the game really only works on resolutions bigger than this
			this.theFrame.setSize(pixelWidth, pixelHeight);
		} else {
			this.theFrame.setSize(TheGame.DEFAULT_PIXEL_WIDTH, TheGame.DEFAULT_PIXEL_HEIGHT);
		}
		
		this.theFrame.setName("GridWars - Jake Murphy");
		this.theFrame.setVisible(true);
		this.theFrame.setFocusable(true);
		this.theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.gamePanel.addKeyListener(GameEngine.player);
        this.gamePanel.addMouseListener(GameEngine.player);
        
        this.gamePanel.addMouseMotionListener(Mouse.theMouse);
        this.gamePanel.addMouseListener(Mouse.theMouse);
        
        this.gamePanel.requestFocus();
        this.theFrame.setLocationRelativeTo(null); //middle of the screen
        
        this.animator = new FPSAnimator(60);
        this.animator.add(gamePanel);
        this.animator.start();
    }
	
	public static void reloadMenu(GameState state) {
		//TODO (ask for name) (TODO maybe before the play button, you input your name with special words for weird spawns?)
		System.out.println("(lost all lives)\n   - reloadMenu");
		LeaderBoard.writeScore(GameEngine.EASY_D, state.getScore(), "ME*", (int)state.getTime());
		LeaderBoard.addToStats(state);
	}
	
    //rewrite the leaderboard to represent the current difficulty set
    @Override
	public void actionPerformed(ActionEvent evt) {
    	menuPanel.remove(board);
    	
    	board = LeaderBoard.getLeaderBoard(evt.getActionCommand());
    	menuPanel.add(board, gbLayout);
    	
    	theFrame.revalidate();
    	theFrame.repaint();
	}

}
