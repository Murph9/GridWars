package game.objects;

public interface SnakeObject {
	
	static final int MAX_SPEED = 4;
	
	SnakeObject getBefore();
	SnakeObject getAfter();
	void amHit(boolean ifPoints);
	void iDied();
	void update(double dt);
	double[] getPosition();
	double getRotation();
	double getSpawnTimer();
}
