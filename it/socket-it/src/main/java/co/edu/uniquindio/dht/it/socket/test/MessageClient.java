package co.edu.uniquindio.dht.it.socket.test;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageClient {
    private static final Logger logger = Logger
            .getLogger(MessageClient.class);
    private final MessageSerialization messageSerialization;
    private final Integer portTcp;

    public MessageClient(MessageSerialization messageSerialization, Integer portTcp) {
        this.messageSerialization = messageSerialization;
        this.portTcp = portTcp;
    }

    public Message send(Message request) {
        try (Socket socket = new Socket(request.getAddress().getDestination(), portTcp)) {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    socket.getOutputStream());
            objectOutputStream.writeObject(messageSerialization.encode(request));

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            String stringMessage = (String) objectInputStream.readObject();

            return messageSerialization.decode(stringMessage);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error writting socket", e);
        }

        return null;
    }
}
