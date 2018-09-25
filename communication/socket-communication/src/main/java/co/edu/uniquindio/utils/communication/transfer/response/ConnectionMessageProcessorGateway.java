package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.ConnectionHandler;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import co.edu.uniquindio.utils.communication.transfer.MessageInputStreamProcessor;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Optional;

public class ConnectionMessageProcessorGateway implements ConnectionHandler {
    public static final String SENDING_INPUT_STREAM = ConnectionMessageProcessorGateway.class.getName() + ".sending_input_stream";

    private final MessageInputStreamProcessor messageInputStreamProcessor;
    private final MessageProcessor messageProcessor;
    private final MessageSerialization messageSerialization;

    public ConnectionMessageProcessorGateway(MessageInputStreamProcessor messageInputStreamProcessor, MessageProcessor messageProcessor, MessageSerialization messageSerialization) {
        this.messageInputStreamProcessor = messageInputStreamProcessor;
        this.messageProcessor = messageProcessor;
        this.messageSerialization = messageSerialization;
    }

    @Override
    public void handle(Socket socket) {
        try {
            Message message = readMessage(socket);

            Boolean sendingInputStream = Optional.ofNullable(message.getParam(SENDING_INPUT_STREAM))
                    .map(Boolean::new)
                    .orElse(false);

            if (sendingInputStream) {
                messageInputStreamProcessor.process(message, socket.getInputStream());
            } else {
                messageProcessor.process(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Message readMessage(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        String stringMessage = (String) objectInputStream.readObject();

        return messageSerialization.decode(stringMessage);
    }
}
