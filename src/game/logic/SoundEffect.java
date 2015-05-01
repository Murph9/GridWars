package game.logic;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;


/**
 * Audio playing and handling.
 * @author Another random internet user
 */
public enum SoundEffect {
	//TODO makes better ones
	/*lots of others needed:
		bullet shoot, hit, bounce
		blackhole everything; hit, die, explode, spawn. ( and constant pulsing)
		life lost
		powerup
		object spawn sound
		???
	*/
	
	//Good:
	OBJECT_KILL("sounds/bullet1.wav"), //bullet shoot, make shorter 

	//Bad:
	BULLET_KILL("sounds/bullet_hit.wav"), //bullet die 
	POWERUP("sounds/Powerup.wav"),   // powerup
	POWERUP2("sounds/Powerup2.wav"),         // gong
	SHOOT("sounds/shot.wav");       // bullet hit

	
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
	
	/** Play sound from beginning, simple.
	 * @param level Decibles from maximum sound (0 = loudest, 100 = softest)
	 * @param pan Not implemented
	 */
	public void play(int level, float pan) { //pan not used yet TODO
		if (volume != Volume.MUTE) {
			if (clip.isRunning()) {
				clip.stop();   // Stop the player if it is still running
			}
			clip.setFramePosition(0); // rewind to the beginning
			clip.start();     // Start playing
		}
		
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-level); // Reduce volume by level decibels.
	}
	
	// Optional static method to pre-load all the sound files.
	static void init() {
		values(); // calls the constructor for all the elements
	}
}


//http://www3.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html




//below is for playing 2 songs at once from: TODO
	//http://stackoverflow.com/questions/9656462/how-do-i-play-two-sounds-at-once

/*import java.net.URL;
import javax.swing.*;
import javax.sound.sampled.*;

public class LoopSounds {

    public static void main(String[] args) throws Exception {
        URL url = new URL(
            "http://pscode.org/media/leftright.wav");
        Clip clip = AudioSystem.getClip();
        AudioInputStream ais = AudioSystem.
            getAudioInputStream( url );
        clip.open(ais);

        URL url2 = new URL(
            "http://pscode.org/media/100_2817-linear.wav");
        Clip clip2 = AudioSystem.getClip();
        AudioInputStream ais2 = AudioSystem.
            getAudioInputStream( url2 );
        clip2.open(ais2);

        // loop continuously
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip2.loop(Clip.LOOP_CONTINUOUSLY);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // A GUI element to prevent the Clip's daemon Thread
                // from terminating at the end of the main()
                JOptionPane.showMessageDialog(null, "Close to exit!");
            }
        });
    }
}*/