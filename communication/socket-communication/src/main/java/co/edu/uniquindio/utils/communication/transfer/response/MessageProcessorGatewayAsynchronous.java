package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class MessageProcessorGatewayAsynchronous implements MessageProcessor, Closeable {
    private final MessageProcessor messageProcessor;
    private final ExecutorService executorService;

    public MessageProcessorGatewayAsynchronous(MessageProcessor messageProcessor, ExecutorService executorService) {
        this.messageProcessor = messageProcessor;
        this.executorService = executorService;
    }

    @Override
    public Message process(Message message) {
        if (!executorService.isShutdown()) {
            executorService.execute(() -> messageProcessor.process(message));
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        executorService.shutdown();
    }
}
