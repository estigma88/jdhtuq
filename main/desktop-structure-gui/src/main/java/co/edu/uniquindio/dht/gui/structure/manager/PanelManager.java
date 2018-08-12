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


import co.edu.uniquindio.dht.gui.LoadingBar;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.dht.gui.structure.task.controller.DeleteNodeTask;
import co.edu.uniquindio.dht.gui.structure.task.controller.NewNodeTask;
import co.edu.uniquindio.dht.gui.structure.task.controller.NewNodesFromFileTask;
import co.edu.uniquindio.dht.gui.structure.task.controller.NewNodesTask;
import co.edu.uniquindio.dht.gui.structure.utils.DHDChord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

//TODO Documentar
@SuppressWarnings("serial")
public class PanelManager extends JPanel implements ActionListener,
        MouseListener, PropertyChangeListener {
    //TODO Documentar
    private JPanel panelCraete;
    //TODO Documentar
    private JButton buttonCreateNode;
    //TODO Documentar
    private JButton buttonCreateNnodes;
    //TODO Documentar
    private JButton buttonCreateNodeFromFile;
    //TODO Documentar
    private JButton buttonDeleteNode;
    //TODO Documentar
    private DefaultTableModel defaultTableModel;
    //TODO Documentar
    private JTable tableNodes;
    //TODO Documentar
    private JScrollPane scrollTableNodes;
    //TODO Documentar
    private Controller controller;
    //TODO Documentar
    private JPanel panelLayout;
    //TODO Documentar
    private JPanel panelN;
    //TODO Documentar
    private JFileChooser chooser;
    //TODO Documentar
    private JFrame frame;

    //TODO Documentar
    public PanelManager(JFrame frame) {
        setLayout(new BorderLayout());

        this.frame = frame;

        chooser = new JFileChooser();
        chooser.setApproveButtonText("Select");
        chooser.setDialogTitle("Select");

        // Panel Center
        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Node");

        tableNodes = new JTable(defaultTableModel);

        scrollTableNodes = new JScrollPane();
        scrollTableNodes.setViewportView(tableNodes);

        add(scrollTableNodes, BorderLayout.CENTER);

        // Panel Center

        // Panel Create

        panelLayout = new JPanel(new GridLayout(2, 1));

        buttonCreateNnodes = new JButton("Create N Nodes");
        buttonCreateNnodes.setToolTipText("Creates a number N of nodes");

        buttonCreateNodeFromFile = new JButton("Create F Nodes");
        buttonCreateNodeFromFile.setToolTipText("Creates Nodes from a file");

        buttonCreateNode = new JButton("Create Node");
        buttonCreateNode.setToolTipText("Creates a Node with a specific name");

        buttonDeleteNode = new JButton("Delete Node");
        buttonDeleteNode.setToolTipText("Deletes the selected node");
        buttonDeleteNode.setEnabled(false);

        panelN = new JPanel(new FlowLayout());
        panelN.add(buttonDeleteNode);
        panelN.add(buttonCreateNodeFromFile);

        panelCraete = new JPanel(new FlowLayout());
        panelCraete.add(buttonCreateNode);
        panelCraete.add(buttonCreateNnodes);

        panelLayout.add(panelCraete);
        panelLayout.add(panelN);

        add(panelLayout, BorderLayout.SOUTH);
        // Panel Create

        tableNodes.addMouseListener(this);
        buttonCreateNode.addActionListener(this);
        buttonDeleteNode.addActionListener(this);
        buttonCreateNnodes.addActionListener(this);
        buttonCreateNodeFromFile.addActionListener(this);
    }

    //TODO Documentar
    public void setController(Controller controller) {
        this.controller = controller;
    }

    //TODO Documentar
    public Controller getController() {
        return controller;
    }

    //TODO Documentar
    public void addNode(List<DHDChord> dhdChordList) {
        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("id");
        defaultTableModel.addColumn("Node");

        tableNodes = new JTable(defaultTableModel);

        TableColumn tc = tableNodes.getColumn(defaultTableModel
                .getColumnName(0));
        tc.setPreferredWidth(10);

        for (int i = 0; i < dhdChordList.size(); i++) {
            defaultTableModel.addRow(new Object[]{
                    dhdChordList.get(i).getNumberNode(),
                    dhdChordList.get(i).getDHashNode().getName()});
        }

        tableNodes.addMouseListener(this);

        scrollTableNodes.setViewportView(tableNodes);

        buttonDeleteNode.setEnabled(true);
    }

    //TODO Documentar
    public void setSelectedNode(String selectedNode) {
        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            if (defaultTableModel.getValueAt(i, 1).equals(selectedNode))
                tableNodes.setRowSelectionInterval(i, i);
        }
    }

    //TODO Documentar
    public void setEnabled(boolean value) {
        buttonDeleteNode.setEnabled(value);
    }

    //TODO Documentar
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonCreateNode) {
            final String name = JOptionPane.showInputDialog(null,
                    "Please give a name for the Node", "Insert a name",
                    JOptionPane.INFORMATION_MESSAGE);

            if (name == null)
                return;

            LoadingBar loadingBar = LoadingBar.getInstance(frame);

            loadingBar.setConfiguration(true, 100);
            loadingBar.setValue(1, "Creating node...");
            loadingBar.begin();

            NewNodeTask newNodeTask = new NewNodeTask(frame, controller, name);
            newNodeTask.addPropertyChangeListener(this);
            newNodeTask.execute();
        } else {
            if (e.getSource() == buttonDeleteNode) {
                int selectedRow = tableNodes.getSelectedRow();

                if (selectedRow == -1)
                    return;

                final String node = defaultTableModel
                        .getValueAt(selectedRow, 1)
                        + "";

                defaultTableModel.removeRow(selectedRow);

                LoadingBar loadingBar = LoadingBar.getInstance(frame);

                loadingBar.setConfiguration(true, 100);
                loadingBar.setValue(1, "Deleting node...");
                loadingBar.begin();

                DeleteNodeTask deleteNodeTask = new DeleteNodeTask(frame, controller, node);
                deleteNodeTask.addPropertyChangeListener(this);
                deleteNodeTask.execute();

            } else {
                if (e.getSource() == buttonCreateNnodes) {
                    final int numberOfNodes;

                    try {
                        numberOfNodes = Integer.parseInt(JOptionPane
                                .showInputDialog(null,
                                        "Please give a number of nodes",
                                        "Insert a number of nodes",
                                        JOptionPane.INFORMATION_MESSAGE));
                    } catch (NumberFormatException e2) {
                        JOptionPane.showMessageDialog(frame,
                                "The number you entered is not valid", "ERROR",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    if (numberOfNodes > 0) {
                        LoadingBar loadingBar = LoadingBar
                                .getInstance(frame);

                        loadingBar.setConfiguration(true, 100);
                        loadingBar.setValue(1, "Creating "
                                + numberOfNodes + " nodes...");
                        loadingBar.begin();

                        NewNodesTask newNodesTask = new NewNodesTask(frame, controller, numberOfNodes);
                        newNodesTask.addPropertyChangeListener(this);
                        newNodesTask.execute();
                    }

                } else {
                    if (e.getSource() == buttonCreateNodeFromFile) {
                        int returnVal = chooser.showOpenDialog(controller
                                .getStructureWindow());
                        final File path;

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            path = chooser.getSelectedFile();

                            LoadingBar loadingBar = LoadingBar
                                    .getInstance(frame);

                            loadingBar.setConfiguration(true, 100);
                            loadingBar.setValue(1,
                                    "Creating nodes from file " + path);
                            loadingBar.begin();

                            NewNodesFromFileTask newNodesFromFileTask = new NewNodesFromFileTask(frame, controller, path);
                            newNodesFromFileTask.addPropertyChangeListener(this);
                            newNodesFromFileTask.execute();
                        }
                    }
                }
            }
        }
        if (controller.getDhdChordList().size() == 0) {
            ((StructureWindow) frame).getSpinnerLengthKey().setEnabled(true);
        } else {
            ((StructureWindow) frame).getSpinnerLengthKey().setEnabled(false);
        }
    }

    private void validateCreated() {
        if (controller.getDhdChordList().size() == 0) {
            ((StructureWindow) frame).getSpinnerLengthKey().setEnabled(true);
        } else {
            ((StructureWindow) frame).getSpinnerLengthKey().setEnabled(false);
        }
    }

    //TODO Documentar
    @Override
    public void mouseClicked(MouseEvent e) {
        String selectedNode = ""
                + tableNodes.getValueAt(tableNodes.getSelectedRow(), 1);

        controller.changeToNode(selectedNode);
    }

    //TODO Documentar
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    //TODO Documentar
    @Override
    public void mouseExited(MouseEvent e) {

    }

    //TODO Documentar
    @Override
    public void mousePressed(MouseEvent e) {

    }

    //TODO Documentar
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if ("state" == propertyChangeEvent.getPropertyName() && SwingWorker.StateValue.DONE == propertyChangeEvent.getNewValue()) {
            LoadingBar loadingBar = LoadingBar.getInstance(frame);
            loadingBar.end();

            validateCreated();

            if (defaultTableModel.getRowCount() == 0)
                buttonDeleteNode.setEnabled(false);
        }
    }
}
