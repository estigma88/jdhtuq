package co.edu.uniquindio.dht.it.datastructure.ring;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.dhash.node.DHashNode;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class Ring {
    @Singular
    private final List<String> nodeNames;
    @Singular
    private final Map<String, DHashNode> nodes;

    public DHashNode getNode(String name){
        return nodes.get(name);
    }
}

