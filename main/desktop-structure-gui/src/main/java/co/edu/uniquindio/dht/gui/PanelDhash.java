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

package co.edu.uniquindio.dht.gui;

import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.resource.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

//TODO Documentar
@SuppressWarnings("serial")
public class PanelDhash extends JPanel implements ActionListener {
    // TODO Documentar
    protected JFileChooser fileChooser;
    // TODO Documentar
    protected JButton buttonPut;
    // TODO Documentar
    protected JButton buttonGet;
    // TODO Documentar
    protected JButton buttonExit;
    // TODO Documentar
    protected JButton buttonOpen;
    // TODO Documentar
    protected StorageNode dHashNode;
    // TODO Documentar
    protected JFrame frame;
    // TODO Documentar
    protected boolean mode;
    // TODO Documentar
    public static final boolean STRUCTURE = false;
    // TODO Documentar
    public static final boolean NETWORK = true;
    protected String resourceDirectory;

    // TODO Documentar
    public PanelDhash(boolean mode, JFrame frame, String resourceDirectory) {
        this.mode = mode;
        this.frame = frame;
        this.resourceDirectory = resourceDirectory;

        setLayout(new FlowLayout());
        setBorder(BorderFactory.createEtchedBorder());

        fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Put");
        fileChooser.setDialogTitle("Select");

        buttonPut = new JButton("put");
        buttonPut
                .setToolTipText("Puts a file from a specific local directory into the network");

        buttonGet = new JButton("get");
        buttonGet.setToolTipText("Retrieves a file from the network");

        buttonExit = new JButton("exit");
        buttonExit.setToolTipText("Its a voluntary departure of the node");

        buttonOpen = new JButton("open");
        buttonOpen
                .setToolTipText("Open the directory where the node stores its resources");

        add(buttonPut);
        add(buttonGet);
        add(buttonOpen);

        if (mode)
            add(buttonExit);

        buttonPut.addActionListener(this);
        buttonGet.addActionListener(this);
        buttonOpen.addActionListener(this);

        if (mode)
            buttonExit.addActionListener(this);
    }

    // TODO Documentar
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonPut) {

            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {

                        LoadingBar loadingBar = LoadingBar.getInstance(frame);

                        loadingBar.setConfiguration(true, 100);
                        loadingBar.setValue(1, "Doing Put...");
                        loadingBar.begin();

                        File fichero = fileChooser.getSelectedFile();
                        try (Resource fileResource = FileResource.withPath()
                                .id(fichero.getName())
                                .path(fichero.getAbsolutePath())
                                .build((name, current, size) -> {
                                })) {
                            getDHashNode().put(fileResource, ((name, current, size) -> {
                            }));
                        } catch (IOException e1) {
                            loadingBar.end();
                            JOptionPane.showMessageDialog(frame, e1
                                    .getMessage());
                        }

                        loadingBar.end();
                    }
                });
                thread.start();
            }

        } else {
            if (e.getSource() == buttonGet) {
                final String a = JOptionPane
                        .showInputDialog(
                                null,
                                "Please write the name of the file that you wish retrieve",
                                "Insert a file name",
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
                            Resource resource = getDHashNode().get(a, ((name, current, size) -> {
                            })).get();

                            Files.createDirectories(Paths.get(resourceDirectory + getDHashNode().getName() + "/gets/"));
                            Files.copy(resource.getInputStream(), Paths.get(resourceDirectory + getDHashNode().getName() + "/gets/" + resource.getId()));

                        } catch (InterruptedException | ExecutionException | IOException e1) {
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
                    File file = new File("dhash/"
                            + getDHashNode().getName());

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
                                        + File.separator, "ERROR",
                                JOptionPane.INFORMATION_MESSAGE);
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

    // TODO Documentar
    public void exit() {
        try {
            if (dHashNode != null)
                dHashNode.leave(((name, current, size) -> {
                }));
        } catch (StorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (mode)
                System.exit(0);
        }

    }

    // TODO Documentar
    public void setDHashNode(StorageNode dHashNode) {
        this.dHashNode = dHashNode;
    }

    // TODO Documentar
    public StorageNode getDHashNode() {
        return dHashNode;
    }

    // TODO Documentar
    public void setEnabled(boolean value) {
        buttonPut.setEnabled(value);
        buttonGet.setEnabled(value);
        buttonOpen.setEnabled(value);

        if (mode)
            buttonExit.setEnabled(value);
    }

    public void enableOpenFolder() {
        File file = new File(resourceDirectory
                + getDHashNode().getName());

        buttonOpen.setEnabled(file.exists());
    }
}
