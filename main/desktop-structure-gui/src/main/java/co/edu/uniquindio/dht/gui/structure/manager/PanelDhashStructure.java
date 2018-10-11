/*
 *  desktop-structure-gui  is a example of the peer to peer application with a desktop ui
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
 *
 */

package co.edu.uniquindio.dht.gui.structure.manager;


import co.edu.uniquindio.dhash.resource.LogProgressStatus;
import co.edu.uniquindio.dht.gui.LoadingBar;
import co.edu.uniquindio.dht.gui.PanelDhash;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.dht.gui.structure.task.storageservice.GetTask;
import co.edu.uniquindio.dht.gui.structure.task.storageservice.PutTask;
import co.edu.uniquindio.storage.resource.ProgressStatus;

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

                ProgressStatus progressStatus = LogProgressStatus.builder()
                        .id(file.getAbsolutePath())
                        .build();

                PutTask putTask = new PutTask(structureWindow, getDHashNode(), file, progressStatus);
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

                ProgressStatus progressStatus = LogProgressStatus.builder()
                        .id(resourceId)
                        .build();

                GetTask getTask = new GetTask(structureWindow, getDHashNode(), resourceId, resourceDirectory, progressStatus);
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
