package co.edu.uniquindio.dht.gui;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import co.edu.uniquindio.dht.gui.network.NetworkWindow;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
//TODO Documentar
public class MainFrame extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//TODO Documentar
	private JButton buttonStructure;
	//TODO Documentar
	private JButton buttonNetwork;
	//TODO Documentar
	public MainFrame() {
		setLayout(new GridLayout(1,2));
		setSize(200,100);
		setTitle("Run mode");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		buttonNetwork=new JButton("Network");
		buttonStructure=new JButton("Structure");
		
		add(buttonStructure);
		add(buttonNetwork);
		buttonNetwork.addActionListener(this);
		buttonStructure.addActionListener(this);
	}
	//TODO Documentar
	@Override
	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		if (e.getSource()==buttonStructure) {

			Thread thread = new Thread(new Runnable() {
				public void run() {
					Controller controller = new Controller();
					StructureWindow structureWindow = new StructureWindow();
					controller.setStructureWindow(structureWindow);
					structureWindow.setController(controller);
					structureWindow.setVisible(true);
				}
			});
			thread.start();
		} else {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					NetworkWindow networkWindow = new NetworkWindow();
					networkWindow.setVisible(true);
				}
			});
			thread.start();
		}
	}
	
}
