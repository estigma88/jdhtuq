package co.edu.uniquindio.dht.gui;

import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.math.BigInteger;
import java.text.DecimalFormat;

//TODO Documentar
@SuppressWarnings("serial")
public class PanelHashing extends JPanel implements ActionListener,
        KeyListener, ChangeListener {
    // TODO Documentar
    private JTextField textFieldFile;
    // TODO Documentar
    private JButton buttonGenerate;
    // TODO Documentar
    private JButton buttonBrowse;
    // TODO Documentar
    private JButton buttonCollection;
    // TODO Documentar
    private JButton buttonSave;
    // TODO Documentar
    private JButton buttonLoad;
    // TODO Documentar
    private JButton buttonDelete;
    // TODO Documentar
    private JButton buttonClean;
    // TODO Documentar
    private JScrollPane scroll;
    // TODO Documentar
    private JTable table;
    // TODO Documentar
    private JPanel southPanel;
    // TODO Documentar
    private JPanel northPanel;
    // TODO Documentar
    private DefaultTableModel model;
    // TODO Documentar
    private JFileChooser chooser;
    // TODO Documentar
    private JFileChooser folderChooser;
    // TODO Documentar
    private final String SEPARATOR = "_:_";
    // TODO Documentar
    private JFrame frame;
    // TODO Documentar
    private DecimalFormat decimalFormat;
    // TODO Documentar
    private JSpinner spinner;
    private SpinnerNumberModel spinnerNumberModel;
    private KeyFactory keyFactory;

    // TODO Documentar
    public PanelHashing(JFrame frame, KeyFactory keyFactory) {
        this.keyFactory = keyFactory;

        setLayout(new BorderLayout());

        this.frame = frame;

        int keyLength = keyFactory.getKeyLength();

        BigInteger i = new BigInteger("2");
        i = i.pow(keyLength);

        int stringLength = i.toString().length();


        spinnerNumberModel = new SpinnerNumberModel();
        spinnerNumberModel.setValue(stringLength);
        spinnerNumberModel.setMaximum(stringLength);
        spinnerNumberModel.setMinimum(1);
        spinnerNumberModel.setStepSize(1);
        spinner = new JSpinner(spinnerNumberModel);

        StringBuilder format = new StringBuilder();

        for (int j = 0; j < stringLength; j++) {
            format.append("0");
        }

        decimalFormat = new DecimalFormat(format.toString());

        northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());

        textFieldFile = new JTextField(15);
        buttonGenerate = new JButton("Generate Hashing");
        buttonBrowse = new JButton("Select a File");
        buttonCollection = new JButton("Select a Folder");
        buttonSave = new JButton("Save");
        buttonLoad = new JButton("Load");

        northPanel.add(textFieldFile);
        northPanel.add(buttonGenerate);
        northPanel.add(buttonBrowse);
        northPanel.add(buttonCollection);
        northPanel.add(spinner);

        southPanel = new JPanel();

        southPanel.setLayout(new FlowLayout());

        southPanel.add(buttonSave);
        southPanel.add(buttonLoad);
        southPanel.add(new JLabel("              "));

        buttonDelete = new JButton("Delete Row");
        buttonClean = new JButton("Clean Table");

        southPanel.add(buttonDelete);
        southPanel.add(buttonClean);

        inicializarTabla();

        chooser = new JFileChooser();
        chooser.setApproveButtonText("select");
        chooser.setDialogTitle("Select");

        folderChooser = new JFileChooser();
        folderChooser.setApproveButtonText("select");
        folderChooser.setDialogTitle("Select");
        folderChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);
        add(scroll, BorderLayout.CENTER);

        buttonGenerate.addActionListener(this);
        buttonBrowse.addActionListener(this);
        buttonCollection.addActionListener(this);
        buttonSave.addActionListener(this);
        buttonLoad.addActionListener(this);
        buttonDelete.addActionListener(this);
        buttonClean.addActionListener(this);
        textFieldFile.addKeyListener(this);
        spinner.addChangeListener(this);
    }

    // TODO Documentar
    private void inicializarTabla() {
        model = new DefaultTableModel() {
            @SuppressWarnings("unchecked")
            @Override
            public Class getColumnClass(int columna) {
                if (columna == 0)
                    return String.class;
                return BigInteger.class;
            }
        };

        model.addColumn("Name");
        model.addColumn("Hashing");

        table = new JTable(model);

        TableRowSorter<TableModel> elQueOrdena = new TableRowSorter<TableModel>(
                model);
        table.setRowSorter(elQueOrdena);

        scroll = new JScrollPane();
        scroll.setViewportView(table);
    }

    // TODO Documentar
    @Override
    public void actionPerformed(ActionEvent evento) {
        if (evento.getSource() == buttonGenerate) {
            String nombre = textFieldFile.getText();

            Key key = keyFactory.newKey(nombre);

            model.addRow(new Object[]{nombre,
                    cut(padding(key.getHashing()))});

            textFieldFile.setText(null);
        } else {
            if (evento.getSource() == buttonBrowse) {
                int returnVal = chooser.showOpenDialog(this);
                File file = null;
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();

                    textFieldFile.setText(file.getName());
                    buttonGenerate.doClick();
                }
            } else {
                if (evento.getSource() == buttonCollection) {
                    int returnVal = folderChooser.showOpenDialog(this);
                    final File file;
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = folderChooser.getSelectedFile();

                        if (file.isDirectory()) {
                            Thread thread = new Thread(new Runnable() {
                                public void run() {

                                    LoadingBar loadingBar = LoadingBar
                                            .getInstance(frame);

                                    loadingBar.setConfiguration(true, 100);
                                    loadingBar.setValue(1,
                                            "Loading the directories");
                                    loadingBar.begin();

                                    selectAllFiles(file);

                                    loadingBar.end();
                                }
                            });
                            thread.start();

                        } else {
                            textFieldFile.setText(file.getName());
                            buttonGenerate.doClick();
                        }
                    }
                } else {
                    if (evento.getSource() == buttonSave) {
                        int returnVal = chooser.showSaveDialog(this);
                        File file = null;
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            file = chooser.getSelectedFile();
                            try {
                                FileWriter fileWriter = new FileWriter(file);
                                BufferedWriter buffered = new BufferedWriter(
                                        fileWriter);

                                for (int i = 0; i < model.getRowCount(); i++) {
                                    StringBuilder stringBuilder = new StringBuilder();

                                    stringBuilder.append(model.getValueAt(i, 0)
                                            + SEPARATOR);
                                    stringBuilder.append(model.getValueAt(i, 1));
                                    stringBuilder.append(System.getProperty("line.separator"));

                                    buffered.write(stringBuilder.toString());
                                }

                                buffered.flush();
                                buffered.close();
                                fileWriter.close();

                                JOptionPane.showMessageDialog(null,
                                        "The file has been saved",
                                        "File Saved",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                                JOptionPane.showMessageDialog(null,
                                        "Error while saving the file", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } catch (IOException e2) {
                                e2.printStackTrace();
                                JOptionPane.showMessageDialog(null,
                                        "Error while saving the file", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        if (evento.getSource() == buttonLoad) {
                            int returnVal = chooser.showOpenDialog(this);
                            File file = null;
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                file = chooser.getSelectedFile();

                                try {
                                    FileReader fileReader = new FileReader(file);
                                    BufferedReader bufferedReader = new BufferedReader(
                                            fileReader);
                                    String line = null;

                                    while ((line = bufferedReader.readLine()) != null) {
                                        model
                                                .addRow(new Object[]{
                                                        line.split(SEPARATOR)[0]
                                                                .trim(),
                                                        cut(padding(
                                                                new BigInteger(
                                                                        line
                                                                                .split(SEPARATOR)[1]
                                                                                .trim())))});
                                    }

                                    bufferedReader.close();
                                    fileReader.close();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        } else {
                            if (evento.getSource() == buttonDelete) {
                                if (table.getSelectedRow() == -1) {
                                    JOptionPane.showMessageDialog(null,
                                            "You must select a row",
                                            "Select a Row",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }

                                model.removeRow(table.getSelectedRow());
                            } else {
                                if (evento.getSource() == buttonClean) {

                                    while (model.getRowCount() > 0) {
                                        model
                                                .removeRow(model.getRowCount() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO Documentar
    private String cut(String text) {
        return text.substring(0, spinnerNumberModel.getNumber().intValue());
    }

    // TODO Documentar
    private String padding(BigInteger hashing) {

        return decimalFormat.format(hashing);
    }

    // TODO Documentar
    private boolean selectAllFiles(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    selectAllFiles(files[i]);
                } else {
                    String nombre = files[i].getName();

                    Key key = keyFactory.newKey(nombre);
                    model.addRow(new Object[]{nombre, cut(padding(key.getHashing()))});

                }
            }
        }
        return true;
    }

    // TODO Documentar
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            buttonGenerate.doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // TODO Documentar
    @Override
    public void stateChanged(ChangeEvent e) {
        loadTable();
    }

    // TODO Documentar
    private void loadTable() {
        for (int i = 0; i < model.getRowCount(); i++) {
            Key key = keyFactory.newKey((String) model.getValueAt(i, 0));

            model.setValueAt(cut(padding(key.getHashing())), i, 1);
        }

    }

    public void resetSpinner(int keyLength) {
        BigInteger i = new BigInteger("2");
        i = i.pow(keyLength);

        int stringLength = i.toString().length();

        spinner.removeChangeListener(this);
        spinnerNumberModel.setValue(stringLength);
        spinnerNumberModel.setMaximum(stringLength);
        spinnerNumberModel.setMinimum(1);
        spinnerNumberModel.setStepSize(1);
        spinner.addChangeListener(this);

        StringBuilder format = new StringBuilder();

        for (int j = 0; j < stringLength; j++) {
            format.append("0");
        }

        decimalFormat.applyPattern(format.toString());
    }
}
