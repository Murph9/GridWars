package game.logic;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.*;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**Handles all file reading through static methods.
 * @author Jake Murphy
 */
public class FileHelper {

	//save file folder
	private static final String SAVE_FOLDER = System.getProperty("user.home")+"/.murph9/gridwars/";
	
	//Statistics
	private static final String STATS = "stats.txt", 
			HIGHEST_SCORE = "high_score", HIGHEST_MULTI = "high_multiplier",
			TOTAL_POWERUPS = "total_powerups",	TOTAL_KILLS = "total_kills", 
			TOTAL_SCORE = "total_score", TOTAL_TIME = "total_time", TOTAL_DEATHS = "total_deaths",
			LONGEST_GAME = "longest_game", TIMES_LOADED = "time_loaded";

	//Settings
	private static final String SETTINGS = "settings.txt",
			PIXEL_WIDTH = "pixel_width", PIXEL_HEIGHT = "pixel_height",
			BOARD_WIDTH = "board_width", BOARD_HEIGHT = "board_height", SCALE = "scale",
			IF_PARTICLES = "particles", PARTICLE_PERCENT = "particle_percent", IF_ANTIALIASING = "antialiasing", IF_SOUND = "sound",
			NAME = "name", DIFFICULTY = "difficulty", PLAYER_INERTIA = "player_inertia",
			GRID_X_COUNT = "grid_x_count", GRID_Y_COUNT = "grid_y_count";
	
	//no setting for debug options please, its someting that you should always have to turn on
	
	//////////////////////////////////////////////////////////////////////////////////
	//SETTINGS:
	
