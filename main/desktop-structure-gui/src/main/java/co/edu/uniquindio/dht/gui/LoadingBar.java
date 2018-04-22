package co.edu.uniquindio.dht.gui;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
//TODO Documentar
@SuppressWarnings("serial")
public class LoadingBar extends JDialog {
	//TODO Documentar
	private JProgressBar loadBar;
	//TODO Documentar
	private int _HEIGHT = 50;
	private int _WIDTH = 200;
	//TODO Documentar
	private int max;
	//TODO Documentar
	private JLabel labelTitle;
	//TODO Documentar
	private JPanel panel;
	//TODO Documentar
	private static LoadingBar loadingBar;
	//TODO Documentar
	private LoadingBar(JFrame jFrame) {
		super(jFrame, true);
		labelTitle = new JLabel();
		loadBar = new JProgressBar();
		panel = new JPanel();

		setLayout(null);
		setUndecorated(true);
		setBackground(Color.BLACK);

		panel.setLayout(null);
		panel.setBounds(0, 0, _WIDTH, _HEIGHT);
		panel.setBorder(BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		add(panel);

		labelTitle.setHorizontalAlignment(JLabel.CENTER);
		labelTitle.setBounds(10, 10, 190, 20);
		panel.add(labelTitle);

		loadBar.setBounds(10, 35, 180, 8);
		panel.add(loadBar);

		setSize(_WIDTH, _HEIGHT);
		setLocationRelativeTo(jFrame);
	}
	//TODO Documentar
	public void setConfiguration(boolean isIndeterminate, int max) {
		loadBar.setIndeterminate(isIndeterminate);

		if (!isIndeterminate) {
			loadBar.setMaximum(max);
			this.max = max;
		}
	}
	//TODO Documentar
	public void setValue(final int value, final String message) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				loadBar.setValue(value);
				labelTitle.setText(message);
			}
		});
	}
	//TODO Documentar
	public void begin() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
	}
	//TODO Documentar
	public void end() {
		setValue(max, "final");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setVisible(false);
			}
		});
	}

	public static LoadingBar getInstance(JFrame jFrame) {
		if (loadingBar == null) {
			loadingBar = new LoadingBar(jFrame);
		}

		return loadingBar;
	}
}
