package game.logic;
import game.objects.*;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.jogamp.opengl.util.FPSAnimator;


//not sure whether thisfile/GameEngine/newfile should handle the actual position of the spawning objects 
//idea for different spawning sets, like only clones ..... (just increasing numbers of them)
//rather large class...

public class TheGame {

	private JFrame theFrame; //reuseable frame that 
	private JPanel menuPanel; //for the menu
	
	private GLJPanel gamePanel; //for the GameEngine
	private FPSAnimator animator;
	private GameEngine engine;
	
	private Timer timer;
	private static int TIME_INTERVAL = 250; //500 seems the best so far, 250 is just hard
	private Random random;
	
	private JCheckBox particles;
	
	private int boardWidth = 16, boardHeight = 12; //at least 4 please
	private double scale = 10; //you know, it kind of works, as easy as this number is 
	
	private final int pixelWidth = 1024;
	private final int pixelHeight = 768;

	private JTextField gameWidth;
	private JTextField gameHeight;
	
	public static LeaderBoard board;
	
 	public static void main(String[] args) {
		TheGame system = new TheGame();
		system.initMenu();
	}
	
	//draw menu
	public void initMenu() {
		random = new Random();

		theFrame = new JFrame();
		
		menuPanel = new JPanel();
		menuPanel.setLayout(new GridBagLayout());
		theFrame.add(menuPanel);
		
		JPanel newGamePanel = new JPanel();
		newGamePanel.setLayout(new GridBagLayout());
		newGamePanel.setBorder(BorderFactory.createTitledBorder("New Game"));
		
		GridBagConstraints c = new GridBagConstraints();
		////////////////////////////////////////////////

		JPanel headPanel = new JPanel();
		headPanel.setLayout(new GridBagLayout());
		
		JTextArea t = new JTextArea("Welcome to NotGridWars2\nArrow keys/WASD to move, right/left mouse button do things");
		t.setEditable(false);
		headPanel.add(t, c);
		
		////////////////////////////////////////////////
		JButton buttonEasy = new JButton("Easy");
		buttonEasy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				initGame(0);
			}
		});
		newGamePanel.add(buttonEasy, c);
		
		JButton buttonMed = new JButton("Med");
		buttonMed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				initGame(1);
			}
		});
		newGamePanel.add(buttonMed, c);
		
		JButton buttonHard = new JButton("Hard");
		buttonHard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				initGame(2);
			}
		});
		newGamePanel.add(buttonHard, c);
		//////////////////////////////////////////////////
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
		
		JPanel scoresPanel = new JPanel();
		scoresPanel.setLayout(new GridBagLayout());
		GridBagConstraints scoresC = new GridBagConstraints();
		scoresC.gridx = 0;
		scoresC.insets = new Insets(3,3,3,3);
		
		LeaderBoard l0 = new LeaderBoard(LeaderBoard.EASY, false);
		scoresPanel.add(l0, scoresC);
		l0.writeScore(-1); //how you write the score
		scoresC.gridx++;
		LeaderBoard l1 = new LeaderBoard(LeaderBoard.MED, false);
		scoresPanel.add(l1, scoresC);
		l1.writeScore(-1); //how you write the score
		scoresC.gridx++;
		LeaderBoard l2 = new LeaderBoard(LeaderBoard.HARD, false);
		scoresPanel.add(l2, scoresC);
		l2.writeScore(-1); //how you write the score
		//////////////////////////////////////////////////
		
		//something else im sure
		
		//////////////////////////////////////////////////
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 2;
		menuPanel.add(headPanel, c);
		
		c.gridy++;
		c.gridwidth = 1;
		menuPanel.add(newGamePanel, c);

		c.gridx++;
		menuPanel.add(settingsPanel, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		menuPanel.add(scoresPanel, c);
		
		//////////////////////////////////////////////////
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
	private void initGame(int diff) {
		menuPanel.setVisible(false);
		
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		
		this.engine = new GameEngine(boardWidth, boardHeight, scale, new GameState(10000, this.particles.isSelected()));
		theFrame.setLocationRelativeTo(null);
		
		this.gamePanel = new GLJPanel(glcapabilities);
		this.gamePanel.addGLEventListener(engine);
		
		this.theFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		
		if (gameWidth.getText().matches("$[0-9]+^") || gameHeight.getText().matches("$[0-9]+^")) {
			int width = Integer.parseInt(gameWidth.getText());
			int height = Integer.parseInt(gameHeight.getText());
			if (width > 800 && height > 600) { //minimum size
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
//        this.gamePanel.setLocation(menuPanel.getLocation());
        this.theFrame.setLocationRelativeTo(null);
        
        this.animator = new FPSAnimator(60);
        this.animator.add(gamePanel);
        this.animator.start();
    }
	
	public static void reloadMenu(String name, int score) {
		
	}
	
    //spawning. simple. Look at SpawnHandler for better spawning logic
    private void newEnemy() {
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

}
