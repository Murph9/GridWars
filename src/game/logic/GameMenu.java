package game.logic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.*;

public class GameMenu extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	final TheGame theGame; //the actual game
	JPanel menuPanel; //for the menu
	
	private GridBagConstraints gbLayout;
	private static JPanel leaderBoard; //panel for showing the currently viewing leaderboard
	private ButtonGroup group; //yay buttons
	
	//More settings?
	JCheckBox particles;
	JSpinner particleCount;
	
	JCheckBox sound;
	JCheckBox antialiasing;
	
	JTextField pixelWidth, pixelHeight;
	JTextField boardWidth, boardHeight;
	
	JTextField gridXCount, gridYCount;
	
	JTextField nameField; //for the record name
	JSpinner inertiaField; //for preference
	
	
	GameMenu(final TheGame theGame) {
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
		buttonEasy.setActionCommand(Engine.Difficulty.easy.toString());
		
		JRadioButton buttonMed = new JRadioButton("Med");
		buttonMed.setActionCommand(Engine.Difficulty.medium.toString());
		
		JRadioButton buttonHard = new JRadioButton("Hard");
		buttonHard.setActionCommand(Engine.Difficulty.hard.toString());
		
		group = new ButtonGroup();
		group.add(buttonEasy);
		group.add(buttonMed);
		group.add(buttonHard);
		
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
		            	theGame.initGame(Engine.Difficulty.valueOf(button.getActionCommand()));
		            }
		        }
			}
		});
		
		JButton stats = new JButton("Stats");
		stats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame a = new JFrame("Stats");
				a.setLayout(new GridBagLayout());
				
				JPanel p = FileHelper.getStats();
				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(6,6,6,6);
				a.add(p, c);
				
				a.pack();
				a.revalidate();
				a.repaint();
				a.setVisible(true);
				
				a.setLocationRelativeTo(null);
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
		
		////////////////////////////////////////////////// 
		////Width x Height
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
		
		wxh.gridy++;
		widthXHeight.add(new JLabel("Grid Count"), wxh);
		wxh.fill = GridBagConstraints.HORIZONTAL;		
		
		//textboxes
		wxh.gridx++;
		wxh.gridy = 0;
		pixelWidth = new JTextField("null*");
		widthXHeight.add(pixelWidth, wxh);
		
		wxh.gridy++;
		boardWidth = new JTextField("null*");
		widthXHeight.add(boardWidth, wxh);
		
		wxh.gridy++;
		gridXCount = new JTextField("null*");
		widthXHeight.add(gridXCount, wxh);

		wxh.gridx++;
		wxh.gridy = 0;
		widthXHeight.add(new JLabel("x"), wxh);
		wxh.gridy++;
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
		
		wxh.gridy++;
		gridYCount = new JTextField("null*");
		widthXHeight.add(gridYCount, wxh);
		
		//////////////////////////////////////
		//Settings
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		GridBagConstraints set = new GridBagConstraints();
		set.insets = new Insets(3, 3, 3, 3);
		

		
		//name field
		set.gridwidth = 1;
		set.gridx = 0;
		set.gridy = 0;
		set.gridwidth = 1;
		settingsPanel.add(new JLabel("Name:"), set);
		
		set.gridx++;
		nameField = new JTextField("null*");
		settingsPanel.add(nameField, set);
		
		//inertia field
		set.gridx = 0;
		set.gridy++;
		settingsPanel.add(new JLabel("Player Inertia"), set);
		
		set.gridx++;
		inertiaField = new JSpinner(new SpinnerNumberModel(4, 0, 10, 1));
		settingsPanel.add(inertiaField, set);
		
		//stats button TODO formatting
		set.gridwidth = 2;
		set.gridx = 0;
		set.gridy++;
		settingsPanel.add(stats, set);

		//TODO think about spacing:
