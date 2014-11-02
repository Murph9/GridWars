
public interface SnakeObject {
	
	static final int MAX_SPEED = 3;
	
	SnakeObject getBefore();
	SnakeObject getAfter();
	void destroy();
	void update(double dt);
	double[] getPosition();
	double getRotation();
}
