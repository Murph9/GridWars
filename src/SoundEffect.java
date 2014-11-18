import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;


public enum SoundEffect {
	SHOT("sounds/bullet1.wav"), //bullet shoot 
	BULLET_KILL("sounds/bullet_hit.wav"), //bullet die 
	POWERUP("sounds/Powerup.wav"),   // powerup
	POWERUP2("sounds/Powerup2.wav"),         // gong
	SHOOT("sounds/shot.wav");       // bullet
	//lots of others
	
	// Nested class for specifying volume
	public static enum Volume {
		MUTE, LOW, MEDIUM, HIGH
	}
	public static Volume volume = Volume.HIGH;
	
	// Each sound effect has its own clip, loaded with its own sound file.
	private Clip clip = null;
	
	SoundEffect(String soundFileName) {
		try {
			// Open an audio input stream.
			File r = new File(soundFileName);
			URL url = r.toURI().toURL();
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			clip = AudioSystem.getClip();

			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	// Play or Re-play the sound effect from the beginning, by rewinding.
	public void play() {
		if (volume != Volume.MUTE) {
			if (clip.isRunning()) {
				clip.stop();   // Stop the player if it is still running
			}
			clip.setFramePosition(0); // rewind to the beginning
			clip.start();     // Start playing
		}
		
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-10.0f); // Reduce volume by 10 decibels.
	}
	
	// Optional static method to pre-load all the sound files.
	static void init() {
		values(); // calls the constructor for all the elements
	}
}



//http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html