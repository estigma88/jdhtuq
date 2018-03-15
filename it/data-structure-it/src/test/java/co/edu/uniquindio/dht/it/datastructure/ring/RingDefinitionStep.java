package co.edu.uniquindio.dht.it.datastructure.ring;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNodeFactory;
import cucumber.api.java.After;
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
    private List<Node> nodes;

    @Given("^I set the key length to (\\d+)$")
    public void key_length_is(int keyLength) throws Throwable {
        keyFactory.updateKeyLength(keyLength);
    }

    @Given("^I have the following node's names and hashings:$")
    public void the_following_node_s_names(List<Node> nodes) throws Throwable {
        this.nodes = nodes;
    }

    @When("^I create the Chord ring$")
    public void chord_ring_is_created() throws Throwable {
        Ring.RingBuilder ringBuilder = Ring.builder();

        for (Node node : nodes) {
            ringBuilder.nodeName(node.getName())
                    .node(node.getName(), (DHashNode) storageNodeFactory.createNode(node.getName()));
        }

        Ring ring = ringBuilder.build();

        for(Node node : nodes){
            DHashNode dHashNode = ring.getNode(node.getName());

            ChordNode chordNode = (ChordNode) dHashNode.getOverlayNode();

            assertThat(chordNode.getKey()).isNotNull();
            assertThat(chordNode.getKey().getValue()).isEqualTo(node.getName());
            assertThat(chordNode.getKey().getHashing()).isEqualTo(new BigInteger(node.getHashing()));
        }

        world.setRing(ring);
    }

    @When("^I wait for stabilizing after (\\d+) seconds$")
    public void wating_for_stabilizing_after_seconds(int timeToStabilize) throws Throwable {
        Thread.sleep(timeToStabilize * 1000);
    }

    @Then("^Chord ring is stable with the following successors:$")
    public void chord_ring_is_stable_with_the_following_successors(Map<String, String> nodeSuccessors) throws Throwable {
        Ring ring = world.getRing();

        for (Node node : nodes) {
            DHashNode dHashNode = ring.getNode(node.getName());

            ChordNode chordNode = (ChordNode) dHashNode.getOverlayNode();

            assertThat(chordNode.getSuccessor()).isNotNull();
            assertThat(chordNode.getSuccessor().getValue()).isEqualTo(nodeSuccessors.get(node.getName()));
        }
    }

    @After
    public void destroyRing() throws StorageException {
        Ring ring = world.getRing();

        for (String nodeName: ring.getNodeNames()){
            DHashNode dHashNode = ring.getNode(nodeName);

            storageNodeFactory.destroyNode(dHashNode);
        }


    }
}
