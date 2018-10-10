package co.edu.uniquindio.dht.it.socket.test.ring;

import co.edu.uniquindio.chord.starter.ChordProperties;
import co.edu.uniquindio.dhash.starter.DHashProperties;
import co.edu.uniquindio.dht.it.socket.Protocol;
import co.edu.uniquindio.dht.it.socket.test.CucumberRoot;
import co.edu.uniquindio.dht.it.socket.test.MessageClient;
import co.edu.uniquindio.dht.it.socket.test.SocketITProperties;
import co.edu.uniquindio.dht.it.socket.test.World;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RingDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;
    @Autowired
    private MessageSerialization messageSerialization;
    @Autowired
    private DHashProperties dHashProperties;
    @Autowired
    private ChordProperties chordProperties;
    @Autowired
    private SocketITProperties socketITProperties;
    @Autowired
    private IdGenerator itSequenceGenerator;
    private List<Node> nodes;

    @Given("^I use the \\\"([^\\\"]*)\\\" as a gateway$")
    public void i_use_the_node_as_a_gateway(String nodeGateway) throws Throwable {
        world.setNodeGateway(nodeGateway);
    }

    @Given("^I set the key length to (\\d+)$")
    public void key_length_is(int keyLength) throws Throwable {
        assertThat(keyLength).isEqualTo(chordProperties.getKeyLength());
    }

    @Given("^I have the following node's names and hashings:$")
    public void the_following_node_s_names(List<Node> nodes) throws Throwable {
        this.nodes = nodes;
    }

    @Given("^The \"([^\"]*)\" is offline$")
    public void the_is_offline(String node) throws Throwable {
        new ProcessBuilder("docker-compose", "kill", node).inheritIO().start().waitFor();
    }

    @Given("^The \"([^\"]*)\" left the network$")
    public void the_left_the_network(String node) throws Throwable {
        Ring ring = world.getRing();

        MessageClient messageClient = ring.getNode(node);

        Message leave = Message.builder()
                .id(itSequenceGenerator.newId())
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.LEAVE)
                .address(Address.builder()
                        .source("localhost")
                        .destination("localhost")
                        .build())
                .build();

        Message response = messageClient.send(leave);

        assertThat(response).isNotNull();
        assertThat(response.getParam(Protocol.PutResponseParams.MESSAGE.name())).isEqualTo("OK");
    }

    @Given("^The \"([^\"]*)\" is not started$")
    public void the_is_stopped(String node) throws Throwable {
        new ProcessBuilder("docker-compose", "stop", node).inheritIO().start().waitFor();
    }

    @Given("^The \"([^\"]*)\" is added to the network$")
    public void the_is_added_to_the_network(String node) throws Throwable {
        new ProcessBuilder("docker-compose", "start", node).inheritIO().start().waitFor();

        addNode(world.getRing(), node);
    }

    @When("^I create the Chord ring$")
    public void chord_ring_is_created() throws Throwable {
        destroyRing();

        Ring ring = new Ring();
        for (Node node : nodes) {
            addNode(ring, node.getName());
        }

        world.setRing(ring);

        new ProcessBuilder("docker-compose", "build").inheritIO().start().waitFor();
        new ProcessBuilder("docker-compose", "up").inheritIO().start();
    }

    private void addNode(Ring ring, String node) {
        MessageClient messageClient = new MessageClient(messageSerialization, socketITProperties.getPortBy(node));

        ring.add(node, messageClient);
    }

    @When("^I wait for stabilizing after (\\d+) seconds$")
    public void wating_for_stabilizing_after_seconds(int timeToStabilize) throws Throwable {
        Thread.sleep(timeToStabilize * 1000);
    }

    @Then("^Chord ring is stable with the following successors:$")
    public void chord_ring_is_stable_with_the_following_successors(Map<String, String> nodeSuccessors) throws Throwable {
        Ring ring = world.getRing();

        for (String node : nodeSuccessors.keySet()) {
            MessageClient messageClient = ring.getNode(node);

            Message getSuccessor = Message.builder()
                    .id(itSequenceGenerator.newId())
                    .sendType(Message.SendType.REQUEST)
                    .messageType(Protocol.GET_SUCCESSOR)
                    .address(Address.builder()
                            .source("localhost")
                            .destination("localhost")
                            .build())
                    .build();

            Message response = messageClient.send(getSuccessor);

            String successor = response.getParam(Protocol.GetSuccessorResponseParams.SUCCESSOR.name());

            assertThat(successor).isNotNull();
            assertThat(successor).isEqualTo(nodeSuccessors.get(node));
        }

    }

    @Before
    public void copyResources() throws IOException {
        Path resourcesSource = Paths.get("src/ittest/resources/resources");
        Path resourcesDestination = Paths.get(socketITProperties.getDhash().getResourceDirectory(), "resources");

        Files.copy(resourcesSource, resourcesDestination);
    }

    @After
    public void destroyRing() throws StorageException, IOException, InterruptedException {
        world.setRing(null);

        new ProcessBuilder("docker-compose", "down", "--rmi", "all").inheritIO().start().waitFor();

        FileUtils.deleteDirectory(new File(socketITProperties.getDhash().getResourceDirectory()));
    }
}
