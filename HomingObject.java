
public abstract class HomingObject extends MovingObject {

	protected double speed = 3; //if not given a speed
	
	HomingObject(double size, double[] colour, double speed) {
		super(size, colour);
		this.speed = speed;
	}

	protected void setSpeed(int speed) { //because objects have different speeds
		this.speed = speed;
	}
	
	public void update(double dt) {
		double[] myPos = getPosition();
		myPos[0] += mySpeedX*dt*speed; //add it
    	myPos[1] += mySpeedY*dt*speed;
		
		double[] s = GameEngine.getPlayerPos();

    	mySpeedX = s[0]-myPos[0];
    	mySpeedY = s[1]-myPos[1];

    	double dist = Math.sqrt(mySpeedX*mySpeedX + mySpeedY*mySpeedY);
    	if (dist != 0) { //divide by zero errors are bad
    		mySpeedX /= dist;
    		mySpeedY /= dist; //now they are normalised
    	}
    	
		if (myPos[0] > GameEngine.boardWidth-(size/2)) {
			mySpeedX = 0;
			myPos[0] = GameEngine.boardWidth-(size/2);
		} else if (myPos[0] < -GameEngine.boardWidth+(size/2)) {
			mySpeedX = 0;
			myPos[0] = -GameEngine.boardWidth+(size/2);
		}
		
		if (myPos[1] > GameEngine.boardHeight-(size/2)) {
			mySpeedY = 0;
			myPos[1] = GameEngine.boardHeight-(size/2);
		} else if (myPos[1] < -GameEngine.boardHeight+(size/2)) {
			mySpeedY = 0;
			myPos[1] = -GameEngine.boardHeight+(size/2);
		}
		
		setPosition(myPos); //set it
    }
}

