package co.edu.uniquindio.dht;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import co.edu.uniquindio.dht.gui.MainFrame;

//TODO Documentar
public class Main {
	//TODO Documentar
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {

		} catch (ClassNotFoundException e) {

		} catch (InstantiationException e) {

		} catch (IllegalAccessException e) {

		}

		MainFrame mainFrame=new MainFrame();
		mainFrame.setVisible(true);
	}
}
