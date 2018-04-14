package co.edu.uniquindio.dht.it.socket.test.ring;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dht.it.socket.test.MessageClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Ring {
    private final Map<String, MessageClient> nodes;

    public Ring() {
        this.nodes = new HashMap<>();
    }

    public MessageClient getNode(String name){
        return nodes.get(name);
    }

    public void add(String name, MessageClient messageClient){
        nodes.put(name, messageClient);
    }

    public Set<String> getNodeNames() {
        return nodes.keySet();
    }
}

