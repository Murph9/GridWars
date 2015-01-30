package game.logic;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**Handles all file reading through static methods.
 * @author Jake Murphy
 */
public class LeaderBoard {

	//Statistics
	private static final String STATS = "stats.txt", 
			HIGHEST_SCORE = "high_score", HIGHEST_MULTI = "high_multiplier",
			TOTAL_POWERUPS = "total_powerups",	TOTAL_KILLS = "total_kills", 
			TOTAL_SCORE = "total_score", TOTAL_TIME = "total_time", TOTAL_DEATHS = "total_deaths",
			LONGEST_GAME = "longest_game";
		//other ideas: times played, 

	//Settings
	private static final String SETTINGS = "settings.txt",
			PIXEL_WIDTH = "pixel_width", PIXEL_HEIGHT = "pixel_height",
			BOARD_WIDTH = "board_width", BOARD_HEIGHT = "board_height", SCALE = "scale",
			IF_PARTICLES = "particles", IF_ANTIALIASING = "antialiasing", IF_SOUND = "sound",
			NAME = "name";
	
	
	///////////////////////
	//SETTINGS:
	
	//returns a int[]: width, height, other boolean (1 = true, 0 = false - same as java)
	public static GameSettings readSettings() {
		File file = new File(SETTINGS);

		int pixel_width = -1, pixel_height = -1, board_width = -1, board_height = -1;
		double scale = -1;
		boolean	particles = true, antialiasing = true, sound = true;
		String name = "null";
		
		if (file.exists()) {
			try {
				Scanner scores = new Scanner(new FileReader(file));
				while (scores.hasNext()) {
					String text = scores.next(); //because everything was spaced... TODO FIX THIS MESS
					if (text.equals(PIXEL_WIDTH)) pixel_width = scores.nextInt();
					else if (text.equals(PIXEL_HEIGHT)) pixel_height = scores.nextInt();
					
					else if (text.equals(BOARD_WIDTH)) board_width = scores.nextInt();
					else if (text.equals(BOARD_HEIGHT)) board_height = scores.nextInt();

					else if (text.equals(NAME)) name = scores.next();
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
						name = "me";
						
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
			name = "me";
			
			GameSettings a = new GameSettings();
			writeSettings(a); //so it now exists to read from
			return a;
		}
		
		GameSettings set = new GameSettings(pixel_width, pixel_height, board_width, board_height, scale);
		set.setIfAliasing(antialiasing);
		set.setIfParticles(particles);
		set.setIfSound(sound); //wouldn't fit into constructor
		set.setName(name);
		
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
		
		out.println(PIXEL_WIDTH +" "+ settings.getPixelWidth());
		out.println(PIXEL_HEIGHT+" "+ settings.getPixelHeight());
		
		out.println(BOARD_WIDTH +" "+ settings.getBoardWidth());
		out.println(BOARD_HEIGHT+" "+ settings.getBoardHeight());
		out.println(SCALE +" "+ settings.getScale());
		
		out.println(NAME +" "+ settings.getName());
		
		out.println(IF_SOUND +" "+ settings.ifSound());
		out.println(IF_PARTICLES +" "+ settings.ifParticles());
		out.println(IF_ANTIALIASING +" "+ settings.ifAliasing());
		
		out.close();
	}
	

	/////////////////////////
	//STATS:
	
	//adds the new stats to the stats.txt file
	public static void addToStats(GameState state) {
		File file = new File(STATS);
		
		Scanner stats = null;
		int hiScore = 0, multi = 0, powerups = 0, kills = 0, totalScore = 0, time = 0, deaths = 0, longest = 0;
		
		try {
			if (!file.exists()) {
				createNewStats(file);
			}
			stats = new Scanner(new FileReader(file));

			
			while (stats.hasNext()) {
				String next = stats.next();
				int INT = stats.nextInt();
				
				if (next.equals(HIGHEST_SCORE)) {
					hiScore = Math.max(INT, state.getScore());
				} else if (next.equals(HIGHEST_MULTI)) {
					multi = Math.max(INT, state.getMultiplier());
				} else if (next.equals(TOTAL_POWERUPS)) {
					powerups = INT + state.getPowerUpCount();
				} else if (next.equals(TOTAL_KILLS)) {
					kills = INT + state.getTotalKills();
				} else if (next.equals(TOTAL_SCORE)) {
					totalScore = INT + state.getScore();
				} else if (next.equals(TOTAL_TIME)) {
					time = INT + (int)state.getTime();
				} else if (next.equals(TOTAL_DEATHS)) {
					deaths = INT + state.getTotalDeaths();
				} else if (next.equals(LONGEST_GAME)) {
					longest = Math.max(INT, (int)state.getTime());
				}
			}
			stats.close();
			
			file.delete();
			file.createNewFile();
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			
			out.println(HIGHEST_SCORE + " " + hiScore);
			out.println(HIGHEST_MULTI + " " + multi); //simple default values
			out.println(TOTAL_POWERUPS + " " + powerups);
			out.println(TOTAL_KILLS + " " + kills);
			out.println(TOTAL_SCORE + " " + totalScore);
			out.println(TOTAL_TIME + " " + time);
			out.println(TOTAL_DEATHS + " " + deaths);
			out.println(LONGEST_GAME + " " + longest);
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
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

	private static void createNewStats(File file) throws IOException {
		file.createNewFile();
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		
		out.println(HIGHEST_SCORE + " 0");
		out.println(HIGHEST_MULTI + " 0"); //simple default values
		out.println(TOTAL_POWERUPS + " 0");
		out.println(TOTAL_KILLS + " 0");
		out.println(TOTAL_SCORE + " 0");
		out.println(TOTAL_TIME + " 0");
		out.println(TOTAL_DEATHS + " 0");
		out.println(LONGEST_GAME + " 0");
		
		out.close();
	}
	

	//returns the stats saved in the stats.txt
	public static String getStats() {
		File file = new File(STATS);
		
		Scanner stats = null;
		HashMap<String, Integer> statSet = null;
		try {
			if (!file.exists()) {
				createNewStats(file);
			}
			stats = new Scanner(new FileReader(file));

			statSet = new HashMap<String, Integer>();
			while (stats.hasNext()) {
				statSet.put(stats.next(), stats.nextInt());
			}
			stats.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		String out = "";
		for (String a: statSet.keySet()) {
			out = out + a + ":\t " + statSet.get(a).toString() + "\n";
		}
		
		return out;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////
	//SCORES:
	
	//writes scores to file (under the current difficulty)
	public static void writeScore(String diff, int score, String name, int time) {
		diff += GameEngine.EXT_D;

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

		Record incoming = new Record(score, name, time);
		if (!records.contains(incoming)) { //no duplicates please
			records.add(incoming);
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
		diff += GameEngine.EXT_D;
		
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
	public static JPanel getLeaderBoard(String diff) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		diff += GameEngine.EXT_D;
		File file = new File(diff);
		if (!file.exists()) {
			throw new IllegalArgumentException("Input file must exist for this method");
		}
		
		String label = "null";
		
		if (diff.equals(GameEngine.EASY_D + GameEngine.EXT_D)) {
			label = "Easy Records";
		} else if (diff.equals(GameEngine.MEDIUM_D + GameEngine.EXT_D)) {
			label = "Medium Records";
		} else if (diff.equals(GameEngine.HARD_D + GameEngine.EXT_D)){
			label = "Hard Records";
		} else {
			throw new IllegalArgumentException("Please use one of the default difficulties");
		}
		panel.setBorder(BorderFactory.createTitledBorder(label));
		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		panel.add(getScoresJPanel(file), g);

		return panel;
	}
	
	//as a jpanel
	private static JPanel getScoresJPanel(File file) {
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
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		String positionS = "";
		String scoreS = "";
		String nameS = "";
		String timeS = "";
		
		int count = 0;
		for (Record i: records) {
			positionS += "\n" + (count+1);
			scoreS += "\n" + i.getScore();
			nameS += "\n" + i.getName();
			timeS += "\n" + i.getTime();
			
			if (count >= 10) break;
			count++;
		}
		
		positionS = positionS.replaceFirst("^\n", "");
		scoreS = scoreS.replaceFirst("^\n", "");
		nameS = nameS.replaceFirst("^\n", "");
		timeS = timeS.replaceFirst("^\n", "");
		
		JTextArea position = new JTextArea(positionS);
		JTextArea score = new JTextArea(scoreS);
		JTextArea name = new JTextArea(nameS);
		JTextArea time = new JTextArea(timeS);
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 0;
		panel.add(position, c);
		c.gridx++;
		panel.add(score, c);
		c.gridx++;
		panel.add(name, c);
		c.gridx++;
		panel.add(time, c);
		
		return panel;
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
	public String getName() { return this.name; }
	
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
		
		if (score == in.score && name.equals(in.name)) { //deliberately ignoring time
			return true;
		}
		return false;
	}
}




//returns the text from the file for the text area
//private static String getScoresText(File file) {
//	LinkedList<Record> records = new LinkedList<Record>();
//	
//	if (file.exists()) {
//		try {
//			Scanner scores = new Scanner(new FileReader(file));
//			while (scores.hasNext()) {
//				records.add(new Record(scores.nextInt(), scores.next(), scores.nextInt()));
//			}
//			scores.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	Collections.sort(records, new Comparator<Record>() {
//		public int compare(Record a, Record b) {
//			if (a.equals(b)) { return 0; }
//			if (a.getScore() <= b.getScore()) { 
//				return 1; // a
//			}
//			return -1; // b
//		}
//	});
//	
//	String text = "";
//	
//	int count = 0;
//	for (Record i: records) {
//		text += ("\n" +(count+1) + " - " + i.toLineString());
//
//		if (count >= 10) break;
//		count++;
//	}
//	text = text.replaceFirst("^\n", ""); //remove first newline...
//	
//	return text;
//}
