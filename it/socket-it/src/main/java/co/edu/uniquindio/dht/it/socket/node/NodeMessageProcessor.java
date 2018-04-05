package co.edu.uniquindio.dht.it.socket.node;

import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

public class NodeMessageProcessor implements MessageProcessor{
    private final StorageNode storageNode;
    public NodeMessageProcessor(StorageNode storageNode) {
        this.storageNode = storageNode;
    }

    @Override
    public Message process(Message request) {
        return null;
    }
}