	public static GameSettings readSettings() {
		File file = new File(SAVE_FOLDER+SETTINGS);

		boolean needsNew = false;
		GameSettings result = null;
		
		if (file.exists()) {
			try {
				Scanner settings = new Scanner(new FileReader(file));
				result = new GameSettings();
				while (settings.hasNext()) {
					String text = settings.next(); //read it (don't really care for the order)
					if (text.equals(PIXEL_WIDTH)) result.pixelWidth = settings.nextInt();
					else if (text.equals(PIXEL_HEIGHT)) result.pixelHeight = settings.nextInt();
					
					else if (text.equals(BOARD_WIDTH)) result.boardWidth = settings.nextInt();
					else if (text.equals(BOARD_HEIGHT)) result.boardHeight = settings.nextInt();

					else if (text.equals(NAME)) result.name = settings.next();
					else if (text.equals(PLAYER_INERTIA)) result.inertia = settings.nextInt();
					else if (text.equals(SCALE)) result.scale = settings.nextDouble();
					
					else if (text.equals(DIFFICULTY)) result.diff = Engine.Difficulty.valueOf(settings.next());
					else if (text.equals(GRID_X_COUNT)) result.gridXCount = settings.nextInt();
					else if (text.equals(GRID_Y_COUNT)) result.gridYCount = settings.nextInt();
					
					else if (text.equals(IF_SOUND)) result.ifSound = settings.nextBoolean();
					else if (text.equals(IF_PARTICLES)) result.ifParticles = settings.nextBoolean(); 
					else if (text.equals(IF_ANTIALIASING)) result.ifAliasing = settings.nextBoolean();
					
					else if (text.equals(PARTICLE_PERCENT)) result.particlePercent = settings.nextInt();
					
					else {
						//TODO message that it was overwritten
						
						needsNew = true;
						break;
					}
				}
				settings.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		} else { //default settings are are below
			needsNew = true;
		}
		
		if (needsNew) {
			result = new GameSettings();
			writeSettings(result); //so it now exists to read from
		}
		
		return result;
	}
	
	//change settings (not many so far) works though
	public static void writeSettings(GameSettings settings) {
		File file = new File(SAVE_FOLDER+SETTINGS);
		
		PrintWriter out = null;
		try {
			file.delete(); //because it will append otherwise
			file.createNewFile(); //will always work here
			out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.println(PIXEL_WIDTH +" "+ settings.pixelWidth);
		out.println(PIXEL_HEIGHT+" "+ settings.pixelHeight);
		
		out.println(BOARD_WIDTH +" "+ settings.boardWidth);
		out.println(BOARD_HEIGHT+" "+ settings.boardHeight);
		out.println(SCALE +" "+ settings.scale);
		
		out.println(NAME +" "+ settings.name);
		out.println(PLAYER_INERTIA + " " + settings.inertia);
		
		out.println(DIFFICULTY + " " + settings.diff);
		out.println(GRID_X_COUNT + " " + settings.gridXCount);
		out.println(GRID_Y_COUNT + " " + settings.gridYCount);
		
		out.println(IF_SOUND +" "+ settings.ifSound);
		out.println(IF_PARTICLES +" "+ settings.ifParticles);
		out.println(IF_ANTIALIASING +" "+ settings.ifAliasing);
		
		out.println(PARTICLE_PERCENT +" "+ settings.particlePercent);
		
		out.close();
	}
	

	//////////////////////////////////////////////////////////////////////////////////
	//STATS:
	
	//adds the new stats to the stats.txt file
	public static void addToStats(GameState state) {
		File file = new File(SAVE_FOLDER+STATS);
		
		Scanner stats = null;
		int hiScore = 0, multi = 0, powerups = 0, kills = 0, totalScore = 0, 
					time = 0, deaths = 0, longest = 0, loaded = 0;
		
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
				} else if (next.equals(TIMES_LOADED)) {
					loaded = INT + 1;
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
			out.println(TIMES_LOADED + " " + loaded);
			
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
		out.println(TIMES_LOADED + " 0");
		
		out.close();
	}
	
	//returns the stats saved in the stats.txt
	public static JPanel getStats() { //TODO i think total_time is wrong
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JTextArea labels = new JTextArea("Field: ");
		labels.setOpaque(false);
		
		JTextArea values = new JTextArea("Value: ");
		values.setOpaque(false);
		
		File file = new File(SAVE_FOLDER+STATS);
		Scanner stats = null;
		try {
			if (!file.exists()) {
				createNewStats(file);
			}
			stats = new Scanner(new FileReader(file));

			while (stats.hasNext()) {
				labels.setText(labels.getText()+ "\n" +stats.next());
				values.setText(values.getText()+ "\n" +stats.nextInt());
			}
			stats.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		panel.add(labels, c);
		
		c.gridx++;
		panel.add(values, c);
		
		return panel;
	}

	//////////////////////////////////////////////////////////////////////////////////
	//SCORES:
	//////////////////////////////////////////////////////////////////////////////////
	
	private static String diffToFileName(Engine.Difficulty diff) {
		return SAVE_FOLDER+diff.toString()+Engine.EXT_D;
	}
	
	//writes scores to file (under the current difficulty)
	public static void writeScore(Engine.Difficulty diff, int score, String name, int time) {
		File file = new File(diffToFileName(diff));
		
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
		file = new File(diffToFileName(diff));
		
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
	public static int getBestScore(Engine.Difficulty diff) {
		File file = new File(diffToFileName(diff));
		
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
		
		if (records.isEmpty()) {
			return 0;
		} else {
			return records.getFirst().getScore();
		}
	}
	
	
	////////////////////////
	//JTextArea stuff:

	//returns the TextArea of the scores (for displaying)
	public static JPanel getLeaderBoard(Engine.Difficulty diff) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		//try to open
		File file = new File(diffToFileName(diff));
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String label = null;
		switch(diff){
			case easy:
				label = "Easy Records";
				break;
			case medium:
				label = "Medium Records";
				break;
			case hard:
				label = "Hard Records";
				break;
			default:
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
		
		String positionS = "#";
		String scoreS = "Score:";
		String nameS = "Name:";
		String timeS = "Time:";
		
		int count = 0;
		for (Record i: records) {
			positionS += "\n" + (count+1);
			scoreS += "\n" + i.getScore();
			nameS += "\n" + i.getName();
			timeS += "\n" + i.getTime();
			
			if (count >= 10) break;
			count++;
		}
		
//		positionS = positionS.replaceFirst("^\n", ""); //just here for reference (removes leading '\n')
		
		JTextArea position = new JTextArea(positionS);
		position.setOpaque(false); //because of the 'ghastly' white colour behind the text
		JTextArea score = new JTextArea(scoreS);
		score.setOpaque(false);
		JTextArea name = new JTextArea(nameS);
		name.setOpaque(false);
		JTextArea time = new JTextArea(timeS);
		time.setOpaque(false);
		
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
