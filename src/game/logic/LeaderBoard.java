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
//TODO rewrite please so that it stores the info itself, rather than to file
//TODO


public class LeaderBoard extends JTextArea {

	public static final String 
		HARD = "hard", MED = "medium", EASY = "easy";
	
	private static final long serialVersionUID = 1L;
	private LinkedList<Record> records;
	private String file;
	
	private File saveFile;
	
	//is just a text area
	LeaderBoard(String gameType, boolean editable) {
		
		this.file = gameType;
		if (this.file.equals(HARD) || this.file.equals(MED) || this.file.equals(EASY)) {
			this.file  = this.file+".txt"; //<name>.txt
			
			this.saveFile = new File(this.file);
		} else {
			//try again
		}
		
		setEditable(editable); //no editing the textbox please
		
		this.records = new LinkedList<Record>(); //start to read records
		try {
			Scanner oldScores = new Scanner(new FileReader(this.file));
			while(oldScores.hasNext()) {
				this.records.add(new Record(oldScores.nextInt(), oldScores.next()));
			}
			oldScores.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Collections.sort(this.records, new Comparator<Record>() {
			public int compare(Record a, Record b) {
				if (a.equals(b)) { return 0; }
				if (a.getScore() <= b.getScore()) { 
					return 1; // a
				}
				return -1; // b
			}
		});
	}
	
	
	//add score to the current records and write to file them in order
	public void addScore(int score, String name) {

		Record temp = new Record(score, name);
		if (this.records.contains(temp)) return; //there are duplicates sometimes
		this.records.add(temp);
		
		Collections.sort(this.records, new Comparator<Record>() { //sort the Record list
			public int compare(Record a, Record b) {
				if (a.equals(b)) { return 0; }
				if (a.getScore() <= b.getScore()) { 
					return 1; // a
				}
				return -1; // b
			}
		});
		
		this.saveFile.delete();
		this.saveFile = new File(this.file);
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(this.saveFile, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (out == null) return; //freak out I guess
		
		int count = Math.min(this.records.size(), 10);
		for (int i = 0; i < count; i++) { //save the best min(records.size(), 15) to file
			out.println(this.records.get(i));
		}
		
		out.close();
		writeScore(score); //with the currently 
	}
	

	public void writeScore(int score) { //makes score bold
		setText(this.file +" - LeaderBoard\n" + "Score\t|  Name\n"); //Heading

		int count = 0;
		for (Record i: this.records) {
			if (i.getScore() == score) {
				setText(getText() +"*" + i.toLineString() + "\n");
			} else {
				setText(getText() + i.toLineString() + "\n");
			}
			if (count >= 10) break;
			count++;
		}
		revalidate();
		repaint();
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
