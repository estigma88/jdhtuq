package co.edu.uniquindio.dht.gui.structure.manager;


import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import co.edu.uniquindio.dht.gui.LoadingBar;
import co.edu.uniquindio.dht.gui.PanelDhash;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.FileResource;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.storage.resource.SerializableResource.ResourceParams;
import co.edu.uniquindio.utils.EscapeChars;

@SuppressWarnings("serial")
public class PanelDhashStructure extends PanelDhash {
	private StructureWindow structureWindow;
	private Controller controller;

	public PanelDhashStructure(JFrame frame) {
		super(PanelDhash.STRUCTURE, frame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonPut) {
			controller.clearGraph();

			int returnVal = fileChooser.showOpenDialog(structureWindow);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Thread thread = new Thread(new Runnable() {
					public void run() {

						LoadingBar loadingBar = LoadingBar.getInstance(frame);

						loadingBar.setConfiguration(true, 100);
						loadingBar.setValue(1, "Doing Put...");
						loadingBar.begin();

						File fichero = fileChooser.getSelectedFile();

						FileResource fileResource=new FileResource(fichero);
						try {
							getDHashNode().put(fileResource);

							controller.setActionColor(true);
						} catch (StorageException e1) {
							loadingBar.end();
							JOptionPane.showMessageDialog(frame, e1
									.getMessage()+" Velo aqui");
							e1.printStackTrace();
						}

						loadingBar.end();
					}
				});
				thread.start();
			}
		} else {
			if (e.getSource() == buttonGet) {
				controller.clearGraph();

				final String a = JOptionPane
						.showInputDialog(
								null,
								"Please write the name of the file that you wish retrieve",
								"Insert a name file",
								JOptionPane.INFORMATION_MESSAGE);

				if (a == null)
					return;

				Thread thread = new Thread(new Runnable() {
					public void run() {

						LoadingBar loadingBar = LoadingBar.getInstance(frame);

						loadingBar.setConfiguration(true, 100);
						loadingBar.setValue(1, "Doing Get...");
						loadingBar.begin();
						try {
							Resource resource=getDHashNode().get(a);
							
							if (resource instanceof FileResource) {
								FileResource fileResource=(FileResource) resource;
								
								Map<String, Object> params=new HashMap<String, Object>();
								params.put(ResourceParams.MANAGER_NAME.name(), getDHashNode().getName()+"/gets/");
								params.put(ResourceParams.PERSIST_TYPE.name(), "get");
								fileResource.persist(params);
							}
							

							controller.setActionColor(false);
						} catch (StorageException e1) {
							loadingBar.end();
							JOptionPane.showMessageDialog(frame, e1
									.getMessage()
									+ "\nPlease try again later", "ERROR",
									JOptionPane.INFORMATION_MESSAGE);
						}

						loadingBar.end();

					}
				});
				thread.start();

			} else {
				if (e.getSource() == buttonOpen) {

					File file=new File("dhash/"+EscapeChars.forHTML(getDHashNode().getName(), true));
					
					Desktop desktop = null;
					// Before more Desktop API is used, first check
					// whether the API is supported by this particular
					// virtual machine (VM) on this particular host.
					if (Desktop.isDesktopSupported()) {
						desktop = Desktop.getDesktop();
					}
			 
					try {
						desktop.open(file);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame,
								"The save path is: " + file.getAbsolutePath()
										+ File.separator
										+ controller.getSelectedNode(),
								"ERROR", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					Thread thread = new Thread(new Runnable() {
						public void run() {

							LoadingBar loadingBar = LoadingBar
									.getInstance(frame);

							loadingBar.setConfiguration(true, 100);
							loadingBar.setValue(1, "Leaving...");
							loadingBar.begin();

							exit();

							loadingBar.end();
						}
					});
					thread.start();
				}
			}
		}
	}

	public void setStructureWindow(StructureWindow structureWindow) {
		this.structureWindow = structureWindow;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}
