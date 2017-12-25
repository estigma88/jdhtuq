package co.edu.uniquindio.dht.gui.structure.manager;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import co.edu.uniquindio.dht.gui.LoadingBar;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.dht.gui.structure.utils.DHDChord;
//TODO Documentar
@SuppressWarnings("serial")
public class PanelManager extends JPanel implements ActionListener,
		MouseListener {
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
			defaultTableModel.addRow(new Object[] {
					dhdChordList.get(i).getNumberNode(),
					dhdChordList.get(i).getDHashNode().getName() });
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

			Thread thread = new Thread(new Runnable() {
				public void run() {

					LoadingBar loadingBar = LoadingBar.getInstance(frame);

					loadingBar.setConfiguration(true, 100);
					loadingBar.setValue(1, "Creating node...");
					loadingBar.begin();

					controller.createNode(name);

					loadingBar.end();
					
					validateCreated();
				}
			});
			thread.start();
		} else {
			if (e.getSource() == buttonDeleteNode) {
				int selectedRow = tableNodes.getSelectedRow();

				if (selectedRow == -1)
					return;

				final String node = defaultTableModel
						.getValueAt(selectedRow, 1)
						+ "";

				defaultTableModel.removeRow(selectedRow);

				Thread thread = new Thread(new Runnable() {
					public void run() {

						LoadingBar loadingBar = LoadingBar.getInstance(frame);

						loadingBar.setConfiguration(true, 100);
						loadingBar.setValue(1, "Deleting node...");
						loadingBar.begin();

						controller.deleteNode(node);

						loadingBar.end();
						
						validateCreated();
					}
				});
				thread.start();
				if (defaultTableModel.getRowCount() == 0)
					buttonDeleteNode.setEnabled(false);
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
						Thread thread = new Thread(new Runnable() {
							public void run() {

								LoadingBar loadingBar = LoadingBar
										.getInstance(frame);

								loadingBar.setConfiguration(true, 100);
								loadingBar.setValue(1, "Creating "
										+ numberOfNodes + " nodes...");
								loadingBar.begin();

								controller.createNnodes(numberOfNodes);

								loadingBar.end();
								
								validateCreated();
							}
						});
						thread.start();
					}

				} else {
					if (e.getSource() == buttonCreateNodeFromFile) {
						int returnVal = chooser.showOpenDialog(controller
								.getStructureWindow());
						final File path;

						if (returnVal == JFileChooser.APPROVE_OPTION) {
							path = chooser.getSelectedFile();

							Thread thread = new Thread(new Runnable() {
								public void run() {

									LoadingBar loadingBar = LoadingBar
											.getInstance(frame);

									loadingBar.setConfiguration(true, 100);
									loadingBar.setValue(1,
											"Creating nodes from file " + path);
									loadingBar.begin();

									controller.createNodesFromPath(path);

									loadingBar.end();
									
									validateCreated();
								}
							});
							thread.start();
						}
					}
				}
			}
		}
		if (controller.getDhdChordList().size()==0) {
			((StructureWindow)frame).getSpinnerLengthKey().setEnabled(true);
		}else{
			((StructureWindow)frame).getSpinnerLengthKey().setEnabled(false);
		}
	}
	private void validateCreated() {
		if (controller.getDhdChordList().size()==0) {
			((StructureWindow)frame).getSpinnerLengthKey().setEnabled(true);
		}else{
			((StructureWindow)frame).getSpinnerLengthKey().setEnabled(false);
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
}
