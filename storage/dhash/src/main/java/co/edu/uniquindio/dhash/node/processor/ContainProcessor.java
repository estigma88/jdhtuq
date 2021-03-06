package co.edu.uniquindio.dhash.node.processor;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContainProcessor implements MessageProcessor {
    private final DHashNode dHashNode;
    private final ResourceManager resourceManager;

    ContainProcessor(DHashNode dHashNode, ResourceManager resourceManager) {
        this.dHashNode = dHashNode;
        this.resourceManager = resourceManager;
    }

    @Override
    public Message process(Message message) {
        Message.MessageBuilder containResponseMessage = Message.builder()
                .id(message.getId())
                .sendType(Message.SendType.RESPONSE)
                .messageType(Protocol.CONTAIN_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(dHashNode.getName())
                        .build());

        if (resourceManager.hasResource(
                message.getParam(Protocol.ContainParams.RESOURCE_KEY.name()))) {

            containResponseMessage.param(Protocol.ContainResponseParams.HAS_RESOURCE.name(),
                    String.valueOf(true));

        } else {

            containResponseMessage.param(Protocol.ContainResponseParams.HAS_RESOURCE.name(),
                    String.valueOf(false));

        }

        log.debug("Node " + dHashNode.getName() + ", confirmation for ");
        log.debug("Response message: [" + containResponseMessage.toString()
                + "]");

        return containResponseMessage.build();
    }
}
