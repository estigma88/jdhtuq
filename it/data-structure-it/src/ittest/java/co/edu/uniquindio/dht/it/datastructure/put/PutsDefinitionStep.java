package co.edu.uniquindio.dht.it.datastructure.put;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.dhash.starter.DHashProperties;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.dht.it.datastructure.ring.Ring;
import co.edu.uniquindio.storage.StorageNode;
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
    private Map<String, Content> contents;

    @Given("^I have the resources names and values:$")
    public void i_have_the_resources_names_and_values(List<Content> contents) throws Throwable {
        this.contents = contents.stream()
                .collect(Collectors.toMap(Content::getName, Function.identity()));
    }

    @When("^I put resources into the network$")
    public void i_put_resources_into_the_network() throws Throwable {
        Ring ring = world.getRing();

        StorageNode storageNode = ring.getNode(world.getNodeGateway());

        for (String contentName : contents.keySet()) {
            storageNode.put(new BytesResource(contentName, contents.get(contentName).getContent().getBytes()));
        }

    }

    @Then("^The resources are put in the following nodes:$")
    public void the_resources_are_put_in_the_following_nodes(Map<String, String> nodesByResource) throws Throwable {
        for (String contentName : nodesByResource.keySet()) {
            String[] nodes = nodesByResource.get(contentName).split(",");

            for (String node : nodes) {
                Path resourcePath = Paths.get(dHashProperties.getResourceDirectory() + node + "/" + contentName);

                File resource = resourcePath.toFile();

                assertThat(resource.exists()).isTrue();

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                Files.copy(resourcePath, os);

                assertThat(contents.get(contentName).getContent()).isEqualTo(os.toString());
            }
        }
    }
}
