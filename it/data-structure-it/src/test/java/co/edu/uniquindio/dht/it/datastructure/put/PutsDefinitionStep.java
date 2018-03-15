package co.edu.uniquindio.dht.it.datastructure.put;

import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.dht.it.datastructure.ring.Ring;
import co.edu.uniquindio.storage.StorageNode;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PutsDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;
    @Autowired
    private HashingGenerator hashingGenerator;
    private List<Content> contents;
    private String nodeGateway;

    @Given("^I have the resources names and values:$")
    public void i_have_the_resources_names_and_values(List<Content> contents) throws Throwable {
        this.contents = contents;
    }

    @Given("^I use the \\\"([^\\\"]*)\\\" as a gateway$")
    public void i_use_the_node_as_a_gateway(String nodeGateway) throws Throwable {
        this.nodeGateway = nodeGateway;
    }

    @When("^I put resources into the network$")
    public void i_put_resources_into_the_network() throws Throwable {
        Ring ring = world.getRing();

        StorageNode storageNode = ring.getNode(nodeGateway);

        for (Content content : contents) {
            System.out.println(content.getName() + ": " + hashingGenerator.generateHashing(content.getName(), 16));

            storageNode.put(new BytesResource(content.getName(), content.getContent().getBytes()));
        }

    }

    @Then("^The resources are put in the following nodes:$")
    public void the_resources_are_put_in_the_following_nodes(List<Content> contents) throws Throwable {

    }
}
