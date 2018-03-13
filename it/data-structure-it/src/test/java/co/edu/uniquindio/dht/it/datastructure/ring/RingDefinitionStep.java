package co.edu.uniquindio.dht.it.datastructure.ring;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.storage.StorageNodeFactory;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RingDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;
    @Autowired
    private StorageNodeFactory storageNodeFactory;
    @Autowired
    private KeyFactory keyFactory;
    private Map<String, String> nodeData;

    @When("^Key length is (\\d+)$")
    public void key_length_is(int keyLength) throws Throwable {
        keyFactory.updateKeyLength(keyLength);
    }

    @Given("^the following node's names:$")
    public void the_following_node_s_names(Map<String, String> nodeNames) throws Throwable {
        this.nodeData = nodeNames;
    }

    @When("^Chord ring is created$")
    public void chord_ring_is_created() throws Throwable {
        Ring.RingBuilder ringBuilder = Ring.builder();

        for (String nodeName : nodeData.keySet()) {
            ringBuilder.nodeName(nodeName)
                    .node(nodeName, (DHashNode) storageNodeFactory.createNode(nodeName));
        }

        Ring ring = ringBuilder.build();

        for(String nodeName : ring.getNodeNames()){
            DHashNode node = ring.getNode(nodeName);

            ChordNode chordNode = (ChordNode) node.getOverlayNode();

            assertThat(chordNode.getKey()).isNotNull();
            assertThat(chordNode.getKey().getValue()).isEqualTo(nodeName);
            assertThat(chordNode.getKey().getHashing()).isEqualTo(new BigInteger(nodeData.get(nodeName)));
        }

        world.setRing(ring);
    }

    @When("^Wating for stabilizing after (\\d+) seconds$")
    public void wating_for_stabilizing_after_seconds(int timeToStabilize) throws Throwable {
        Thread.sleep(timeToStabilize * 1000);
    }

    @Then("^Chord ring is stable with the following successors:$")
    public void chord_ring_is_stable_with_the_following_successors(Map<String, String> successorNode) throws Throwable {
        Ring ring = world.getRing();

        for (String nodeName : successorNode.keySet()) {
            DHashNode node = ring.getNode(nodeName);

            ChordNode chordNode = (ChordNode) node.getOverlayNode();

            assertThat(chordNode.getSuccessor()).isNotNull();
            assertThat(chordNode.getSuccessor().getValue()).isEqualTo(successorNode.get(nodeName));
        }
    }
}
