package co.edu.uniquindio.dht.it.integration.node;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageServer {
    private MessageProcessor messageProcessor;

    public MessageServer(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @PostMapping(value = "/jdhtuq/messages")
    public Message process(@RequestBody Message request) {
        return messageProcessor.process(request);
    }
}
