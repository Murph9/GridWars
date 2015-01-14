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

//TODO
//things to store in file:

public class LeaderBoard { //no longer extends JTextArea, just returns one if asked

	//note the spaces:
	private static final String STATS = "stats.txt", 
			HIGHEST_SCORE = "high_score ", HIGHEST_MULTI = "high_multiplier ",
			TOTAL_POWERUPS = "total_powerups ",	TOTAL_KILLS = "total_kills ", 
			TOTAL_SCORE = "total_score ", TOTAL_TIME = "total_time ", TOTAL_DEATHS = "total_deaths ",
			LONGEST_GAME = "longest_game ";
	
	private static final String SETTINGS = "settings.txt",
			SCREEN_WIDTH = "width ", SCREEN_HEIGHT = "height ",
			IF_PARTICLES = "particles ", IF_ANTIALIASING = "antialiasing ";
	
	
	///////////////////////
	//SETTINGS:
	
	//returns a int[]: width, height, other boolean (1 = true, 0 - false - same as java)
	public static int[] readSettings() {
		File file = new File(SETTINGS);

		int width = -1, height = -1, particles = -1, antialiasing = -1;
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) {
					String text = scores.next() + " "; //because everything was spaced... TODO FIX THIS MESS
					if (text.equals(SCREEN_WIDTH)) width = scores.nextInt();
					else if (text.equals(SCREEN_HEIGHT)) height = scores.nextInt();
					
					else if (text.equals(IF_PARTICLES)) particles = scores.nextInt(); 
					else if (text.equals(IF_ANTIALIASING)) antialiasing = scores.nextInt();
					
					else {
						scores.next(); //although it shouldn't ever get here
						System.out.println("wow, you broke it good");
					}
				}
				scores.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		} else { //default values are below
			width = 1024;
			height = 728;
			particles = 1;
			antialiasing = 1;
			
			writeSettings(width, height, new int[]{particles, antialiasing});
				//simpler?, maybe
		}
		
		return new int[] {width, height, particles, antialiasing};
	}
	
	//change settings (not many so far) works though
	public static void writeSettings(int width, int height, int[] settings) {
		File file = new File(SETTINGS);
		
		PrintWriter out = null;
		try {
			file.delete(); //because it will append otherwise
			file.createNewFile(); //will always work here
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.println(SCREEN_WIDTH + new Integer(width).toString());
		out.println(SCREEN_HEIGHT+ new Integer(height).toString());
		
		out.println(IF_PARTICLES + settings[0]);
		out.println(IF_ANTIALIASING + settings[1]);
		
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
		System.out.println(diff);
		
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
