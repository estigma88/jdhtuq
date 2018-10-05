package co.edu.uniquindio.dht.it.socket.test;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class MessageClient {
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
            log.error("Error writting socket", e);
            throw new IllegalStateException("Error writting socket to: " + request.getAddress() + " in port: " + portTcp, e);
        }
    }
}
