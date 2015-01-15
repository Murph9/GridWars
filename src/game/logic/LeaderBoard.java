package game.logic;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JTextArea;

/**Handles all file reading through static methods.
 * 
 * @author Jake Murphy
 */
public class LeaderBoard {

	//note the spaces:
	private static final String STATS = "stats.txt", 
			HIGHEST_SCORE = "high_score ", HIGHEST_MULTI = "high_multiplier ",
			TOTAL_POWERUPS = "total_powerups ",	TOTAL_KILLS = "total_kills ", 
			TOTAL_SCORE = "total_score ", TOTAL_TIME = "total_time ", TOTAL_DEATHS = "total_deaths ",
			LONGEST_GAME = "longest_game ";
	
	private static final String SETTINGS = "settings.txt",
			PIXEL_WIDTH = "pixel_width ", PIXEL_HEIGHT = "pixel_height ",
			BOARD_WIDTH = "board_width ", BOARD_HEIGHT = "board_height ", SCALE = "scale ",
			IF_PARTICLES = "particles ", IF_ANTIALIASING = "antialiasing ", IF_SOUND = "sound ";
	
	
	///////////////////////
	//SETTINGS:
	
	//returns a int[]: width, height, other boolean (1 = true, 0 - false - same as java)
	public static GameSettings readSettings() {
		File file = new File(SETTINGS);

		int pixel_width = -1, pixel_height = -1, board_width = -1, board_height = -1;
		double scale = -1;
		boolean	particles = true, antialiasing = true, sound = true;
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) {
					String text = scores.next() + " "; //because everything was spaced... TODO FIX THIS MESS
					if (text.equals(PIXEL_WIDTH)) pixel_width = scores.nextInt();
					else if (text.equals(PIXEL_HEIGHT)) pixel_height = scores.nextInt();
					
					else if (text.equals(BOARD_WIDTH)) board_width = scores.nextInt();
					else if (text.equals(BOARD_HEIGHT)) board_height = scores.nextInt();

					else if (text.equals(SCALE)) scale = scores.nextDouble();
					
					else if (text.equals(IF_SOUND)) sound = scores.nextBoolean();
					else if (text.equals(IF_PARTICLES)) particles = scores.nextBoolean(); 
					else if (text.equals(IF_ANTIALIASING)) antialiasing = scores.nextBoolean();
					
					else {
						//rewrite is needed but, for testing this will just say:
						System.out.println("Reverting back to default settings, as something broke.\nOld settings moved to old_<>");
						File oldSettings = new File("old_"+SETTINGS);
						file.renameTo(oldSettings);
						
						GameSettings b = new GameSettings();
						pixel_width = 1024;
						pixel_height = 728;
						sound = true;
						particles = true;
						antialiasing = true;
						
						file.delete();
						writeSettings(b);
						scores.close();
						return b;
					}
				}
				scores.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			}
			
		} else { //default settings are are below
			pixel_width = 1024;
			pixel_height = 728;
			sound = true;
			particles = true;
			antialiasing = true;
			
			GameSettings a = new GameSettings();
			writeSettings(a); //so it now exists to read from
			return a;
		}
		
		GameSettings set = new GameSettings(pixel_width, pixel_height, board_width, board_height, scale);
		set.setIfAliasing(antialiasing);
		set.setIfParticles(particles);
		set.setIfSound(sound); //wouldn't fit into constructor
		
		return set;
	}
	
	//change settings (not many so far) works though
	public static void writeSettings(GameSettings settings) {
		File file = new File(SETTINGS);
		
		PrintWriter out = null;
		try {
			file.delete(); //because it will append otherwise
			file.createNewFile(); //will always work here
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.println(PIXEL_WIDTH + settings.getPixelWidth());
		out.println(PIXEL_HEIGHT+ settings.getPixelHeight());
		
		out.println(BOARD_WIDTH + settings.getBoardWidth());
		out.println(BOARD_HEIGHT+ settings.getBoardHeight());
		out.println(SCALE + settings.getScale());
		
		out.println(IF_SOUND + settings.ifSound());
		out.println(IF_PARTICLES + settings.ifParticles());
		out.println(IF_ANTIALIASING + settings.ifAliasing());
		
		out.close();
	}
	

	/////////////////////////
	//STATS: TODO
	
	//adds to the stats.txt file TODO
	public static void addToStats(GameState state) {
		File file = new File(STATS);
		
		Scanner stats = null;
		HashMap<String, Integer> statSet = null;
		try {
			stats = new Scanner(new FileReader(file));
			
			statSet = new HashMap<String, Integer>();
			while (stats.hasNext()) {
				statSet.put(stats.next(), stats.nextInt());
			}
			stats.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		PrintWriter out = null;
		try {
			file.createNewFile(); //only works if it doesn't exist
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.close();
	}
	
	//returns the stats saved in the stats.txt
	public static String getStats() {
		return "";
		//TODO
	}
	
	
	//////////////////////////
	//SCORES:
	
	//writes scores to file (under the current difficulty)
	public static void writeScore(String diff, int score, String name, int time) {
		diff += GameEngine.EXT;

		File file = new File(diff);
		
		LinkedList<Record> records = new LinkedList<Record>();
		
		try {
			Scanner scores = new Scanner(new FileReader(file));
			while (scores.hasNext()) { //write scores to list
				records.add(new Record(scores.nextInt(), scores.next(), scores.nextInt()));
			}
			scores.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		records.add(new Record(score, name, time)); //add new record
		
		Collections.sort(records, new Comparator<Record>() {
			public int compare(Record a, Record b) {
				if (a.equals(b)) { return 0; }
				if (a.getScore() <= b.getScore()) { 
					return 1; // a
				}
				return -1; // b
			}
		});
		
		file.delete();
		file = new File(diff);
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int count = Math.min(records.size(), 10);
		for (int i = 0; i < count; i++) { //save the best min(records.size(), 15) to file
			out.println(records.get(i));
		}
		
		out.close();
	}
	
	//returns the best score for the UI best score function
	public static int getBestScore(String diff) {
		diff += GameEngine.EXT;
		
		File file = new File(diff);
		
		LinkedList<Record> records = new LinkedList<Record>();
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) { //write scores to list
					records.add(new Record(scores.nextInt(), scores.next(), scores.nextInt()));
				}
				scores.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return 0;
			}
		}
		
		Collections.sort(records, new Comparator<Record>() {
			public int compare(Record a, Record b) {
				if (a.equals(b)) { return 0; }
				if (a.getScore() <= b.getScore()) { 
					return 1; // a
				}
				return -1; // b
			}
		});
		
//		if (records.isEmpty()) {
//			return 0;
//		} else {
			return records.getFirst().getScore();
		}
//	}
	
	
	////////////////////////
	//JTextArea stuff:

	//returns the TextArea of the scores (for displaying)
	public static JTextArea getLeaderBoard(String diff) {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		
		diff += GameEngine.EXT;
		File file = new File(diff);
		if (!file.exists()) {
			throw new IllegalArgumentException("Input file must exist for this method");
		}
		
		String text = getText(file);
		
		if (diff.equals(GameEngine.EASY_D + GameEngine.EXT)) {
			area.setText("Easy Scores\n"+ text);
		} else if (diff.equals(GameEngine.MEDIUM_D + GameEngine.EXT)) {
			area.setText("Medium Scores\n" + text);
		} else if (diff.equals(GameEngine.HARD_D + GameEngine.EXT)){
			area.setText("Hard Scores\n" + text);
		}
		
		return area;
	}
	
	//returns the text from the file for the text area 
	private static String getText(File file) {
		LinkedList<Record> records = new LinkedList<Record>();
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) {
					records.add(new Record(scores.nextInt(), scores.next(), scores.nextInt()));
				}
				scores.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		Collections.sort(records, new Comparator<Record>() {
			public int compare(Record a, Record b) {
				if (a.equals(b)) { return 0; }
				if (a.getScore() <= b.getScore()) { 
					return 1; // a
				}
				return -1; // b
			}
		});
		
		String text = "";
		
		int count = 0;
		for (Record i: records) {
			text += (i.toLineString() + "\n");

			if (count >= 10) break;
			count++;
		}
		
		return text;
	}

}


class Record {
	private int score;
	private String name;
	private int time;
	
	Record (int score, String name, int time) {
		this.score = score;
		this.name = name;
		this.time = time;
	}
	
	public int getTime()  { return this.time;  }
	public int getScore() { return this.score; }
	
	public String toString() { //for saving to file
		return (score + "\t " + name + "\t " + time);
	}
	
	public String toLineString() { //for writing to leader board
		return (score + "\t|  "+ name + "\t| " + time);
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!this.getClass().equals(o.getClass())) return false;
		if (o == this) return true;
		Record in = (Record) o;
		
		if (score == in.score && name.equals(in.name)) {
			return true;
		}
		return false;
	}
}
