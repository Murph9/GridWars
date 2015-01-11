package game.logic;
import game.objects.*;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Random;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.jogamp.opengl.util.FPSAnimator;

//Handles menus and other things
//rather large class...

public class TheGame implements ActionListener {

	private JFrame theFrame; //reuseable frame that 
	private JPanel menuPanel; //for the menu
	
	private GLJPanel gamePanel; //for the GameEngine
	private FPSAnimator animator;
	private GameEngine engine;
	
	private Timer timer;
	private static int TIME_INTERVAL = 250; //500 seems the best so far, 250 is just hard
	private Random random;
	
	private JCheckBox particles; //settings (from the checkboxes)
//	private JCheckBox particles; //add others... (thats not 'and')
	
	private int boardWidth = 16, boardHeight = 12; //at least 4 please
	private double scale = 10; //you know, it kind of works, as easy as this number is 
	
	private final int pixelWidth = 1024;
	private final int pixelHeight = 768;

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
		random = new Random();

		theFrame = new JFrame();
		
		menuPanel = new JPanel();
		menuPanel.setLayout(new GridBagLayout());
		theFrame.add(menuPanel);
		
		JPanel newGamePanel = new JPanel();
		newGamePanel.setLayout(new GridBagLayout());
		newGamePanel.setBorder(BorderFactory.createTitledBorder("New Game"));
		
		
		////////////////////////////////////////////////
		////Game Mode
		
		GridBagConstraints a = new GridBagConstraints();
		
		JRadioButton buttonEasy = new JRadioButton(easyB);
		buttonEasy.setActionCommand(LeaderBoard.EASY);
		
		JRadioButton buttonMed = new JRadioButton(medB);
		buttonMed.setActionCommand(LeaderBoard.MED);
		
		JRadioButton buttonHard = new JRadioButton(hardB);
		buttonHard.setActionCommand(LeaderBoard.HARD);
		
		JButton go = new JButton("Go");
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();

		            if (button.isSelected()) {
		                initGame(button.getText());
		            }
		        }
			}
		});
		
		group = new ButtonGroup();
		group.add(buttonEasy);
		group.add(buttonMed);
		group.add(buttonHard);
		
		buttonMed.setSelected(true); //because its hard coded somewhere else i think (in this file)
		
		buttonEasy.addActionListener(this);
		buttonMed.addActionListener(this);
		buttonHard.addActionListener(this);

		
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
		
		
		////////////////////////////////////////////////// 
		////Settings
		GridBagConstraints c2 = new GridBagConstraints();
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		
		c2.gridx = 0;
		c2.gridy = 0;
		JCheckBox settings = new JCheckBox("Yay check box");
		settingsPanel.add(settings, c2);
		
		c2.gridy++;
		this.particles = new JCheckBox("Particles");
		this.particles.setSelected(true); //defaults to on (read from settings later)
		settingsPanel.add(this.particles, c2);
		
		c2.gridy = 0;
		c2.gridx++;
		gameWidth = new JTextField("1024"); //read both from settinfsgs later
		settingsPanel.add(gameWidth, c2);
		
		c2.gridy++;
		gameHeight = new JTextField("768");
		settingsPanel.add(gameHeight, c2);
		
		//////////////////////////////////////////////////
		////Heading Info + positions		
		
		gbLayout = new GridBagConstraints();
		gbLayout.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.insets = new Insets(6,6,6,6);
		
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
		
		///////Scoreboard
		board = LeaderBoard.getLeaderBoard(LeaderBoard.MED);
		
		gbLayout.gridx++;
		gbLayout.gridy = 1;
		gbLayout.gridheight = 2;
		menuPanel.add(board, gbLayout);
		
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
	private void initGame(String diff) {
		menuPanel.setVisible(false);
		
		//TODO
		//might just make a note here, the 'diff' input to this method isn't being used
		//TODO
		
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		
		this.engine = new GameEngine(new GameState(boardWidth, boardHeight, scale, 10000, this.particles.isSelected()));
		this.theFrame.setLocationRelativeTo(null);
		
		this.gamePanel = new GLJPanel(glcapabilities);
		this.gamePanel.addGLEventListener(engine);
		
		this.theFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		
			//matches just numbers [hopefully]
		if (gameWidth.getText().matches("[0-9]+") || gameHeight.getText().matches("[0-9]+")) {
			int width = Integer.parseInt(gameWidth.getText());
			int height = Integer.parseInt(gameHeight.getText());
			if (width >= 800 && height >= 600) { //minimum size you should have
				this.theFrame.setSize(width, height);
			} else {
				this.theFrame.setSize(pixelWidth, pixelHeight);
			}
		} else {
			this.theFrame.setSize(pixelWidth, pixelHeight);
		}
		
		this.theFrame.setName("GridWars - Jake Murphy");
		this.theFrame.setVisible(true);
		this.theFrame.setFocusable(true);
		this.theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newEnemy();
			}
		};
		this.timer = new Timer(TIME_INTERVAL, taskPerformer);
		this.timer.start();
        
        this.gamePanel.addKeyListener(GameEngine.player);
        this.gamePanel.addMouseListener(GameEngine.player);
        
        this.gamePanel.addMouseMotionListener(Mouse.theMouse);
        this.gamePanel.addMouseListener(Mouse.theMouse);
        
        this.gamePanel.requestFocus();
        this.theFrame.setLocationRelativeTo(null);
        
        this.animator = new FPSAnimator(60);
        this.animator.add(gamePanel);
        this.animator.start();
    }
	
	public static void reloadMenu(GameState state) {
		//TODO (ask for name and other things)
		System.out.println("(lost all lives)\n   - reloadMenu");
		LeaderBoard.writeScore(LeaderBoard.EASY, state.getScore(), "ME");
	}
	
    //spawning done simple. Look at SpawnHandler for better spawning logic
    private void newEnemy() {
    	if (!GameEngine.canSpawn()) {
    		return;
    	}
    	int a = random.nextInt(13);
    	GameObject s = null;
    	switch (a) {
    	case 0: case 1: case 2:
    		s = new SimpleSpinner();    	break;
    	case 3: case 4:
    		s = new HomingDiamond();   		break;
    	case 5: case 6:
    		s = new SplitingSquare();		break;
    	case 7:
    		s = new ShieldedClone();    	break;
    	case 8:
    		s = new SnakeHead();    		break;
    	case 9: case 10:
    		s = new ShySquare();			break;
    	case 11:
    		s = new BlackHole();   			break;
    	case 12:
    		s = new ConnectedTriangle();	break;
    	}
    	s.setPosition(new double[]{(random.nextInt(2)*2-1)*(boardWidth-0.5), (random.nextInt(2)*2-1)*(boardHeight-0.5)});
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
