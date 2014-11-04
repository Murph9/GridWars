
public abstract class HomingObject extends MovingObject {

	protected float speed = 2; //if not given a speed
	
	HomingObject(double size, double[] colour, float speed) {
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
		
		//please future me don't try to add border collisions again, homing objets will NEVER hit the side.
			//just think about it (and if they do they can have that code themselves
		
		if (myPos[0] > GameEngine.boardWidth-(mySize/2)) {
			mySpeedX = GameEngine.boardWidth-(mySize/2);
		} else if (myPos[0] < -GameEngine.boardWidth+(mySize/2)) {
			mySpeedX = 0;
			myPos[0] = -GameEngine.boardWidth+(mySize/2);
		}
		
		if (myPos[1] > GameEngine.boardHeight-(mySize/2)) {
			mySpeedY = 0;
			myPos[1] = GameEngine.boardHeight-(mySize/2);
		} else if (myPos[1] < -GameEngine.boardHeight+(mySize/2)) {
			mySpeedY = 0;
			myPos[1] = -GameEngine.boardHeight+(mySize/2);
		}
		
		setPosition(myPos); //set it
	}
}

	