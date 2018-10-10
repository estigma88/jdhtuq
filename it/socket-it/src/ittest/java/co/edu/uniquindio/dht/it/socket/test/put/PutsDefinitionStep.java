package co.edu.uniquindio.dht.it.socket.test.put;

import co.edu.uniquindio.dhash.starter.DHashProperties;
import co.edu.uniquindio.dht.it.socket.Protocol;
import co.edu.uniquindio.dht.it.socket.test.CucumberRoot;
import co.edu.uniquindio.dht.it.socket.test.MessageClient;
import co.edu.uniquindio.dht.it.socket.test.SocketITProperties;
import co.edu.uniquindio.dht.it.socket.test.World;
import co.edu.uniquindio.dht.it.socket.test.ring.Ring;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.message.Message;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PutsDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;
    @Autowired
    private DHashProperties dHashProperties;
    @Autowired
    private SocketITProperties socketITProperties;
    @Autowired
    private IdGenerator itSequenceGenerator;
    private Map<String, Content> contents;

    @Given("^I have the resources names and values:$")
    public void i_have_the_resources_names_and_values(List<Content> contents) throws Throwable {
        this.contents = contents.stream()
                .collect(Collectors.toMap(Content::getName, Function.identity()));
    }

    @When("^I put resources into the network$")
    public void i_put_resources_into_the_network() throws Throwable {
        Ring ring = world.getRing();

        MessageClient messageClient = ring.getNode(world.getNodeGateway());

        for (String contentName : contents.keySet()) {
            Message put = Message.builder()
                    .id(itSequenceGenerator.newId())
                    .sendType(Message.SendType.REQUEST)
                    .messageType(Protocol.PUT)
                    .address(Address.builder()
                            .source("localhost")
                            .destination("localhost")
                            .build())
                    .param(Protocol.PutParams.RESOURCE_NAME.name(), contentName)
                    .param(Protocol.PutDatas.RESOURCE.name(), contents.get(contentName).getPath())
                    .build();

            Message response = messageClient.send(put);

            assertThat(response).isNotNull();
            assertThat(response.getParam(Protocol.PutResponseParams.MESSAGE.name())).isEqualTo("OK");
        }

    }

    @Then("^The resources are put in the following nodes:$")
    public void the_resources_are_put_in_the_following_nodes(Map<String, String> nodesByResource) throws Throwable {
        for (String contentName : nodesByResource.keySet()) {
            String[] nodes = nodesByResource.get(contentName).split(",");

            for (String node : nodes) {
                Path resourcePath = Paths.get(dHashProperties.getResourceDirectory(), node, contentName, contentName);

                assertThat(Files.exists(resourcePath)).isTrue();

                assertThat(Files.readAllLines(Paths.get(contents.get(contentName).getPath()))).isEqualTo(Files.readAllLines(resourcePath));
            }
        }
    }
}
