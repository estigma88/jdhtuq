/*
 *  desktop-network-gui is a example of the peer to peer application with a desktop ui
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		/*SwingUtilities.invokeLater(new Runnable() {
			public void run() {*/
				loadBar.setValue(value);
				labelTitle.setText(message);
			/*}
		});*/
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