//		set.gridheight = 3;
//		set.gridx = 2;
//		set.gridy = 0;
//		JSeparator x = new JSeparator(SwingConstants.VERTICAL);
//        x.setPreferredSize(new Dimension(3, 100));
//        settingsPanel.add(x, set);
//		set.gridheight = 1;
		
		//checkboxes
		set.gridwidth = 1;
		set.gridx = 4;
		set.gridy = 0;
		particles = new JCheckBox("Particles");
		settingsPanel.add(particles, set);
		
		set.gridx++;
		particleCount = new JSpinner(new SpinnerNumberModel(0, 0, 100, 10));
		settingsPanel.add(particleCount);
		set.gridx++;
		settingsPanel.add(new JLabel("%"));
		
		set.gridx = 4;
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
		gbLayout.insets = new Insets(8,8,8,8);
		
		JPanel headPanel = new JPanel();
		headPanel.setLayout(new GridBagLayout());
		
		JTextArea t = new JTextArea("Welcome to NotGridWars2\nArrow keys or WASD to move, "
				+ "right/left mouse button do things.\nEsc is pause, 'q' is quit while paused.");
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
		
		gbLayout.gridx = 0;
		gbLayout.gridy++;
		menuPanel.add(widthXHeight, gbLayout);

		gbLayout.gridwidth = 2;
		gbLayout.gridy++;
		menuPanel.add(settingsPanel, gbLayout);

		gbLayout.gridx = 1;
		gbLayout.gridy = 1;
		gbLayout.gridwidth = 1;
		gbLayout.gridheight = 2;
		//the line is applies to is down below the switch case stuff
		//scoreboard is below
		
		//////////////////////////////////////////////////
//		GameSettings fileSettings = FileHelper.readSettings();
		GameSettings fileSettings = new GameSettings();
		
		nameField.setText(fileSettings.name);
		inertiaField.setValue(fileSettings.inertia);
		
		Engine.Difficulty diff = fileSettings.diff;
		switch (diff) {
		case easy:
			buttonEasy.setSelected(true);
			break;
		case medium:
			buttonMed.setSelected(true);
			break;
		case hard:
			buttonHard.setSelected(true);
			break;
		default:
			System.err.println("NOT VALID DIFFICULTY");
		}
		leaderBoard = FileHelper.getLeaderBoard(diff);
		menuPanel.add(leaderBoard, gbLayout); //this should be up above
		
		particles.setSelected(fileSettings.ifParticles);
		antialiasing.setSelected(fileSettings.ifAliasing);
		sound.setSelected(fileSettings.ifSound);
		
		particleCount.setValue(fileSettings.particlePercent);
		
		pixelWidth.setText(""+fileSettings.pixelWidth);
		pixelHeight.setText(""+fileSettings.pixelHeight);
		
		boardWidth.setText(""+fileSettings.boardWidth);
		boardHeight.setText(""+fileSettings.boardHeight);
		
		gridXCount.setText(""+fileSettings.gridXCount);
		gridYCount.setText(""+fileSettings.gridYCount);
	}
	

    //rewrite the leaderboard to represent the current difficulty set
    @Override
	public void actionPerformed(ActionEvent evt) {
    	menuPanel.remove(leaderBoard);
    	
    	String command = evt.getActionCommand();
    	leaderBoard = FileHelper.getLeaderBoard(Engine.Difficulty.valueOf(command));
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
	
	public boolean hasValidGridCounts() {
		if (gridXCount.getText().matches("[0-9]+") && gridYCount.getText().matches("[0-9]+")) {
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

	public int getGridXCount() {
		return Integer.parseInt(gridXCount.getText());
	}
	public int getGridYCount() {
		return Integer.parseInt(gridYCount.getText());
	}
	
	public GameSettings getSettings() {
		//should do maths on the size of the screen
		GameSettings set = new GameSettings(getPixelWidth(), getPixelHeight(), getBoardWidth(), getBoardHeight(), 8);
		set.ifAliasing = antialiasing.isSelected();
		set.ifSound = sound.isSelected();
		set.ifParticles = particles.isSelected();
		set.name = nameField.getText();
		set.inertia = (int)inertiaField.getValue();
		
		for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
            	set.diff = Engine.Difficulty.valueOf(button.getActionCommand());
            }
        }
		
		set.gridXCount = getGridXCount();
		set.gridYCount = getGridYCount();
		
		set.particlePercent = (int)particleCount.getValue();
		return set;
	}
}
