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
			IF_PARTICLES = "particles", PARTICLE_COUNT = "particle_count", IF_ANTIALIASING = "antialiasing", IF_SOUND = "sound",
			NAME = "name", DIFFICULTY = "difficulty", PLAYER_INERTIA = "player_inertia",
			GRID_X_COUNT = "grid_x_count", GRID_Y_COUNT = "grid_y_count";
	
	//no setting for debug options please, its someting that you should always have to turn on
	
	//////////////////////////////////////////////////////////////////////////////////
	//SETTINGS:
	
	//returns a int[]: width, height, other boolean (1 = true, 0 = false - same as java)
	public static GameSettings readSettings() {
		File file = new File(SETTINGS);

		int pixel_width = -1, pixel_height = -1, board_width = -1, board_height = -1, particleCount = 0;
		double scale = -1;
		boolean	particles = true, antialiasing = true, sound = true;
		String name = "null", diff = "null";
		int gridX = -1, gridY = -1, inertia = 4;
		
		if (file.exists()) {
			try {
				Scanner settings = new Scanner(new FileReader(file));
				while (settings.hasNext()) {
					String text = settings.next(); //read it (don't really care for the order)
					if (text.equals(PIXEL_WIDTH)) pixel_width = settings.nextInt();
					else if (text.equals(PIXEL_HEIGHT)) pixel_height = settings.nextInt();
					
					else if (text.equals(BOARD_WIDTH)) board_width = settings.nextInt();
					else if (text.equals(BOARD_HEIGHT)) board_height = settings.nextInt();

					else if (text.equals(NAME)) name = settings.next();
					else if (text.equals(PLAYER_INERTIA)) inertia = settings.nextInt();
					else if (text.equals(SCALE)) scale = settings.nextDouble();
					
					else if (text.equals(DIFFICULTY)) diff = settings.next();
					else if (text.equals(GRID_X_COUNT)) gridX = settings.nextInt();
					else if (text.equals(GRID_Y_COUNT)) gridY = settings.nextInt();
					
					else if (text.equals(IF_SOUND)) sound = settings.nextBoolean();
					else if (text.equals(IF_PARTICLES)) particles = settings.nextBoolean(); 
					else if (text.equals(IF_ANTIALIASING)) antialiasing = settings.nextBoolean();
					
					else if (text.equals(PARTICLE_COUNT)) particleCount = settings.nextInt();
					
					else {
						//rewrite is needed but, for testing this will just say:
						System.err.println("Reverting back to default settings, as something broke.\nOld settings moved to old_####");
						File oldSettings = new File("old_"+SETTINGS);
						file.renameTo(oldSettings);
						
						GameSettings b = new GameSettings();
						pixel_width = 1024;
						pixel_height = 728;
						sound = true;
						particles = true;
						antialiasing = true;
						name = "me";
						inertia = 4;
						particleCount = 100;
						
						gridX = 100;
						gridY = 100;
						
						file.delete();
						writeSettings(b);
						settings.close();
						return b;
					}
				}
				settings.close();

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
			inertia = 4;
			particleCount = 100;
			
			gridX = 100;
			gridY = 100;
			
			GameSettings a = new GameSettings();
			writeSettings(a); //so it now exists to read from
			return a;
		}
		
		GameSettings set = new GameSettings(pixel_width, pixel_height, board_width, board_height, scale);
		set.setIfAliasing(antialiasing);
		set.setIfParticles(particles);
		set.setIfSound(sound); //all this wouldn't fit into constructor
		set.setName(name);
		set.setInertia(inertia);
		set.setParticlePercentage(particleCount);
		
		set.setDifficulty(diff);
		set.setGridXCount(gridX);
		set.setGridYCount(gridY);
		
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
		out.println(PLAYER_INERTIA + " " + settings.getInertia());
		
		out.println(DIFFICULTY + " " + settings.getDifficulty());
		out.println(GRID_X_COUNT + " " + settings.getGridXCount());
		out.println(GRID_Y_COUNT + " " + settings.getGridYCount());
		
		out.println(IF_SOUND +" "+ settings.ifSound());
		out.println(IF_PARTICLES +" "+ settings.ifParticles());
		out.println(IF_ANTIALIASING +" "+ settings.ifAliasing());
		
		out.println(PARTICLE_COUNT +" "+ settings.getParticlePercentage());
		
		out.close();
	}
	

	//////////////////////////////////////////////////////////////////////////////////
	//STATS:
	
	//adds the new stats to the stats.txt file
	public static void addToStats(GameState state) {
		File file = new File(STATS);
		
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
		
		File file = new File(STATS);
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
	
	//writes scores to file (under the current difficulty)
	public static void writeScore(String diff, int score, String name, int time) {
		diff += Engine.EXT_D;

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
		diff += Engine.EXT_D;
		
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
		
		diff += Engine.EXT_D;
		File file = new File(diff);
		if (!file.exists()) {
			throw new IllegalArgumentException("Input file must exist for this method");
		}
		
		String label = "null";
		
		if (diff.equals(Engine.EASY_D + Engine.EXT_D)) {
			label = "Easy Records";
		} else if (diff.equals(Engine.MEDIUM_D + Engine.EXT_D)) {
			label = "Medium Records";
		} else if (diff.equals(Engine.HARD_D + Engine.EXT_D)){
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
