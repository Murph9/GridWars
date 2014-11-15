
public interface SnakeObject {
	
	static final int MAX_SPEED = 3;
	
	SnakeObject getBefore();
	SnakeObject getAfter();
	void amHit(boolean ifPoints);
	void update(double dt);
	double[] getPosition();
	double getRotation();
}
