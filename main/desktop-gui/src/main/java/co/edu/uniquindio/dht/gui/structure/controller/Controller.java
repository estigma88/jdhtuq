package co.edu.uniquindio.dht.gui.structure.controller;

import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.chord.node.LookupType;
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.chord.protocol.Protocol.LookupParams;
import co.edu.uniquindio.chord.protocol.Protocol.LookupResponseParams;
import co.edu.uniquindio.dhash.resource.ResourceNotFoundException;
import co.edu.uniquindio.dht.gui.PanelDhash;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.graph.Edge;
import co.edu.uniquindio.dht.gui.structure.graph.PanelGraph;
import co.edu.uniquindio.dht.gui.structure.manager.PanelManager;
import co.edu.uniquindio.dht.gui.structure.utils.DHDChord;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO Documentar
public class Controller implements Observer<Message> {

    private HashingGenerator hashingGenerator;
    // TODO Documentar
    private PanelManager panelManager;
    // TODO Documentar
    private StructureWindow structureWindow;
    // TODO Documentar
    private PanelDhash panelDhash;
    // TODO Documentar
    private List<DHDChord> dhdChordList;
    // TODO Documentar
    private PanelGraph panelGraph;
    // TODO Documentar
    private int numOfNodes = 0;
    // TODO Documentar
    private boolean isFirstNode = true;
    // TODO Documentar
    private String selectedNode = "";
    private StorageNodeFactory storageNodeFactory;
    private CommunicationManager communicationManager;
    private KeyFactory keyFactory;

    // TODO Documentar
    public Controller() {
        dhdChordList = new ArrayList<DHDChord>();
    }

    public Controller(StorageNodeFactory storageNodeFactory, CommunicationManager communicationManager, HashingGenerator hashingGenerator, KeyFactory keyFactory) {
        this();
        this.storageNodeFactory = storageNodeFactory;
        this.communicationManager = communicationManager;
        this.keyFactory = keyFactory;
        this.hashingGenerator = hashingGenerator;
    }

    // TODO Documentar
    public void changeToNode(String selectedNode) {
        validateSelection(selectedNode);

        DHDChord dhdChord = getDHDChordNode(selectedNode);

        changeNode(dhdChord.getDHashNode().getName(), dhdChord);
    }

    // TODO Documentar
    private void validateSelection(String selectedNode) {
        if (selectedNode == null || selectedNode.equals("")) {
            panelDhash.setEnabled(false);
            panelManager.setEnabled(false);
            panelGraph.setPickNode(null);
            structureWindow.clean();
            return;
        } else {
            panelDhash.setEnabled(true);
            panelManager.setEnabled(true);
        }

        this.selectedNode = selectedNode;
    }

    // TODO Documentar
    private void changeNode(String selectedNode, DHDChord dhdChord) {
        StorageNode dHashNode;

        validateSelection(selectedNode);

        if (dhdChord != null) {
            dHashNode = dhdChord.getDHashNode();
            panelDhash.setDHashNode(dHashNode);
            panelDhash.enableOpenFolder();
        }

        panelGraph.setPickNode(dhdChord.getNumberNode() + "");
        panelManager.setSelectedNode(selectedNode);
        structureWindow.setSelectedNode(dhdChord);

    }

