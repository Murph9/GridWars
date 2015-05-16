package game.logic;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.scene.media.AudioClip;

/**
 * Audio playing and handling.
 * @author Link in this source
 */
public class SoundEffect extends Thread {
	//TODO makes better ones
	/*lots of others needed:
		bullet shoot, hit, bounce
		blackhole everything; hit, die, explode, spawn. ( and constant pulsing)
		life lost
		powerup
		object spawn sound
		???
	*/
	public static final String 

	SHOT_HARD =	"sounds/shot_hard.wav", 
	SHOT_SOFT =	"sounds/shot_soft.wav",
	POWERUP =	"sounds/powerup.wav",
	POWERUP2 =	"sounds/powerup2.wav",
	SPAWN = 	"sounds/spawn.wav";

	//variables to use when run is called
	private String soundFileName; //file location
	private int level; //volume of played sound
	private double pan; //pan of sound, -1 -> 1
	
	/** Play sound from beginning, simple. Called using new SoundEffect(<sound>).start()
	 * @param level Decibles from maximum sound (0 = loudest, 100 = softest)
	 * @param pan Not implemented
	 */
	public SoundEffect(String soundFileName, int volume, double pan) {
		this.soundFileName = soundFileName;
		this.level = volume;
		this.pan = pan;
	}
	
	
	@Override
	public void run() {
		File file = new File(soundFileName);
		URL u = null;
		try {
			u = file.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		
		AudioClip sound = new AudioClip(u.toString());
		sound.setBalance(pan);
		sound.setVolume(level); //TODO find what this wants
		sound.play();
		
		//yeah so apparently there is this thing in javafx that helps .... [wish i found this sooner]
		//https://docs.oracle.com/javafx/2/api/javafx/scene/media/AudioClip.html
	}
}

