package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;

import org.math.plot.Plot2DPanel;
import com.Com;
import com.observers.ComObserver;

import bot.event.AbstractEvent;
import bot.event.factories.AEFDestruirUnidad;
import bwapi.Unit;
import utils.DebugEnum;

import javax.swing.JToggleButton;

/**
 * Class with the panels of the GUI.
 * 
 * @author Alberto Casas Ortiz.
 * @author Ra�l Mart�n Guada�o
 * @author Miguel Ascanio G�mez.
 */
public class ExecPanel extends JPanel implements ComObserver {

	private static final long serialVersionUID = -624314441968368833L;
	private long debugMask;

	private Com com;

	private boolean b = true;
	private int frameSpeed;

	// TABS //
	private JTabbedPane topTabbedPanel;

	private TabConsole tabConsole;
	private TabGraph tabGraphActions;
	private TabGraph tabGraphKills;
	private TabDanger tabDanger;

	private JScrollPane qTabScroll;
	private JPanel qPanel;

	/////////////////
	// CONSTRUCTOR //
	/////////////////

	/**
	 * Constructor of the class Execpanel.
	 * 
	 * @param com
	 *            = Comunication.
	 */
	public ExecPanel(Com com) {
		this.com = com;
		this.com.addObserver(this);
		this.debugMask = 0;

		this.setLayout(new BorderLayout());

		this.topTabbedPanel = new JTabbedPane();
		this.topTabbedPanel.setSize(getWidth(), getHeight());
		this.add(this.topTabbedPanel, BorderLayout.CENTER);

		this.tabConsole = new TabConsole();
		this.topTabbedPanel.add("Console", tabConsole);


		this.tabGraphActions = new TabGraph("Episodes", "Movs");
		this.topTabbedPanel.add("Movs", tabGraphActions);

		this.tabGraphKills = new TabGraph("Episodes", "Kills");
		this.topTabbedPanel.add("Kills", tabGraphKills);
		
		this.tabDanger = new TabDanger();
		this.topTabbedPanel.add("Danger", tabDanger);

		// TODO
		qPanel = new JPanel(new FlowLayout());
		this.qTabScroll = new JScrollPane(qPanel);
		this.topTabbedPanel.add("QTable", qTabScroll);
	}

	//////////////
	// PARTIALS //
	//////////////

	private class TabConsole extends JPanel {

		private static final long serialVersionUID = 5295002384363956476L;
		/** Labels of parameter. */
		private JLabel l_alpha, l_gamma, l_epsilon;
		/** Text field of parameter. */
		private JTextField t_alpha, t_gamma, t_epsilon;

		private class PanelButtons extends JPanel {

			private static final long serialVersionUID = -2630532443748193074L;

			/** Run button. */
			private JButton run;
			/** Shut starcraft button. */
			private JButton btnShutsc;
			/** Button for disable gui. */
			private JToggleButton tglbtnGui;

			public PanelButtons() {
				super(new GridBagLayout());

				this.run = new JButton("Run");
				this.run.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						double alpha, gamma, epsilon;

						try {
							alpha = Double.parseDouble(t_alpha.getText());
							if (alpha < 0 || alpha >= 1)
								throw new NumberFormatException();
							gamma = Double.parseDouble(t_gamma.getText());
							if (gamma < 0 || gamma >= 1)
								throw new NumberFormatException();
							epsilon = Double.parseDouble(t_epsilon.getText());
							if (epsilon < 0 || epsilon >= 1)
								throw new NumberFormatException();
							com.configureParams(alpha, gamma, epsilon);
							com.configureBot(b, frameSpeed);

							tglbtnGui.setEnabled(true);
							run.setEnabled(false);
							new Thread(com).start();
						} catch (NumberFormatException e1) {
							t_alpha.setText(Double.toString(qLearning.Const.ALPHA));
							t_gamma.setText(Double.toString(qLearning.Const.GAMMA));
							t_epsilon.setText(Double.toString(qLearning.Const.EPSLLON_EGREEDY));
						}
					}
				});