    // TODO Documentar
    public void createNode(String name) {
        try {
            StorageNode dHash;
            if (name.equals("")) {
                dHash = storageNodeFactory.createNode();
            } else {
                dHash = storageNodeFactory.createNode(name);
            }

            if (isFirstNode) {
                isFirstNode = false;

                communicationManager.addObserver(this);
            }

            numOfNodes++;

            DHDChord dhdChord = new DHDChord(dHash, numOfNodes, hashingGenerator, keyFactory);

            dhdChordList.add(dhdChord);

            sortNodes();

            panelManager.addNode(dhdChordList);

            makeRing();

            // StabilizedListener stabilizedListener = new StabilizedListener(
            // dhdChordList, this);
            // stabilizedListener.start();

            changeNode(dHash.getName(), dhdChord);

        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // TODO Documentary
    public void createNodesFromPath(File path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while (line != null) {
                createNode(line.trim());

                panelGraph.repaint();

                Thread.sleep(1000);

                line = bufferedReader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // TODO Documentar
    public void createNnodes(int numberOfNodes) {
        for (int i = 0; i < numberOfNodes; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            createNode("");

            panelGraph.repaint();
        }
    }

    // TODO Documentar
    public void deleteNode(String selectedNode) {
        DHDChord dhdChord = getDHDChordNode(selectedNode);
        StorageNode dHashNode = dhdChord.getDHashNode();

        panelDhash.setDHashNode(dHashNode);
        panelDhash.exit();

        dhdChordList.remove(dhdChord);

        if (dhdChordList.size() == 0) {
            validateSelection("");
            makeRing();
            return;
        }

        sortNodes();
        makeRing();

        dhdChord = dhdChordList.get(0);

        // setStabilizingState(false);
        //
        // StabilizedListener stabilizedListener = new StabilizedListener(
        // dhdChordList, this);
        // stabilizedListener.start();

        changeNode(dhdChord.getDHashNode().getName(), dhdChord);
    }

    // TODO Documentar
    private void sortNodes() {
        Collections.sort(dhdChordList);
    }

    // TODO Documentar
    private void makeRing() {
        panelGraph.makeRing(dhdChordList);
    }

    // TODO Documentar
    public void clearGraph() {
        panelGraph.deleteTrace();
    }

    // TODO Documentar
    private void fixEdges(ArrayList<Edge> edges) {
        Edge edge = edges.get(0);
        String successor = edge.getSuccessor();
        ArrayList<Edge> edgesToPaint = new ArrayList<Edge>();

        edgesToPaint.add(edge);

        for (int i = 1; i < edges.size(); i++) {
            Edge e = edges.get(i);

            if (e.getSuccessor().equals(successor)) {
                edgesToPaint.add(e);
            }
        }

        panelGraph.setEdges(edgesToPaint);
    }

    // TODO Documentar
    @Override
    public void update(Message message) {
        if (message.getMessageType().equals(Protocol.LOOKUP_RESPONSE)) {
            if (message.getParam(LookupParams.TYPE.name()).equals(
                    LookupType.LOOKUP.name())) {

                String from = getNumOfNode(message.getAddress().getSource());
                String to = getNumOfNode(message.getAddress().getDestination());
                String successor = getNumOfNode(ChordKey.valueOf(message
                        .getParam(LookupResponseParams.NODE_FIND.name())).getValue());

                panelGraph.addEdge(from, to, successor);
                fixEdges(panelGraph.getEdges());
            }
        }
    }

    // TODO Documentar
    private String getNumOfNode(String node) {
        for (DHDChord dhdChord : dhdChordList) {
            StorageNode dhtNode = dhdChord.getDHashNode();

            if (dhtNode.getName().equals(node)) {
                return "" + dhdChord.getNumberNode();
            }
        }
        return null;
    }

    // TODO Documentar
    private DHDChord getDHDChordNode(String node) {
        for (DHDChord dhdChord : dhdChordList) {
            StorageNode dhtNode = dhdChord.getDHashNode();

            if (dhtNode.getName().equals(node)) {
                return dhdChord;
            }
        }
        return null;
    }

    // TODO Documentar
    public PanelManager getPanelManager() {
        return panelManager;
    }

    // TODO Documentar
    public void setPanelManager(PanelManager panelManager) {
        this.panelManager = panelManager;
    }

    // TODO Documentar
    public StructureWindow getStructureWindow() {
        return structureWindow;
    }

    // TODO Documentar
    public void setStructureWindow(StructureWindow structureWindow) {
        this.structureWindow = structureWindow;
    }

    // TODO Documentar
    public PanelDhash getPanelDhash() {
        return panelDhash;
    }

    // TODO Documentar
    public void setPanelDhash(PanelDhash panelDhash) {
        this.panelDhash = panelDhash;
    }

    // TODO Documentar
    public void setPanelLienzo(PanelGraph panelGrafo) {
        this.panelGraph = panelGrafo;
    }

    // TODO Documentar
    public void setActionColor(boolean isPut) {
        panelGraph.setActionColor(isPut);
    }

    // TODO Documentar
    public String getSelectedNode() {
        return selectedNode;
    }

    // TODO Documentar
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<DHDChord> getDhdChordList() {
        return dhdChordList;
    }

}
