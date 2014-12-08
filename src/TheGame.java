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
import javax.swing.JTextField;
import javax.swing.Timer;

import com.jogamp.opengl.util.FPSAnimator;


//not sure whether this file or GameEngine should handle the actual position of the spawning objects 
//idea for different spawning sets, like only clones ..... (just increasing numbers of them)
//rather large class...
public class TheGame {

	private JFrame menuFrame;
	
	private JFrame gameFrame;
	private GLJPanel gamePanel; //for the GameEngine
	private FPSAnimator animator;
	private GameEngine engine;
	
	private Timer timer;
	private static int TIME_INTERVAL = 250; //500 seems the best so far, 250 is just hard
	private Random random;
	
	private static final int boardWidth = 16, boardHeight = 12; //at least 4 please
	private static final double scale = 10; //you know, it kind of works, as easy as this number is 
	
	private final int pixelWidth = 1024;
	private final int pixelHeight = 768;

	private JTextField gameWidth;
	private JTextField gameHeight;
	
	public static void main(String[] args) {
		TheGame system = new TheGame();
		system.initMenu();
	}
	
	public void initMenu() {
		random = new Random();

		menuFrame = new JFrame();
		menuFrame.setLayout(new GridBagLayout());
		
		JPanel newGamePanel = new JPanel();
		newGamePanel.setLayout(new GridBagLayout());
		newGamePanel.setBorder(BorderFactory.createTitledBorder("New Game"));
		
		GridBagConstraints c = new GridBagConstraints();
		
		JButton buttonEasy = new JButton("Easy");
		buttonEasy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				initGame(0);
				menuFrame.setVisible(false);
			}
		});
		newGamePanel.add(buttonEasy, c);
		
		JButton buttonMed = new JButton("Med");
		buttonMed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				initGame(1);
				menuFrame.setVisible(false);
			}
		});
		newGamePanel.add(buttonMed, c);
		
		JButton buttonHard = new JButton("Hard");
		buttonHard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				initGame(2);
				menuFrame.setVisible(false);
			}
		});
		newGamePanel.add(buttonHard, c);
		//////////////////////////////////////////////////
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		
		JCheckBox settings = new JCheckBox("Yay check box");
		settingsPanel.add(settings, c);
		
		JCheckBox set2 = new JCheckBox("box 2");
		settingsPanel.add(set2, c);
		
		gameWidth = new JTextField("width");
		settingsPanel.add(gameWidth, c);
		
		gameHeight = new JTextField("height");
		settingsPanel.add(gameHeight, c);
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
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 1;
		menuFrame.add(newGamePanel, c);

		c.gridx++;
		menuFrame.add(settingsPanel, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		menuFrame.add(scoresPanel, c);
		//////////////////////////////////////////////////
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setName("Menu - Jake Murphy");
		
		menuFrame.pack();
		menuFrame.repaint();
		menuFrame.setLocationRelativeTo(null);
		menuFrame.setVisible(true);
	}
	
	//because its in the same file the settings dont't need to be passed in
	private void initGame(int diff) {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		
		this.engine = new GameEngine(boardWidth, boardHeight, scale, 10000);
		
		this.gamePanel = new GLJPanel(glcapabilities);
		this.gamePanel.addGLEventListener(engine);
		
		this.gameFrame = new JFrame();
		this.gameFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		
		if (!gameWidth.getText().equals("width") && !gameHeight.getText().equals("height")) {
			int width = Integer.parseInt(gameWidth.getText());
			int height = Integer.parseInt(gameHeight.getText());
			if (width > 800 && height > 600) {
				this.gameFrame.setSize(width, height);
			} else {
				this.gameFrame.setSize(pixelWidth, pixelHeight);
			}
		} else {
			this.gameFrame.setSize(pixelWidth, pixelHeight);
		}
		this.gameFrame.setName("GridWars - Jake Murphy");
		this.gameFrame.setVisible(true);
		this.gameFrame.setFocusable(true);
		this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
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
        this.gamePanel.setLocation(menuFrame.getLocation());
        
        this.animator = new FPSAnimator(60);
        this.animator.add(gamePanel);
        this.animator.start();
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