				this.tglbtnGui = new JToggleButton("GUI");
				this.tglbtnGui.setEnabled(false);
				this.tglbtnGui.setSelected(true);
				this.tglbtnGui.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (com.bot != null) {
							com.bot.guiEnabled = !b;
							b = !b;
							tglbtnGui.setSelected(b);
						}
					}
				});

				// TODO
				this.btnShutsc = new JButton("ShutSc");
				this.btnShutsc.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						com.shutSc();
					}
				});

				///////////////////////////////////////////
				// LOCATE ELEMENTS //////////////////////
				///////////////////////////////////////////

				GridBagConstraints c = new GridBagConstraints();

				// Add buttons of the gui.
				c.anchor = GridBagConstraints.CENTER;
				c.insets = new Insets(5, 5, 5, 5);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				this.add(run, c);
				c.gridx = 1;
				c.gridy = 0;
				this.add(tglbtnGui, c);
				c.gridx = 2;
				c.gridy = 0;
				this.add(btnShutsc, c);

			}

		}

		private PanelButtons panelButtons;

		/** Label for frames per second. */
		private JLabel lblFps;

		/** Label for speed. */
		private JLabel lblSpeed;
		/** Text field for speed. */
		private JTextField textFieldSpeed;

		private JLabel lblTxtKills, lblTxtDeaths, lblKills, lblDeaths;
		private int kills = 0, deaths = 0;

		/** Scroll panel for the text console. */
		private JScrollPane scrollConsole;
		/** Text console. */
		private JTextArea textConsole;

		private TabConsole() {
			super(new GridBagLayout());

			this.panelButtons = new PanelButtons();

			this.l_alpha = new JLabel("Alpha: ", SwingConstants.RIGHT);
			this.t_alpha = new JTextField();
			this.t_alpha.setText(Double.toString(qLearning.Const.ALPHA));
			this.t_alpha.setColumns(5);

			this.l_gamma = new JLabel("Gamma: ", SwingConstants.RIGHT);
			this.t_gamma = new JTextField();
			this.t_gamma.setText(Double.toString(qLearning.Const.GAMMA));
			this.t_gamma.setColumns(5);

			this.l_epsilon = new JLabel("Epsilon: ", SwingConstants.RIGHT);
			this.t_epsilon = new JTextField();
			this.t_epsilon.setText(Double.toString(qLearning.Const.EPSLLON_EGREEDY));
			this.t_epsilon.setColumns(5);

			lblSpeed = new JLabel("Speed:", SwingConstants.RIGHT);

			textFieldSpeed = new JTextField();
			textFieldSpeed.setText("0");
			textFieldSpeed.setColumns(5);
			textFieldSpeed.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					try {
						int n = Integer.parseInt(textFieldSpeed.getText());
						if (n < 0)
							throw new NumberFormatException();
						frameSpeed = n;
						try {
							com.bot.frameSpeed = Integer.parseInt(textFieldSpeed.getText());
						} catch (NullPointerException e1) {
						}
					} catch (NumberFormatException e1) {
						textFieldSpeed.setText(com.bot.frameSpeed + "");
					}
				}

				@Override
				public void focusGained(FocusEvent e) {
				}
			});

			lblFps = new JLabel("FPS:", SwingConstants.CENTER);

			this.lblTxtDeaths = new JLabel("Muertes: ");
			this.lblDeaths = new JLabel();
			this.lblTxtKills = new JLabel("Asesinatos: ");
			this.lblKills = new JLabel();

			this.textConsole = new JTextArea();
			this.textConsole.setEditable(false);
			this.scrollConsole = new JScrollPane(textConsole);
			this.scrollConsole.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			DefaultCaret caret = (DefaultCaret) textConsole.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

			locateElements();
		}

		private void locateElements() {
			GridBagConstraints c = new GridBagConstraints();

			// Add parameters alpha, epsillon and lambda.
			c.gridheight = 1;
			c.gridwidth = 1;

			c.weightx = 1;
			
			c.gridx = 0;
			c.gridy = 0;
			add(l_alpha, c);
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 1;
			add(t_alpha, c);

			c.gridx = 2;
			c.gridy = 0;
			add(l_gamma, c);
			c.gridx = 3;
			c.gridy = 0;
			add(t_gamma, c);

			c.gridx = 4;
			c.gridy = 0;
			add(l_epsilon, c);
			c.gridx = 5;
			c.gridy = 0;
			add(t_epsilon, c);

			// Add the information of speed and fps.
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			add(lblFps, c);

			c.gridx = 1;
			c.gridy = 1;
			add(lblSpeed, c);
			c.gridx = 2;
			c.gridy = 1;
			add(textFieldSpeed, c);

			// Add the panel with the buttons.
			c.gridwidth = 6;
			c.gridx = 0;
			c.gridy = 2;
			add(this.panelButtons, c);

			// Add the counters of kills and deaths
			c.gridwidth = 2;
			c.gridx = 0;
			c.gridy = 3;
			add(this.lblTxtDeaths, c);
			c.gridx = 1;
			c.gridy = 3;
			add(this.lblDeaths, c);
			c.gridx = 0;
			c.gridy = 4;
			add(this.lblTxtKills, c);
			c.gridx = 1;
			c.gridy = 4;
			add(this.lblKills, c);

			// Add the scroll panel with the text view of the console.
			c.gridwidth = 6;
			c.gridx = 0;
			c.gridy = 5;

			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			add(this.scrollConsole, c);
		}

		public void onEndIteration(int i, int movimientos, int nume) {
			// TODO Auto-generated method stub
			this.textConsole.append("movimientos: " + movimientos + " nume: " + nume + " episodio " + i + "\n");
		}

		public void onEvent(AbstractEvent abstractEvent) {
			switch (abstractEvent.getCode()) {
			case AEFDestruirUnidad.CODE_KILL:
				tabGraphKills.update(1);
				this.kills++;
				this.lblKills.setText(Integer.toString(kills));
				break;
			case AEFDestruirUnidad.CODE_KILLED:
				tabGraphKills.update(0);
				this.deaths++;
				this.lblDeaths.setText(Integer.toString(deaths));
				break;
			default:
				break;
			}
		}

		public void onSendMessage(String s) {
			this.textConsole.append(s + "\n");
		}

		public void onFpsAverageAnnoucement(double fps) {
			this.lblFps.setText("FPS: " + Double.toString(fps));
		}

	}
	
	/////////////////////

	private class TabGraph extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1290634325178686829L;
		private Plot2DPanel graphic;

		private double[] xx;
		private double[] yy;

		private int length;
		private int pos;

		private TabGraph(String sx, String sy) {
			this.graphic = new Plot2DPanel();

			this.length = 512;
			this.pos = 0;
			this.xx = new double[length];
			this.yy = new double[length];

			this.setLayout(new GridLayout(1, 1));
			this.graphic.setAxisLabels(sx, sy);
			this.add(this.graphic);
		}

		private void update(double x, double y) {
			this.resize();

			xx[pos] = x;
			yy[pos] = y;
			pos++;

			if (pos % 10 == 0) {
				this.graphic.removeAllPlots();
				this.graphic.addLinePlot("Movements per iteration.", Color.BLUE, Arrays.copyOf(xx, pos),
						Arrays.copyOf(yy, pos));
				this.graphic.repaint();
				this.repaint();
			}
		}

		/**
		 * Resize the arrays of movements and iterations.
		 */
		private void resize() {
			if (pos < length)
				return;

			double[] xa, ya;
			xa = new double[length * 2];
			ya = new double[length * 2];

			System.arraycopy(xx, 0, xa, 0, length);
			System.arraycopy(yy, 0, ya, 0, length);

			this.length *= 2;
			this.xx = xa;
			this.yy = ya;
		}

		public void update(double y) {
			update(pos, y);
		}

	}

	private class TabDanger extends JPanel {

		private static final long serialVersionUID = -429614650656376442L;

		public void paint(Graphics g){
			super.paint(g); 
			int sizeX = this.getWidth();
			int sizeY = this.getHeight();

			Unit unit = com.ComData.unit;
			if(unit != null){		
				
				Point center = new Point(sizeX/2-unit.getType().sightRange()/2, sizeY/2-unit.getType().sightRange()/2);
			
				g.setColor(new Color(0.0f, 0.0f, 0.1f, 0.5f));
				g.fillOval(center.x, center.y, unit.getType().sightRange(), unit.getType().sightRange());
				g.setColor(Color.BLACK);
				g.drawOval(center.x, center.y, unit.getType().sightRange(), unit.getType().sightRange());

				
				Unit anotherUnit;
				List<Unit> list = unit.getUnitsInRadius(unit.getType().sightRange());

				for(int i = 0; i < list.size(); i++){
					anotherUnit = list.get(i);
					int distX = (unit.getX() - anotherUnit.getX());
					int distY = (unit.getY() - anotherUnit.getY());
					
					Point centerAux = new Point(center.x-distX, center.y-distY);
			
					
					if(anotherUnit.getPlayer().isAlly(unit.getPlayer())){
						g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.5f));
						g.fillOval(centerAux.x, centerAux.y, anotherUnit.getType().sightRange(), anotherUnit.getType().sightRange());
						g.setColor(new Color(0.0f, 1.0f, 0.0f, 1.0f));
						g.drawOval(centerAux.x, centerAux.y, anotherUnit.getType().sightRange(), anotherUnit.getType().sightRange());
					}else{
						g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
						g.fillOval(centerAux.x, centerAux.y, anotherUnit.getType().sightRange(), anotherUnit.getType().sightRange());
						g.setColor(Color.BLACK);
						g.drawOval(centerAux.x, centerAux.y, anotherUnit.getType().sightRange(), anotherUnit.getType().sightRange());
					}
				
				}
			}
		}
	};
	
	/*******************/
	// GETTER / SETTER //
	/////////////////////

	/**
	 * Set a debug mask.
	 * 
	 * @param debugMask
	 *            = New debug mask.
	 */
	public void setDebugMask(long debugMask) {
		this.debugMask = debugMask;
	}

	/**
	 * Get debug mask.
	 * 
	 * @return The debug mask.
	 */
	public long getDebugMask() {
		return debugMask;
	}

	/////////////////////
	// OVERRIDE METHOD //
	/////////////////////

	/**
	 * Listener on end of and iteration.
	 * 
	 * @param i
	 *            = Iteration number.
	 * @param movimientos
	 *            = Number of movements.
	 * @param nume
	 *            = Number of random movements.
	 */
	@Override
	public void onEndIteration(int i, int movimientos, int nume) {
		this.tabConsole.onEndIteration(i, movimientos, nume);
		// Paint the plot.
		this.tabGraphActions.update(i, movimientos);
	}

	@Override
	public void onEvent(AbstractEvent abstractEvent) {
		this.tabConsole.onEvent(abstractEvent);
	}

	/**
	 * Listener on sending a message.
	 * 
	 * @param s
	 *            = Message to send.
	 */
	@Override
	public void onSendMessage(String s) {
		this.tabConsole.onSendMessage(s);
	}

	/**
	 * Listener on error.
	 * 
	 * @param s
	 *            = Message of the error.
	 * @param fatal
	 *            = True if the error is fatal.
	 */
	@Override
	public void onError(String s, boolean fatal) {
		JOptionPane.showMessageDialog(this.getParent(), s, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Listener on FPS average announcement.
	 * 
	 * @param fps
	 *            = Frames per second.
	 */
	@Override
	public void onFpsAverageAnnouncement(double fps) {
		this.tabConsole.onFpsAverageAnnoucement(fps);
		this.tabDanger.repaint();
	}

	/**
	 * Listener on debug message.
	 * 
	 * @param s
	 *            = Message.
	 * @param level
	 *            = Level of the message.
	 */
	@Override
	public void onDebugMessage(String s, DebugEnum level) {
		if ((debugMask & (1 << level.ordinal())) != 0)
			onSendMessage(s);
	}

	@Override
	public void onFullQUpdate(JPanel panel) {
		qPanel.removeAll();
		qPanel.add(panel);
		qPanel.repaint();
	}

	///////////////////
	// UNUSED METHOD //
	///////////////////

	/**
	 * Listener on end train.
	 */
	@Override
	public void onEndTrain() {
		// TODO Auto-generated method stub
	}

	/**
	 * Listener on action taken.
	 */
	@Override
	public void onActionTaken() {
		// TODO Auto-generated method stub
	}

	/**
	 * Listener on action fail.
	 */
	@Override
	public void onActionFail() {
		// TODO Auto-generated method stub
	}

}