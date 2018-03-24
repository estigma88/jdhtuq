package co.edu.uniquindio.dht.it.datastructure.ring;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.starter.DHashProperties;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
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
    @Autowired
    private CommunicationManager communicationManagerChord;
    @Autowired
    private CommunicationManager communicationManagerDHash;
    @Autowired
    private DHashProperties dHashProperties;
    private List<Node> nodes;

    @Given("^I use the \\\"([^\\\"]*)\\\" as a gateway$")
    public void i_use_the_node_as_a_gateway(String nodeGateway) throws Throwable {
        world.setNodeGateway(nodeGateway);
    }

    @Given("^I set the key length to (\\d+)$")
    public void key_length_is(int keyLength) throws Throwable {
        keyFactory.updateKeyLength(keyLength);
    }

    @Given("^I have the following node's names and hashings:$")
    public void the_following_node_s_names(List<Node> nodes) throws Throwable {
        this.nodes = nodes;
    }

    @Given("^The \"([^\"]*)\" is offline$")
    public void the_is_offline(String node) throws Throwable {
        Ring ring = world.getRing();

        DHashNode dHashNode = ring.getNode(node);

        ChordNode chordNode = (ChordNode) dHashNode.getOverlayNode();

        chordNode.stopStabilizing();

        communicationManagerChord.removeMessageProcessor(node);
        communicationManagerDHash.removeMessageProcessor(node);
    }

    @Given("^The \"([^\"]*)\" left the network$")
    public void the_left_the_network(String node) throws Throwable {
        Ring ring = world.getRing();

        DHashNode dHashNode = ring.getNode(node);

        dHashNode.leave();
    }

    @Given("^The \"([^\"]*)\" is added to the network$")
    public void the_is_added_to_the_network(String node) throws Throwable {
        Ring ring = world.getRing();

        ring.add(node, (DHashNode) storageNodeFactory.createNode(node));
    }

    @When("^I create the Chord ring$")
    public void chord_ring_is_created() throws Throwable {
        Ring ring = new Ring();

        for (Node node : nodes) {
            ring.add(node.getName(), (DHashNode) storageNodeFactory.createNode(node.getName()));
        }

        for (Node node : nodes) {
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

        for (String node : nodeSuccessors.keySet()) {
            DHashNode dHashNode = ring.getNode(node);

            ChordNode chordNode = (ChordNode) dHashNode.getOverlayNode();

            assertThat(chordNode.getSuccessor()).isNotNull();
            assertThat(chordNode.getSuccessor().getValue()).isEqualTo(nodeSuccessors.get(node));
        }
    }

    @After
    public void destroyRing() throws StorageException, IOException {
        Ring ring = world.getRing();

        for (String nodeName : ring.getNodeNames()) {
            DHashNode dHashNode = ring.getNode(nodeName);

            ChordNode chordNode = (ChordNode) dHashNode.getOverlayNode();

            chordNode.stopStabilizing();

            communicationManagerChord.removeMessageProcessor(nodeName);
            communicationManagerDHash.removeMessageProcessor(nodeName);
        }

        world.setRing(null);

        FileUtils.deleteDirectory(new File(dHashProperties.getResourceDirectory()));
    }
}
