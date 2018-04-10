package co.edu.uniquindio.dht.it.socket.test.gets;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.dht.it.socket.test.CucumberRoot;
import co.edu.uniquindio.dht.it.socket.test.MessageClient;
import co.edu.uniquindio.dht.it.socket.test.World;
import co.edu.uniquindio.dht.it.socket.test.put.Content;
import co.edu.uniquindio.storage.StorageNode;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetsDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;

    @Then("^I lookup the following resources:$")
    public void i_lookup_the_following_resources(List<Content> contents) throws Throwable {
        MessageClient messageClient = world.getRing().getNode(world.getNodeGateway());

        for (Content content : contents) {
            /*BytesResource resource = (BytesResource) messageClient.get(content.getName());

            assertThat(resource).isNotNull();
            assertThat(resource.getBytes()).isEqualTo(content.getContent().getBytes());*/
        }
    }
}
