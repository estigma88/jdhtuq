package co.edu.uniquindio.dht.gui.structure;


import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.dht.gui.PanelHashing;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.dht.gui.structure.graph.PanelGraph;
import co.edu.uniquindio.dht.gui.structure.manager.PanelDhashStructure;
import co.edu.uniquindio.dht.gui.structure.manager.PanelManager;
import co.edu.uniquindio.dht.gui.structure.utils.DHDChord;
import co.edu.uniquindio.overlay.KeyFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.text.DecimalFormat;

//TODO Documentar
@SuppressWarnings("serial")
public class StructureWindow extends JFrame implements ActionListener,
        ChangeListener {
    private final JLabel labelMaxAmountNodes;
    //TODO Documentar
    private PanelGraph panelGraph;
    //TODO Documentar
    private PanelDhashStructure panelDhashStructure;
    //TODO Documentar
    private PanelManager panelManager;
    //TODO Documentar
    private PanelHashing panelHashing;
    //TODO Documentar
    private Controller controller;
    //TODO Documentar
    private JSplitPane panelMain;
    //TODO Documentar
    private JSplitPane panelSplit;
    //TODO Documentar
    private JPanel panelLeft;
    //TODO Documentar
    private JPanel panelSouth;
    //TODO Documentar
    private JButton buttonClear;
    //TODO Documentar
    private JLabel labelHashing;
    //TODO Documentar
    private JLabel labelValue;
    //TODO Documentar
    private JLabel labelId;
    //TODO Documentar
    private JLabel labelLengthKey;
    //TODO Documentar
    private JSpinner spinnerLengthKey;
    //TODO Documentar
    private SpinnerNumberModel spinnerNumberLengthKeyModel;
    //TODO Documentar
    private DecimalFormat decimalFormat;
    //TODO Documentar
    private BigInteger actualHashing;
    //TODO Documentar
    private int stringLength;
    private HashingGenerator hashingGenerator;
    private KeyFactory keyFactory;

    //TODO Documentar
    public StructureWindow(HashingGenerator hashingGenerator, KeyFactory keyFactory, String resourceDirectory) {
        this("Structure Window", hashingGenerator, keyFactory, resourceDirectory);
    }

    //TODO Documentar
    public StructureWindow(String title, HashingGenerator hashingGenerator, KeyFactory keyFactory, String resourceDirectory) {
        this.hashingGenerator = hashingGenerator;
        this.keyFactory = keyFactory;

        setTitle(title);
        setSize(900, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        panelManager = new PanelManager(this);

        panelDhashStructure = new PanelDhashStructure(this, resourceDirectory);
        panelDhashStructure.setEnabled(false);

        panelGraph = new PanelGraph();

        panelSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        buttonClear = new JButton("Clean");

        Font font = new Font("Verdana", Font.BOLD, 11);

        labelHashing = new JLabel();
        labelHashing.setFont(font);
        labelHashing.setForeground(new Color(78, 138, 78));

        labelValue = new JLabel();
        labelValue.setFont(font);
        labelValue.setForeground(Color.black);

        labelLengthKey = new JLabel("Length ChordKey");
        labelLengthKey.setFont(font);
        labelLengthKey.setForeground(Color.black);

        labelId = new JLabel();
        labelId.setFont(font);
        labelId.setForeground(Color.black);

        int keyLength = keyFactory.getKeyLength();

        BigInteger i = new BigInteger("2");
        i = i.pow(keyLength);

        stringLength = i.toString().length();

        spinnerNumberLengthKeyModel = new SpinnerNumberModel(keyLength, 2,
                160, 1);
        spinnerLengthKey = new JSpinner(spinnerNumberLengthKeyModel);

        labelMaxAmountNodes = new JLabel("Max Nodes: " + i);
        labelMaxAmountNodes.setFont(font);
        labelMaxAmountNodes.setForeground(Color.black);

        StringBuilder format = new StringBuilder();

        for (int j = 0; j < stringLength; j++) {
            format.append("0");
        }

        decimalFormat = new DecimalFormat(format.toString());

        panelSouth.add(buttonClear);
        panelSouth.add(labelId);
        panelSouth.add(labelValue);
        panelSouth.add(labelHashing);
        panelSouth.add(labelLengthKey);
        panelSouth.add(spinnerLengthKey);
        panelSouth.add(labelMaxAmountNodes);

        panelLeft = new JPanel(new BorderLayout());
        panelLeft.add(panelGraph, BorderLayout.CENTER);
        panelLeft.add(panelDhashStructure, BorderLayout.NORTH);

        panelMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panelMain.setLeftComponent(panelLeft);
        panelMain.setRightComponent(panelManager);
        panelMain.setDividerSize(2);
        panelMain.setDividerLocation(650);
        panelMain.setResizeWeight(1.0);

        panelHashing = new PanelHashing(this, keyFactory);

        panelSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panelSplit.setOneTouchExpandable(true);
        panelSplit.setResizeWeight(1.0);
        panelSplit.setDividerSize(10);
        panelSplit.setLeftComponent(panelMain);
        panelSplit.setRightComponent(panelHashing);

        add(panelSplit, BorderLayout.CENTER);
        add(panelSouth, BorderLayout.SOUTH);

        buttonClear.addActionListener(this);
        spinnerLengthKey.addChangeListener(this);
    }

    //TODO Documentar
    public void setController(Controller controller) {
        this.controller = controller;
        panelManager.setController(controller);
        panelGraph.setController(controller);
        panelDhashStructure.setController(controller);
        panelDhashStructure.setStructureWindow(this);
        this.controller.setPanelDhash(panelDhashStructure);
        this.controller.setPanelManager(panelManager);
        this.controller.setPanelLienzo(panelGraph);
    }

    //TODO Documentar
    public Controller getController() {
        return controller;
    }

    //TODO Documentar
    public void setSelectedNode(DHDChord dhdChord) {
        actualHashing = hashingGenerator.generateHashing(dhdChord.getDHashNode().getName(), keyFactory.getKeyLength());

        labelId.setText("Id=" + dhdChord.getNumberNode());
        labelValue.setText("Value=" + dhdChord.getDHashNode().getName());
        labelHashing.setText("Hashing=" + actualHashing);
    }

    //TODO Documentar
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.clearGraph();
    }

    //TODO Documentar
    public void clean() {
        labelId.setText("");
        labelValue.setText("");
        labelHashing.setText("");
    }

    //TODO Documentar
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == spinnerLengthKey) {
            keyFactory.updateKeyLength(spinnerNumberLengthKeyModel.getNumber().intValue());

            int keyLength = keyFactory.getKeyLength();

            BigInteger i = new BigInteger("2");
            i = i.pow(keyLength);

            labelMaxAmountNodes.setText("Max Nodes: " + i);
            stringLength = i.toString().length();

            StringBuilder format = new StringBuilder();

            for (int j = 0; j < stringLength; j++) {
                format.append("0");
            }

            decimalFormat.applyPattern(format.toString());

            panelHashing.resetSpinner(keyLength);
        }

    }

    public JSpinner getSpinnerLengthKey() {
        return spinnerLengthKey;
    }

}
