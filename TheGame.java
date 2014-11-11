import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.Timer;

import com.jogamp.opengl.util.FPSAnimator;


//not sure whether this file or GameEngine/other should handle the 
	// actual position of the spawning objects 

public class TheGame extends JFrame {
	//contains main and all that jazz
	private static final long serialVersionUID = 1L;

	private GLJPanel myPanel;
	private FPSAnimator animator;	
	
	private Timer timer;
	private static int TIME_INTERVAL = 500;
	private Random random;
	
	private static int boardWidth = 12, boardHeight = 10; //at least 4 please
	
    public static void main(String[] args) {
		TheGame system = new TheGame();
		system.init();
	}
    
    private void newEnemy() {
    	int a = random.nextInt(12);
    	MovingObject s = null;
    	switch (a) {
    	case 0: case 1: case 2: //more common = yay
    		s = new SimpleSpinner(1, GameEngine.PURPLE);
    		break;
    	case 3: case 4:
    		s = new HomingDiamond(1, GameEngine.LIGHT_BLUE);
    		break;
    	case 5: case 6:
    		s = new SplitingSquare(1, GameEngine.RED, 0, 1, true);
    		break;
    	case 7:
    		s = new ShieldedClone(1.1, GameEngine.ORANGE);
    		break;
    	case 8:
    		s = new SnakeHead(0.8, GameEngine.YELLOW, 18);
    		break;
    	case 9: case 10:
    		s = new ShySquare(1, GameEngine.GREEN);
    		break;
    	case 11:
    		s = new BlackHole(1, GameEngine.RED);
    	}
    	s.setPosition(new double[]{(random.nextInt(2)*2-1)*(boardWidth-0.5), (random.nextInt(2)*2-1)*(boardHeight-0.5)});
    }
    
    public void init() {
    	GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        
        this.myPanel = new GLJPanel(glcapabilities);

        Camera camera = new Camera();
        camera.setSize(Math.max(boardHeight + 1, boardWidth - 4)); //these numbers are just so that it always fits on screen
        
        random = new Random();
        
        GameEngine.player = new Player(1, GameEngine.WHITE);
        Border border = new Border(boardWidth, boardHeight);
        border.setSize(1); //just incase it stopped being 1
        
        GameEngine engine = new GameEngine(camera, boardWidth, boardHeight);
        this.myPanel.addGLEventListener(engine);
        
        getContentPane().add(myPanel, BorderLayout.CENTER);
        setSize(1024, 768);
        setName("Game");
        setVisible(true);
        setFocusable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newEnemy();
			}
		};
		this.timer = new Timer(TIME_INTERVAL, taskPerformer);
		this.timer.start();
        
        this.myPanel.addKeyListener(GameEngine.player);
        this.myPanel.addMouseMotionListener(Mouse.theMouse);
        this.myPanel.addMouseListener(Mouse.theMouse);
        this.myPanel.setFocusable(true);
        this.myPanel.requestFocus();
        
        this.animator = new FPSAnimator(60);
        this.animator.add(myPanel);
        this.animator.start();
    }
}
