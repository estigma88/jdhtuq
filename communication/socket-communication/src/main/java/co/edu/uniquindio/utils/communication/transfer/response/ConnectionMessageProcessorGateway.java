package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.ConnectionHandler;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Optional;

@Slf4j
public class ConnectionMessageProcessorGateway implements ConnectionHandler {
    public static final String HANDLE_STREAMS = ConnectionMessageProcessorGateway.class.getName() + ".handle_streams";

    private final MessageStreamProcessor messageStreamProcessor;
    private final MessageProcessor messageProcessor;
    private final MessageSerialization messageSerialization;

    public ConnectionMessageProcessorGateway(MessageStreamProcessor messageStreamProcessor, MessageProcessor messageProcessor, MessageSerialization messageSerialization) {
        this.messageStreamProcessor = messageStreamProcessor;
        this.messageProcessor = messageProcessor;
        this.messageSerialization = messageSerialization;
    }

    @Override
    public void handle(Socket connection) {
        try (Socket socket = connection){
            Message message = readMessage(socket);

            Boolean sendingInputStream = Optional.ofNullable(message.getParam(HANDLE_STREAMS))
                    .map(Boolean::new)
                    .orElse(false);

            if (sendingInputStream) {
                messageStreamProcessor.process(message, socket.getInputStream(), socket.getOutputStream());
            } else {
                messageProcessor.process(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error processing connection", e);
        }
    }

    Message readMessage(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        String stringMessage = (String) objectInputStream.readObject();

        return messageSerialization.decode(stringMessage);
    }
}
