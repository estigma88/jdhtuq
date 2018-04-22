package co.edu.uniquindio.dht.gui.structure.manager;


import co.edu.uniquindio.dht.gui.LoadingBar;
import co.edu.uniquindio.dht.gui.PanelDhash;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.dht.gui.structure.task.storageservice.GetTask;
import co.edu.uniquindio.dht.gui.structure.task.storageservice.PutTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class PanelDhashStructure extends PanelDhash implements PropertyChangeListener {
    private StructureWindow structureWindow;
    private Controller controller;

    public PanelDhashStructure(JFrame frame, String resourceDirectory) {
        super(PanelDhash.STRUCTURE, frame, resourceDirectory);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonPut) {
            controller.clearGraph();

            int returnVal = fileChooser.showOpenDialog(structureWindow);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                controller.setActionColor(true);

                LoadingBar loadingBar = LoadingBar.getInstance(frame);

                loadingBar.setConfiguration(true, 100);
                loadingBar.setValue(1, "Doing Put...");
                loadingBar.begin();

                File file = fileChooser.getSelectedFile();

                PutTask putTask = new PutTask(structureWindow, getDHashNode(), file);
                putTask.addPropertyChangeListener(this);
                putTask.execute();
            }
        } else {
            if (e.getSource() == buttonGet) {
                controller.clearGraph();

                final String resourceId = JOptionPane
                        .showInputDialog(
                                null,
                                "Please write the name of the file that you wish retrieve",
                                "Insert a name file",
                                JOptionPane.INFORMATION_MESSAGE);

                if (resourceId == null)
                    return;

                controller.setActionColor(false);

                LoadingBar loadingBar = LoadingBar.getInstance(frame);

                loadingBar.setConfiguration(true, 100);
                loadingBar.setValue(1, "Doing Get...");
                loadingBar.begin();

                GetTask getTask = new GetTask(structureWindow, getDHashNode(), resourceId, resourceDirectory);
                getTask.addPropertyChangeListener(this);
                getTask.execute();
            } else {
                if (e.getSource() == buttonOpen) {

                    File file = new File(resourceDirectory + getDHashNode().getName());

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

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if ("state" == propertyChangeEvent.getPropertyName() && SwingWorker.StateValue.DONE == propertyChangeEvent.getNewValue()) {
            LoadingBar loadingBar = LoadingBar.getInstance(frame);
            loadingBar.end();

            controller.refreshPanelGraph();
        }
    }
}
