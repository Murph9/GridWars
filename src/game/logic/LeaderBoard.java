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
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JTextArea;

//TODO
/* things to store in file: (noting that the file needs to be rewritten)
   * Game Stats (long term stats):
        * highest score
        * highest multiplier
        * total kills
        * powerups collected
        * times died
        * total score
        * ???
   * most recent record - method
   * files containing each record set (name + score)
   * 
 * Also requires a method that saves to file on abrupt exit of program
 *    called from wherever makes sense
*/



//maybe returns text for a textarea rather than being one
public class LeaderBoard {//extends JTextArea {

	public static final String 
		HARD = "hard.txt", MED = "medium.txt", EASY = "easy.txt";
	private static final String STATS = "stats.txt", SETTINGS = "settings.txt",
			SCREEN_WIDTH = "width ", SCREEN_HEIGHT = "height ",
			IF_PARTICLES = "particles ", IF_ANTIALIASING = "antialiasing "; //.... [note the space]
	
	/*
	//Current game values
	private int score, totalKills;
	
	private double bulletSpeed;
	
	//Settings:
	private int width, height;
	private double scale;
	
	private boolean ifParticles; //should have more than on/off options
	private boolean ifAliasing;
	*/
	
	//returns a int[]: width, height, other boolean (1 = true)
	public static int[] readSettings() {
		return null;
		//TODO got bored of files
	}
	
	
	//change settings
	public static void writeSettings(int width, int height, boolean[] settings) {
		File file = new File(SETTINGS);
		
		PrintWriter out = null;
		try {
			file.createNewFile(); //only works if it doesn't exist
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
	
	
	//adds to the stats.txt file
	public static void addToStats(GameState state) {
		File file = new File(STATS);
		
		PrintWriter out = null;
		try {
			file.createNewFile(); //only works if it doesn't exist
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO, got lost...
		
		out.close();
	}
	
	//returns the stats saved in the stats.txt
	public static String getStats() {
		return "";
	}
	
	//writes scores to file (under the current difficulty)
	public static void writeScore(String diff, int score, String name) {
		File file = new File(diff);
		
		LinkedList<Record> records = new LinkedList<Record>();
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) { //write scores to list
					records.add(new Record(scores.nextInt(), scores.next()));
				}
				scores.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
		}
		records.add(new Record(score, name)); //add new record
		
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
		File file = new File(diff);
		
		LinkedList<Record> records = new LinkedList<Record>();
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) { //write scores to list
					records.add(new Record(scores.nextInt(), scores.next()));
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
		
		return records.getFirst().getScore();
	}
	
	
	////JTextArea stuff:

	//returns the TextArea of the scores (for displaying)
	public static JTextArea getLeaderBoard(String diff) {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		
		File file = new File(diff);
		if (!file.exists()) {
			throw new IllegalArgumentException("Input file must exist for this method");
		}
		
		String text = getText(file);
		
		if (diff.equals(EASY)) {
			area.setText("Easy Scores\n"+ text);
		} else if (diff.equals(MED)) {
			area.setText("Medium Scores\n" + text);
		} else if (diff.equals(HARD)){
			area.setText("Hard Scores\n" + text);
		}
		
		return area;
	}
	
	//returns a string containing the records for the given file
	
	//returns the text from the file for the text area 
	private static String getText(File file) {
		LinkedList<Record> records = new LinkedList<Record>();
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) {
					records.add(new Record(scores.nextInt(), scores.next()));
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
	
	Record (int score, String name) {
		this.score = score;
		this.name = name;
	}
	
	public int getScore() {
		return this.score; 
	}
	
	public String toString() { //for saving to file
		return (score + "\t " + name);
	}
	
	public String toLineString() { //for writing to leader board
		return (score + "\t|  "+ name);
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
