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
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.communication.transfer.ConnectionListener;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

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
        TIMEOUT_TCP_CONNECTION, PORT_TCP
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
     * @see
     * co.edu.uniquindio.utils.communication.transfer.Communicator#send(co.edu
     * .uniquindio.utils.communication.message.Message)
     */
    public void send(Message message) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(message.getAddress().getDestination(), portTcp), timeoutTcpConnection);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    socket.getOutputStream());
            objectOutputStream.writeObject(messageSerialization.encode(message));

        } catch (IOException e) {
            logger.error("Error writing socket " + message.getAddress(), e);
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
