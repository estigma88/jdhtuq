package co.edu.uniquindio.dht.gui;

import co.edu.uniquindio.dhash.resource.InputStreamResource;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.EscapeChars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private String resourceDirectory = "dhash/";

    // TODO Documentar
    public PanelDhash(boolean mode, JFrame frame) {
        this.mode = mode;
        this.frame = frame;

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
                        try {
                            InputStreamResource fileResource = new InputStreamResource(fichero.getName(), new FileInputStream(fichero));
                            getDHashNode().put(fileResource);
                        } catch (StorageException e1) {
                            loadingBar.end();
                            JOptionPane.showMessageDialog(frame, e1
                                    .getMessage());
                        } catch (FileNotFoundException e1) {
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
                            Resource resource = getDHashNode().get(a);

                            Files.createDirectories(Paths.get(resourceDirectory + getDHashNode().getName() + "/gets/"));
                            Files.copy(resource.getInputStream(), Paths.get(resourceDirectory + getDHashNode().getName() + "/gets/" + resource.getKey()));

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
                    File file = new File("dhash/"
                            + EscapeChars.forHTML(getDHashNode().getName(),
                            true));

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
                dHashNode.leave();
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
        File file = new File("dhash/"
                + EscapeChars.forHTML(getDHashNode().getName(), true));

        buttonOpen.setEnabled(file.exists());
    }
}
