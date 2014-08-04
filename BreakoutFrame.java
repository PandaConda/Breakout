import java.awt.event.WindowEvent;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.awt.Container;
import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;

/**
 * A window in which to display the game.
 * @author Based on code by Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
 */
public final class BreakoutFrame extends JFrame implements WindowListener, ActionListener {
	/**
	 * frames per second
	 */
	private static final int DEFAULT_FPS = 60;

	/**
	 * Game drawing surface and model
	 */
	private BreakoutPanel thePanel;
	private BreakoutModel theModel;
	/**
	 * To display frames per second
	 */
	private JTextField fpsField;

	/**
	 * To display updates per second
	 */
	private JTextField upsField;

	private JTextField pwrField;
	/**
	 * To format the FPS/UPS
	 */
	private DecimalFormat twoDP = new DecimalFormat("0.##"); // 2 dp

	/**
	 * Adds an instance of BreakoutPanel, and two text fields (for UPS, FPS)
	 *  to the content pane.
	 */
	public BreakoutFrame(long period) {
		super("Breakout!");

		// Create and add the BreakoutPanel.
		Container c = getContentPane();
		thePanel = new BreakoutPanel(this, period);
		c.add(thePanel, "Center");

		// A JPanel to hold the FPS/UPS readout
		JPanel ctrls = new JPanel();
		ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

		// Display the FPS
		fpsField = new JTextField("Your Score: 0");
		fpsField.setEditable(false);
		ctrls.add(fpsField);
		
		pwrField = new JTextField("Power-Up: None");
		pwrField.setEditable(false);
		ctrls.add(pwrField);

		// Display the UPS
		upsField = new JTextField("Lives Left: 3");
		upsField.setEditable(false);
		ctrls.add(upsField);

		// Add the FPS/UPS readout underneath the BreakoutPanel
		c.add(ctrls, "South");

		// Detect the standard window controls
		addWindowListener(this);

		//create the menu-bar
		JMenuBar menubar = new JMenuBar();
		
		//the level menu contains a list of level which will be loaded when clicked on
		JMenu levelMenu = new JMenu("Levels");
		
		ButtonGroup levelList = new ButtonGroup();
		
		JRadioButtonMenuItem emptyRBMenuItem = new JRadioButtonMenuItem("Empty");
		emptyRBMenuItem.setSelected(true);
		levelList.add(emptyRBMenuItem);
		levelMenu.add(emptyRBMenuItem);
		emptyRBMenuItem.addActionListener(this);
		
		JRadioButtonMenuItem classicRBMenuItem = new JRadioButtonMenuItem("Classic");
		levelList.add(classicRBMenuItem);
		levelMenu.add(classicRBMenuItem);
		classicRBMenuItem.addActionListener(this);
		
		JRadioButtonMenuItem invinciblockRBMenuItem = new JRadioButtonMenuItem("Invinciblock");
		levelList.add(invinciblockRBMenuItem);
		levelMenu.add(invinciblockRBMenuItem);
		invinciblockRBMenuItem.addActionListener(this);
		
		menubar.add(levelMenu);
		
		//the theme menu contains a list of themes (red, green and blue) which will determines the colors the game will render in
		JMenu themeMenu = new JMenu("Themes");
		
		ButtonGroup themeList = new ButtonGroup();
		
		JRadioButtonMenuItem RedRBMenuItem = new JRadioButtonMenuItem("Red");
		RedRBMenuItem.setSelected(true);
		themeList.add(RedRBMenuItem);
		themeMenu.add(RedRBMenuItem);
		RedRBMenuItem.addActionListener(this);
		
		JRadioButtonMenuItem GreenRBMenuItem = new JRadioButtonMenuItem("Green");
		themeList.add(GreenRBMenuItem);
		themeMenu.add(GreenRBMenuItem);
		GreenRBMenuItem.addActionListener(this);
		
		JRadioButtonMenuItem BlueRBMenuItem = new JRadioButtonMenuItem("Blue");
		themeList.add(BlueRBMenuItem);
		themeMenu.add(BlueRBMenuItem);
		BlueRBMenuItem.addActionListener(this);
		
		menubar.add(themeMenu);
		
		//the power-up menu contains all the powerups in the game (except hp-up) and are included for debugging purposes
		JMenu powMenu = new JMenu("Powerups");
		
		JCheckBoxMenuItem enablePowerupsCBButton = new JCheckBoxMenuItem("Enable Power-Ups");
		enablePowerupsCBButton.setSelected(true);
		enablePowerupsCBButton.addActionListener(this);
		powMenu.add(enablePowerupsCBButton);
		
		menubar.add(powMenu);
		
		JMenu ballMenu = new JMenu("Ball Power-Ups");
		
		JMenu ballSizeMenu = new JMenu("Ball Size");
		
		ButtonGroup ballSizeList = new ButtonGroup();
		
		JRadioButtonMenuItem smallBallRBButton = new JRadioButtonMenuItem("Small Balls");
		smallBallRBButton.setSelected(true);
		ballSizeMenu.add(smallBallRBButton);
		ballSizeList.add(smallBallRBButton);
		smallBallRBButton.addActionListener(this);
		
		JRadioButtonMenuItem mediumBallRBButton = new JRadioButtonMenuItem("Medium Balls");
		ballSizeMenu.add(mediumBallRBButton);
		ballSizeList.add(mediumBallRBButton);
		mediumBallRBButton.addActionListener(this);
		
		JRadioButtonMenuItem largeBallRBButton = new JRadioButtonMenuItem("Large Balls");
		ballSizeMenu.add(largeBallRBButton);
		ballSizeList.add(largeBallRBButton);
		largeBallRBButton.addActionListener(this);
		
		ballMenu.add(ballSizeMenu);
		
		JCheckBoxMenuItem indestuctoBallCBButton = new JCheckBoxMenuItem("Indestructoball");
		indestuctoBallCBButton.addActionListener(this);
		
		ballMenu.add(indestuctoBallCBButton);
		
		JCheckBoxMenuItem torusModeCBButton = new JCheckBoxMenuItem("Torus Mode");
		torusModeCBButton.addActionListener(this);
		
		ballMenu.add(torusModeCBButton);
		
		powMenu.add(ballMenu);
				
		JMenu paddleMenu = new JMenu("Paddle Power-Ups");
		
		JMenu paddleSizeMenu = new JMenu("Paddle Size");
		
		ButtonGroup paddleSizeList = new ButtonGroup();
		
		
		JRadioButtonMenuItem smallPaddleRBButton = new JRadioButtonMenuItem("Small Paddle");
		paddleSizeMenu.add(smallPaddleRBButton);
		paddleSizeList.add(smallPaddleRBButton);
		smallPaddleRBButton.addActionListener(this);
		
		JRadioButtonMenuItem mediumPaddleRBButton = new JRadioButtonMenuItem("Medium Paddle");
		mediumPaddleRBButton.setSelected(true);
		paddleSizeMenu.add(mediumPaddleRBButton);
		paddleSizeList.add(mediumPaddleRBButton);
		mediumPaddleRBButton.addActionListener(this);
		
		JRadioButtonMenuItem largePaddleRBButton = new JRadioButtonMenuItem("Large Paddle");
		paddleSizeMenu.add(largePaddleRBButton);
		paddleSizeList.add(largePaddleRBButton);
		largePaddleRBButton.addActionListener(this);
		
		paddleMenu.add(paddleSizeMenu);
		
		JCheckBoxMenuItem verticalMovementCBButton = new JCheckBoxMenuItem("Vertical Mode");
		verticalMovementCBButton.addActionListener(this);
		
		paddleMenu.add(verticalMovementCBButton);
		powMenu.add(paddleMenu);
		
		JMenu spawnMenu = new JMenu("Spawn Power-Ups");
		ButtonGroup spawnList = new ButtonGroup();

		JRadioButtonMenuItem noSpawnModRBButton = new JRadioButtonMenuItem("Normal Spawn");
		noSpawnModRBButton.setSelected(true);
		spawnMenu.add(noSpawnModRBButton);
		spawnList.add(noSpawnModRBButton);
		noSpawnModRBButton.addActionListener(this);
		
		JRadioButtonMenuItem multiBallSpawnModRBButton = new JRadioButtonMenuItem("Multiball Spawn");
		spawnMenu.add(multiBallSpawnModRBButton);
		spawnList.add(multiBallSpawnModRBButton);
		multiBallSpawnModRBButton.addActionListener(this);
		
		JRadioButtonMenuItem SpawnageSpawnModRBButton = new JRadioButtonMenuItem("Spawnage Spawn");
		SpawnageSpawnModRBButton.setSelected(true);
		spawnMenu.add(SpawnageSpawnModRBButton);
		spawnList.add(SpawnageSpawnModRBButton);
		SpawnageSpawnModRBButton.addActionListener(this);
		
		powMenu.add(spawnMenu);
		
		JMenu difMenu = new JMenu("Difficulty");
		
		ButtonGroup difList = new ButtonGroup();
		
		JRadioButtonMenuItem veryEasyDifRBButton = new JRadioButtonMenuItem("Very Easy");
		difMenu.add(veryEasyDifRBButton);
		difList.add(veryEasyDifRBButton);
		veryEasyDifRBButton.addActionListener(this);
		
		JRadioButtonMenuItem easyDifRBButton = new JRadioButtonMenuItem("Easy");
		difMenu.add(easyDifRBButton);
		difList.add(easyDifRBButton);
		easyDifRBButton.addActionListener(this);
		
		JRadioButtonMenuItem normalDifRBButton = new JRadioButtonMenuItem("Normal");
		normalDifRBButton.setSelected(true);
		difMenu.add(normalDifRBButton);
		difList.add(normalDifRBButton);
		normalDifRBButton.addActionListener(this);
		
		JRadioButtonMenuItem hardDifRBButton = new JRadioButtonMenuItem("Hard");
		difMenu.add(hardDifRBButton);
		difList.add(hardDifRBButton);
		hardDifRBButton.addActionListener(this);
		
		JRadioButtonMenuItem veryHardDifRBButton = new JRadioButtonMenuItem("Very Hard");
		difMenu.add(veryHardDifRBButton);
		difList.add(veryHardDifRBButton);
		veryHardDifRBButton.addActionListener(this);
		
		menubar.add(difMenu);
		super.setJMenuBar(menubar);
		// Set frame to just the right size to hold the panel and text fields
		pack();
//		setResizable(false);

		// Let's see it
		setVisible(true);
	}

