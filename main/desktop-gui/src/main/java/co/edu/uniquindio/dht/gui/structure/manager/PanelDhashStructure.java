package co.edu.uniquindio.dht.gui.structure.manager;


import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.dht.gui.LoadingBar;
import co.edu.uniquindio.dht.gui.PanelDhash;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.storage.StorageException;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("serial")
public class PanelDhashStructure extends PanelDhash {
    private StructureWindow structureWindow;
    private Controller controller;
    private String resourceDirectory = "dhash/";

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

                        try (FileInputStream fileInputStream = new FileInputStream(fichero)) {
                            BytesResource fileResource = new BytesResource(fichero.getName(), IOUtils.toByteArray(fileInputStream));

                            getDHashNode().put(fileResource);

                            controller.setActionColor(true);
                        } catch (StorageException e1) {
                            loadingBar.end();
                            JOptionPane.showMessageDialog(frame, e1
                                    .getMessage() + " Velo aqui");
                            e1.printStackTrace();
                        } catch (FileNotFoundException e1) {
                            loadingBar.end();
                            JOptionPane.showMessageDialog(frame, e1
                                    .getMessage() + " Velo aqui");
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            loadingBar.end();
                            JOptionPane.showMessageDialog(frame, e1
                                    .getMessage() + " Velo aqui");
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
                            BytesResource resource = (BytesResource) getDHashNode().get(a);

                            Files.createDirectories(Paths.get(resourceDirectory + getDHashNode().getName() + "/gets/"));
                            Files.copy(new ByteArrayInputStream(resource.getBytes()), Paths.get(resourceDirectory + getDHashNode().getName() + "/gets/" + resource.getId()));

                            controller.setActionColor(false);
                        } catch (StorageException e1) {
                            loadingBar.end();
                            JOptionPane.showMessageDialog(frame, e1
                                            .getMessage()
                                            + "\nPlease try again later", "ERROR",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException e1) {
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

                    File file = new File("dhash/" + getDHashNode().getName());

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
