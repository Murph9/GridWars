package game.logic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class GameMenuGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	final TheGame theGame;
	JPanel menuPanel; //for the menu
	
	private double scale = 10; //you know, it kind of works, as easy as this number is 
		//TODO keep gameSize the sameish
	
	private GridBagConstraints gbLayout;
	private static JPanel leaderBoard;
	
	private ButtonGroup group;
	
	
	//More settings?
	JCheckBox particles;
	JSpinner particleCount;
	
	JCheckBox sound;
	JCheckBox antialiasing;
	
	JTextField pixelWidth, pixelHeight;
	JTextField boardWidth, boardHeight;
	
	JTextField nameField;

	
	GameMenuGUI(final TheGame theGame) {
		this.theGame = theGame;
		
		menuPanel = new JPanel();
		menuPanel.setLayout(new GridBagLayout());
		this.add(menuPanel);

		JPanel newGamePanel = new JPanel();
		newGamePanel.setLayout(new GridBagLayout());
		newGamePanel.setBorder(BorderFactory.createTitledBorder("New Game"));

		
		////////////////////////////////////////////////
		////Game Mode
		JRadioButton buttonEasy = new JRadioButton("Easy");
		buttonEasy.setActionCommand(GameEngine.EASY_D);
		
		JRadioButton buttonMed = new JRadioButton("Med");
		buttonMed.setActionCommand(GameEngine.MEDIUM_D);
		
		JRadioButton buttonHard = new JRadioButton("Hard");
		buttonHard.setActionCommand(GameEngine.HARD_D);
		
		group = new ButtonGroup();
		group.add(buttonEasy);
		group.add(buttonMed);
		group.add(buttonHard);
		
		buttonMed.setSelected(true); //because its hard coded somewhere else i think (in this file)
		
		buttonEasy.addActionListener(this);
		buttonMed.addActionListener(this);
		buttonHard.addActionListener(this);
		
		JButton go = new JButton("Start");
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
		            AbstractButton button = buttons.nextElement();

		            if (button.isSelected()) {
		            	theGame.initGame(button.getActionCommand());
		            }
		        }
			}
		});
		
		JButton stats = new JButton("Stats");
		stats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "\n"+FileHelper.getStats());
			}
		});
		
		GridBagConstraints a = new GridBagConstraints();
		a.fill = GridBagConstraints.HORIZONTAL;
		
		a.gridx = 0;
		a.gridy = 0;
		a.gridwidth = 6;
		newGamePanel.add(go, a);
		
		a.gridwidth = 1;
		a.gridx++;
		a.gridy++;
		newGamePanel.add(buttonEasy, a);
		a.gridx++;
		newGamePanel.add(buttonMed, a);
		a.gridx++;
		newGamePanel.add(buttonHard, a);
		
		///////////////////////////////////////////////////////
		////Other
		
		JPanel otherPanel = new JPanel();
		otherPanel.setLayout(new GridBagLayout());
		otherPanel.setBorder(BorderFactory.createTitledBorder("Other"));
		a = new GridBagConstraints();
		a.insets = new Insets(3, 3, 3, 3);
		
		a.gridx = 0;
		a.gridy = 0;
		a.gridwidth = 1;
		otherPanel.add(new JLabel("Name:"), a);

		a.gridx++;
		nameField = new JTextField("null*");
		otherPanel.add(nameField, a);
		
		a.gridx = 0;
		a.gridy++;
		a.gridwidth = 2;
		otherPanel.add(stats, a);

		////////////////////////////////////////////////// 
		////Settings
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		GridBagConstraints set = new GridBagConstraints();
		
		JPanel widthXHeight = new JPanel();
		widthXHeight.setLayout(new GridBagLayout());
		widthXHeight.setBorder(BorderFactory.createTitledBorder("Width x Height"));
		
		GridBagConstraints wxh = new GridBagConstraints();
		wxh.insets = new Insets(3,3,3,3);
		
		//labels
		wxh.gridx = 0;
		wxh.gridy = 0;
		widthXHeight.add(new JLabel("Screen"), wxh);

		wxh.gridy++;
		widthXHeight.add(new JLabel("Game Field"), wxh);
		wxh.fill = GridBagConstraints.HORIZONTAL;
		
		//textboxes
		wxh.gridx++;
		wxh.gridy = 0;
		pixelWidth = new JTextField("null*");
		widthXHeight.add(pixelWidth, wxh);
		
		wxh.gridy++;
		boardWidth = new JTextField("null*");
		widthXHeight.add(boardWidth, wxh);

		wxh.gridx++;
		wxh.gridy = 0;
		widthXHeight.add(new JLabel("x"), wxh);
		wxh.gridy++;
		widthXHeight.add(new JLabel("x"), wxh);
		
		wxh.gridx++;
		wxh.gridy = 0;
		
		pixelHeight = new JTextField("null*");
		widthXHeight.add(pixelHeight, wxh);
		
		wxh.gridy++;
		boardHeight = new JTextField("null*");
		widthXHeight.add(boardHeight, wxh);

		//checkboxes
		set.gridx = 0;
		set.gridy = 0;
		particles = new JCheckBox("Particles");
		settingsPanel.add(particles, set);
		
		set.gridx++;
		particleCount = new JSpinner(new SpinnerNumberModel(0, 0, 100, 10));
		settingsPanel.add(particleCount);
		set.gridx++;
		settingsPanel.add(new JLabel("%"));
		
		set.gridx = 0;
		set.gridy++;
		antialiasing = new JCheckBox("Antialiasing");
		settingsPanel.add(antialiasing, set);
		
		set.gridy++;
		sound = new JCheckBox("Sound");
		settingsPanel.add(sound, set);
		
		this.pack();
		
		//////////////////////////////////////////////////
		////Heading Info + positions		
		gbLayout = new GridBagConstraints();
		gbLayout.fill = GridBagConstraints.BOTH;
		gbLayout.insets = new Insets(5,5,5,5);
		
		JPanel headPanel = new JPanel();
		headPanel.setLayout(new GridBagLayout());
		
		JTextArea t = new JTextArea("Welcome to NotGridWars2\nArrow keys or WASD to move, "
				+ "right/left mouse button do things.\nEsc is pause.");
		t.setEditable(false);
		t.setOpaque(false);
		headPanel.add(t, gbLayout);
		
		gbLayout.gridy = 0;
		gbLayout.gridx = 0;
		gbLayout.gridwidth = 2;
		menuPanel.add(headPanel, gbLayout);
		
		gbLayout.gridy++;
		gbLayout.gridwidth = 1;
		menuPanel.add(newGamePanel, gbLayout);
		
		gbLayout.gridy++;
		menuPanel.add(otherPanel, gbLayout);

		gbLayout.gridx = 0;
		gbLayout.gridy = 3;
		menuPanel.add(widthXHeight, gbLayout);
		
		gbLayout.gridwidth = 1;
		gbLayout.gridx++;
		menuPanel.add(settingsPanel, gbLayout);

		////Scoreboard
		leaderBoard = FileHelper.getLeaderBoard(GameEngine.MEDIUM_D);
		
		gbLayout.gridx = 1;
		gbLayout.gridy = 1;
		gbLayout.gridwidth = 1;
		gbLayout.gridheight = 2;
		menuPanel.add(leaderBoard, gbLayout);
		
		//////////////////////////////////////////////////
		GameSettings fileSettings = FileHelper.readSettings();
		pixelWidth.setText(""+fileSettings.getPixelWidth());
		pixelHeight.setText(""+fileSettings.getPixelHeight());
		
		boardWidth.setText(""+fileSettings.getBoardWidth());
		boardHeight.setText(""+fileSettings.getBoardHeight());
		
		nameField.setText(fileSettings.getName());
		
		particles.setSelected(fileSettings.ifParticles());
		antialiasing.setSelected(fileSettings.ifAliasing());
		sound.setSelected(fileSettings.ifSound());
		
		particleCount.setValue(fileSettings.getParticleCount());
	}
	

    //rewrite the leaderboard to represent the current difficulty set
    @Override
	public void actionPerformed(ActionEvent evt) {
    	menuPanel.remove(leaderBoard);
    	
    	leaderBoard = FileHelper.getLeaderBoard(evt.getActionCommand());
    	menuPanel.add(leaderBoard, gbLayout);
    	
    	this.revalidate();
    	this.repaint();
	}
    
	public boolean hasValidScreenValues() {
		if (pixelWidth.getText().matches("[0-9]+") && pixelHeight.getText().matches("[0-9]+")) {
			return true;
		}
		return false;
	}
	
	public boolean hasValidBoardValues() {
		if (boardWidth.getText().matches("[0-9]+") && boardHeight.getText().matches("[0-9]+")) {
			return true;
		}
		return false;
	}


	public int getPixelWidth() {
		return Integer.parseInt(pixelWidth.getText());
	}
	public int getPixelHeight() {
		return Integer.parseInt(pixelHeight.getText());
	}

	public int getBoardWidth() {
		return Integer.parseInt(boardWidth.getText());
	}
	public int getBoardHeight() {
		return Integer.parseInt(boardHeight.getText());
	}

	
	public GameSettings getSettings() {
		GameSettings set = new GameSettings(getPixelWidth(), getPixelHeight(), getBoardWidth(), getBoardHeight(), scale);
		set.setIfAliasing(antialiasing.isSelected());
		set.setIfSound(sound.isSelected());
		set.setIfParticles(particles.isSelected());
		set.setName(nameField.getText());
		
		set.setParticleCount((int)particleCount.getValue());
		return set;
	}
}