	/**
	 * Update the Frames Per Second text field
	 */
	public void setFPS(String text) {
		fpsField.setText(text);
	}
	
	public void setPWR(String text) {
		pwrField.setText(text);
	}

	/**
	 * Update the Updates Per Second text field
	 */
	public void setUPS(String text) {
		upsField.setText(text);
	}

	public void actionPerformed(ActionEvent e) {
		//respond to menu selections
		if (e.getActionCommand().equals("Red") || e.getActionCommand().equals("Blue") || e.getActionCommand().equals("Green")) { //change theme
			theModel.setColor(e.getActionCommand().toLowerCase());
		//change ball size
		} else if (e.getActionCommand().equals("Small Balls")) {
			theModel.setBallSize(15);
		}  else if (e.getActionCommand().equals("Medium Balls")) {
			theModel.setBallSize(30);
		}  else if (e.getActionCommand().equals("Large Balls")) {
			theModel.setBallSize(45);
		//make ball indestructoball (see what I did there?)
		} else if (e.getActionCommand().equals("Indestructoball")) {
			if (((AbstractButton) e.getSource()).getModel().isSelected()) {
				theModel.setIndestructoBall(true);
			} else {
				theModel.setIndestructoBall(false);
			}
		//disable power-ups in a normal game
		} else if (e.getActionCommand().equals("Enable Power-Ups")) {
			if (((AbstractButton) e.getSource()).getModel().isSelected()) {
				theModel.setPowerUps(true);
			} else {
				theModel.setPowerUps(false);
			}
		//ball doesn't bounce off sides!
		} else if (e.getActionCommand().equals("Torus Mode")) {
			if (((AbstractButton) e.getSource()).getModel().isSelected()) {
				theModel.setTorusMode(true);
			} else {
				theModel.setTorusMode(false);
			}
		//change paddle size
		} else if (e.getActionCommand().equals("Small Paddle")) {
			theModel.setPaddleWidth(40);
		} else if (e.getActionCommand().equals("Medium Paddle")) {
			theModel.setPaddleWidth(80);
		} else if (e.getActionCommand().equals("Large Paddle")) {
			theModel.setPaddleWidth(120);
		//set spawn mode to normal, multi or spawnage!
		} else if (e.getActionCommand().equals("Normal Spawn")) {
			theModel.setMultiBall(false);
			theModel.setSpawnageMode(false);
		} else if (e.getActionCommand().equals("Multiball Spawn")) {
			theModel.setMultiBall(true);
			theModel.setSpawnageMode(false);
		} else if (e.getActionCommand().equals("Spawnage Spawn")) {
			theModel.setMultiBall(false);
			theModel.setSpawnageMode(true);
		//change difficulty
		} else if (e.getActionCommand().equals("Very Easy")) {
			theModel.setDifficultyFactor(16);
		} else if (e.getActionCommand().equals("Easy")) {
			theModel.setDifficultyFactor(8);
		} else if (e.getActionCommand().equals("Normal")) {
			theModel.setDifficultyFactor(4);
		} else if (e.getActionCommand().equals("Hard")) {
			theModel.setDifficultyFactor(2);
		} else if (e.getActionCommand().equals("Very Hard")) {
			theModel.setDifficultyFactor(1);
		//choose level
		} else if (e.getActionCommand().equals("Empty")) {
			theModel.loadLevel("empty");
		} else if (e.getActionCommand().equals("Classic")) {
			theModel.loadLevel("classic");
		} else if (e.getActionCommand().equals("Invinciblock")) {
			theModel.loadLevel("invinciblock");
		//enables vertical paddle movement
		} else if (e.getActionCommand().equals("Vertical Mode")) {
			if (((AbstractButton) e.getSource()).getModel().isSelected()) {
				theModel.setVerticalMode(true);
			} else {
				theModel.setVerticalMode(false);
				theModel.movePaddleToBottom();
			}
		}
	}
	/**
	 * Resume game, from a paused state, when the window is activated
	 */
	public void windowActivated(WindowEvent e) {
		theModel.resumeGame();
	}

