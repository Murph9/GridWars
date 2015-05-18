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

	public static boolean ifSound = true;
	
	//variables to use when run is called
	private int soundFileIndex; //file location
	private int level; //volume of played sound
	private double pan; //pan of sound, -1 -> 1
	
	/** Play sound from beginning, simple. Called using new SoundEffect(<sound>).start()
	 * @param level Decibles from maximum sound (0 = loudest, 100 = softest)
	 * @param pan Not implemented
	 */
	public SoundEffect(int soundFileIndex, int volume, double pan) {
		if (soundFileIndex > Engine.SOUND_SIZE || soundFileIndex < 0) {
			System.err.println("Sound int wrong, SoundEffect line ~37");
			this.soundFileIndex = -1;
			return;
		}
		
		this.soundFileIndex = soundFileIndex;
		this.level = volume;
		this.pan = pan;
	}
	
	
	@Override
	public void run() {
		if (!ifSound) return;
		File file = Engine.sounds[soundFileIndex];
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


	public static void setSound(boolean in) {
		ifSound = in;
	}
}

