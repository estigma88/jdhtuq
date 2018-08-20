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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;

/**
 * The <code>MulticastManagerUDP</code> class implemented the transfer
 * messages on multicast UDP
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class MulticastManagerUDP implements Communicator {

    /**
     * Properties for configuration CommunicationManagerNetworkLAN
     *
     * @author Daniel Pelaez
     * @author Hector Hurtado
     * @author Daniel Lopez
     * @version 1.0, 17/06/2010
     * @since 1.0
     */
    public enum CommunicationManagerNetworkLANProperties {
        BUFFER_SIZE_MULTICAST, IP_MULTICAST, PORT_MULTICAST
    }

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(MulticastManagerUDP.class);

    /**
     * Is the size of the buffer used for receiving messages.
     */
    private long bufferSize = 1024;

    /**
     * Is the {@code MulticastSocket} used for sending and receiving messages.
     */
    private MulticastSocket multicastSocket;

    /**
     * Is the group that will be communicating by multicast.
     */
    private InetAddress group;

    /**
     * Is the buffer used for a DatagramPacket when reading a message.
     */
    private byte[] buffer;

    /**
     * Stores the value of the port used for the UDP Multicast communication.
     */
    private int portMulticast;


    private final MessageSerialization messageSerialization;

    /**
     * Builds a MulticastManagerUDP and started multicast socket
     *
     * @param messageSerialization
     */
    public MulticastManagerUDP(MessageSerialization messageSerialization) {
        this.messageSerialization = messageSerialization;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * co.edu.uniquindio.utils.communication.transfer.Communicator#receiver()
     */
    public Message receiver() {
        DatagramPacket datagramPacket;
        String string;
        Message message;

        datagramPacket = new DatagramPacket(buffer, buffer.length);

        try {
            multicastSocket.receive(datagramPacket);

            string = new String(datagramPacket.getData(), 0, datagramPacket
                    .getLength());

            message = messageSerialization.decode(string);

            return message;
        } catch (IOException e) {
            logger.error("Error reading multicast socket", e);
        }

        return null;
    }

    @Override
    public void start(Map<String, String> properties) {
        try {
            if (properties
                    .containsKey(CommunicationManagerNetworkLANProperties.PORT_MULTICAST
                            .name())) {
                portMulticast = Integer
                        .parseInt(properties
                                .get(CommunicationManagerNetworkLANProperties.PORT_MULTICAST
                                        .name()));
            } else {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                        "Property PORT_MULTICAST not found");

                logger.error("Property PORT_MULTICAST no found",
                        illegalArgumentException);

                throw illegalArgumentException;
            }

            if (properties
                    .containsKey(CommunicationManagerNetworkLANProperties.IP_MULTICAST
                            .name())) {
                group = InetAddress.getByName(properties
                        .get(CommunicationManagerNetworkLANProperties.IP_MULTICAST
                                .name()));
            } else {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                        "Property IP_MULTICAST not found");

                logger.error("Property IP_MULTICAST no found",
                        illegalArgumentException);

                throw illegalArgumentException;
            }

            if (properties
                    .containsKey(CommunicationManagerNetworkLANProperties.BUFFER_SIZE_MULTICAST
                            .name())) {
                bufferSize = Long
                        .parseLong(properties
                                .get(CommunicationManagerNetworkLANProperties.BUFFER_SIZE_MULTICAST
                                        .name()));
            } else {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                        "Property BUFFER_SIZE_MULTICAST not found");

                logger.error("Property BUFFER_SIZE_MULTICAST no found",
                        illegalArgumentException);

                throw illegalArgumentException;
            }

            this.portMulticast = portMulticast;

            this.multicastSocket = new MulticastSocket(portMulticast);

            this.group = group;

            this.multicastSocket.joinGroup(group);

            this.buffer = new byte[(int) bufferSize];
        } catch (IOException e) {
            logger.error("Error creating multicast socket", e);
            throw new IllegalStateException("Error creating multicast socket", e);
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
        DatagramPacket datagramPacket;
        String string = messageSerialization.encode(message);

        datagramPacket = new DatagramPacket(string.getBytes(), string.length(),
                group, portMulticast);

        try {
            multicastSocket.send(datagramPacket);
        } catch (IOException e) {
            logger.error("Error writting multicast socket", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see co.edu.uniquindio.utils.communication.transfer.Stoppable#close()
     */
    public void close() {
        multicastSocket.close();
    }

}
