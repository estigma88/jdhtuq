package co.edu.uniquindio.dht.it.socket.test.gets;

import co.edu.uniquindio.dht.it.socket.Protocol;
import co.edu.uniquindio.dht.it.socket.test.CucumberRoot;
import co.edu.uniquindio.dht.it.socket.test.MessageClient;
import co.edu.uniquindio.dht.it.socket.test.World;
import co.edu.uniquindio.dht.it.socket.test.put.Content;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetsDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;
    @Autowired
    private SequenceGenerator itSequenceGenerator;

    @Then("^I lookup the following resources:$")
    public void i_lookup_the_following_resources(List<Content> contents) throws Throwable {
        MessageClient messageClient = world.getRing().getNode(world.getNodeGateway());

        for (Content content : contents) {
            Message get = Message.builder()
                    .sequenceNumber(itSequenceGenerator.getSequenceNumber())
                    .sendType(Message.SendType.REQUEST)
                    .messageType(Protocol.GET)
                    .address(Address.builder()
                            .source("localhost")
                            .destination("localhost")
                            .build())
                    .param(Protocol.GetParams.RESOURCE_NAME.name(), content.getName())
                    .build();

            Message response = messageClient.send(get);

            assertThat(response).isNotNull();
            assertThat(response.getData(Protocol.GetResponseDatas.RESOURCE.name())).isNotNull();
            assertThat(response.getData(Protocol.GetResponseDatas.RESOURCE.name())).isEqualTo(content.getContent().getBytes());
        }
    }
}
