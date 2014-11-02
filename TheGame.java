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


public class TheGame extends JFrame {
	//contains main and all that jazz
	private static final long serialVersionUID = 1L;

	private GLJPanel myPanel;
	private FPSAnimator animator;	
	
	private Timer timer;
	private static int TIME_INTERVAL = 500;
	private Random random;
	
	private static int boardWidth = 12, 
					boardHeight = 10; //at least 4 please
	
	public static final double[] WHITE = {1,1,1}, 	RED = {1,0,0},
							LIGHT_BLUE = {0,1,0.8}, GREEN = {0,1,0},
							PURPLE = {1,0,1}, 		YELLOW = {1,1,0},
							ORANGE = {0.8, 0.3, 0.2};
    
    public static void main(String[] args) {
		TheGame system = new TheGame();
		system.init();
	}
    
    private void newEnemy() {
    	int a = random.nextInt(9);
    	MovingObject s = null;
    	switch (a) {
    	case 0: case 1: case 2: //more common = yay
    		s = new SimpleSpinner(1, PURPLE, (random.nextInt(180)+90)*(random.nextInt(1)*2-1));
    		s.setSpeed(random.nextDouble(), random.nextDouble());
    		break;
    	case 3: case 4:
    		s = new HomingDiamond(1, LIGHT_BLUE);
    		break;
    	case 5: case 6:
    		s = new SplitingSquare(1, RED, 0, 1.4, true);
    		break;
    	case 7:
    		s = new ShieldedClone(1.1, ORANGE);
    		break;
    	case 8:
    		s = new SnakeHead(0.7, YELLOW, 10);
    		break;
    	}
    	s.setPosition(new double[]{(random.nextInt(2)*2-1)*(boardWidth-0.5), (random.nextInt(2)*2-1)*(boardHeight-0.5)});
    }
    
    public void init() {
    	GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        
        this.myPanel = new GLJPanel(glcapabilities);

        Camera camera = new Camera();
        camera.setScale(Math.max(boardHeight + 1, boardWidth - 4)); //these numbers are just so that it always fits on screen
        
        random = new Random();
        
        GameEngine.player = new Player(1, WHITE);
        Border border = new Border(boardWidth, boardHeight);
        border.setScale(1); //so no warning
        
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