	/**
	 * Pause game if the window is deactivated
	 */
	public void windowDeactivated(WindowEvent e) {
		theModel.pauseGame();
	}

	/**
	 * Resume game when the window is deiconified
	 */
	public void windowDeiconified(WindowEvent e) {
		theModel.resumeGame();
	}

	/**
	 *  Pause game if the window is iconified
	 */
	public void windowIconified(WindowEvent e) {
		theModel.pauseGame();
	}

	/**
	 * Stop the game (quit) if the window is closed.
	 */
	public void windowClosing(WindowEvent e) {
		thePanel.stopGame();
	}

	/**
	 * Don't expect to deal with this
	 */
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * Don't expect to deal with this
	 */
	public void windowOpened(WindowEvent e) {
	}

	public void setBreakoutModel(BreakoutModel theModel) {
		this.theModel = theModel;
	}
	/**
	 * Takes in a requested FPS, calculates the update period
	 * in ns, then creates a new BreakoutFrame instance
	 */
	public static void main(String args[]) {
		System.setProperty("apple.laf.useScreenMenuBar", "true"); // http://stackoverflow.com/questions/3154638/setting-java-swing-application-name-on-mac
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Breakout!"); // http://stackoverflow.com/questions/3154638/setting-java-swing-application-name-on-mac
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);
		new BreakoutFrame(1000000000 / fps);
	}

}