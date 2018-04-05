package co.edu.uniquindio.dht.it.socket.ring;

import co.edu.uniquindio.dhash.node.DHashNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Ring {
    private final Map<String, DHashNode> nodes;

    public Ring() {
        this.nodes = new HashMap<>();
    }

    public DHashNode getNode(String name){
        return nodes.get(name);
    }

    public void add(String name, DHashNode dHashNode){
        nodes.put(name, dHashNode);
    }

    public Set<String> getNodeNames() {
        return nodes.keySet();
    }
}

