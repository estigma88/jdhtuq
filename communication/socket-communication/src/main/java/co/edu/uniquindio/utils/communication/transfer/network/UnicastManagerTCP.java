/*
 *  Communication project implement communication point to point and multicast
 *  Copyright (C) 2010  Daniel Pelaez, Daniel Lopez, Hector Hurtado
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.communication.transfer.ConnectionListener;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static co.edu.uniquindio.utils.communication.transfer.response.ConnectionMessageProcessorGateway.HANDLE_STREAMS;

/**
 * The <code>UnicastManagerTCP</code> class implemented transfer message based
 * in TCP
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class UnicastManagerTCP implements Communicator, ConnectionListener {

    /**
     * The <code>UnicastManagerTCPProperties</code> enum contains params
     * required for communication
     *
     * @author dpelaez
     */
    public enum UnicastManagerTCPProperties {
        TIMEOUT_TCP_CONNECTION, PORT_TCP, SIZE_BUFFER
    }

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(UnicastManagerTCP.class);

    /**
     * The server socket that will be waiting for connection.
     */
    private ServerSocket serverSocket;

    /**
     * The value of the port used to create the socket.
     */
    private int portTcp;
    private int timeoutTcpConnection;
    private int sizeBuffer;
    private final MessageSerialization messageSerialization;

    /**
     * Builds a UnicastManagerTCP
     *
     * @param messageSerialization
     */
    public UnicastManagerTCP(MessageSerialization messageSerialization) {
        this.messageSerialization = messageSerialization;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * co.edu.uniquindio.utils.communication.transfer.Communicator#receiver()
     */
    public Message receiver() {
        String stringMessage;
        Message message = null;

        try (Socket socket = listen();
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            stringMessage = (String) objectInputStream.readObject();

            message = messageSerialization.decode(stringMessage);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error reading socket", e);
        }

        return message;
    }

    @Override
    public Socket listen() throws IOException {
        return serverSocket.accept();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * co.edu.uniquindio.utils.communication.transfer.Communicator#send(co.edu
     * .uniquindio.utils.communication.message.Message)
     */
    public void send(Message message) {
        try (Socket socket = new Socket()) {
            send(message, socket);
        } catch (IOException e) {
            logger.error("Error writing socket " + message.getAddress(), e);
        }

    }

    @Override
    public MessageStream receive(Message message) {
        try {
            Socket socket = new Socket();

            message = Message.with(message)
                    .param(HANDLE_STREAMS, String.valueOf(true))
                    .build();

            send(message, socket);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            String stringMessage = (String) objectInputStream.readObject();

            Message messageResponse = messageSerialization.decode(stringMessage);

            return MessageStream.builder()
                    .message(messageResponse)
                    .inputStream(socket.getInputStream())
                    .build();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error writing socket " + message.getAddress(), e);
            return null;
        }
    }

    @Override
    public void send(Message message, InputStream inputStream) {
        try (Socket socket = new Socket()) {
            message = Message.with(message)
                    .param(HANDLE_STREAMS, String.valueOf(true))
                    .build();

            send(message, socket);
            send(socket, inputStream);
        } catch (IOException e) {
            logger.error("Error writing socket " + message.getAddress(), e);
        }
    }

    @Override
    public void send(Message message, OutputStream destination) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    destination);
            objectOutputStream.writeObject(messageSerialization.encode(message));
        } catch (IOException e) {
            logger.error("Error writing socket " + message.getAddress(), e);
        }
    }

    private void send(Message message, Socket socket) throws IOException {
        socket.connect(new InetSocketAddress(message.getAddress().getDestination(), portTcp), timeoutTcpConnection);

        send(message, socket.getOutputStream());
    }

    private void send(Socket socket, InputStream source) throws IOException {
        OutputStream destination = socket.getOutputStream();

        send(source, destination);
    }

    public void send(InputStream source, OutputStream destination) throws IOException {
        int count;
        byte[] buffer = new byte[sizeBuffer];
        while ((count = source.read(buffer)) > 0) {
            destination.write(buffer, 0, count);
        }
        source.close();
        destination.close();
    }

    @Override
    public void start(Map<String, String> properties) {
        if (properties
                .containsKey(UnicastManagerTCPProperties.PORT_TCP.name())) {
            portTcp = Integer.parseInt(properties
                    .get(UnicastManagerTCPProperties.PORT_TCP.name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property PORT_TCP not found");

            logger.error("Property PORT_TCP not found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }
        if (properties
                .containsKey(UnicastManagerTCPProperties.TIMEOUT_TCP_CONNECTION.name())) {
            timeoutTcpConnection = Integer.parseInt(properties
                    .get(UnicastManagerTCPProperties.TIMEOUT_TCP_CONNECTION.name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property TIMEOUT_TCP_CONNECTION not found");

            logger.error("Property TIMEOUT_TCP_CONNECTION not found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }

        if (properties
                .containsKey(UnicastManagerTCPProperties.SIZE_BUFFER.name())) {
            sizeBuffer = Integer.parseInt(properties
                    .get(UnicastManagerTCPProperties.SIZE_BUFFER.name()));
        } else {
            /*IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property SIZE_BUFFER not found");

            logger.error("Property SIZE_BUFFER not found",
                    illegalArgumentException);

            throw illegalArgumentException;*/
            sizeBuffer = 2048;
        }
        try {
            this.serverSocket = new ServerSocket(portTcp);
        } catch (IOException e) {
            logger.error("Error creating server socket", e);
            throw new IllegalStateException("Error creating server socket", e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see co.edu.uniquindio.utils.communication.transfer.Stoppable#close()
     */
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Error closing server socket", e);
            throw new IllegalStateException("Error closing server socket", e);
        }
    }

}
