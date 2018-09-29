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
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;

/**
 * The <code>MulticastManagerUDP</code> class implemented the transfer
 * messages on multicast UDP
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class MulticastManagerUDP implements Communicator {

    public enum MulticastManagerUDPProperties {
        BUFFER_SIZE_MULTICAST, IP_MULTICAST, PORT_MULTICAST
    }

    private static final Logger logger = Logger
            .getLogger(MulticastManagerUDP.class);

    private MulticastSocket multicastSocket;
    private InetAddress group;
    private byte[] buffer;
    private int portMulticast;
    private final MessageSerialization messageSerialization;

    MulticastManagerUDP(MessageSerialization messageSerialization) {
        this.messageSerialization = messageSerialization;
    }

    @Override
    public Message receive() {
        DatagramPacket datagramPacket;
        String string;
        Message message = null;

        datagramPacket = new DatagramPacket(buffer, buffer.length);

        try {
            multicastSocket.receive(datagramPacket);

            string = new String(datagramPacket.getData(), 0, datagramPacket
                    .getLength());

            message = messageSerialization.decode(string);
        } catch (IOException e) {
            logger.error("Error reading multicast socket", e);
        }

        return message;
    }

    @Override
    public void send(Message message) {
        DatagramPacket datagramPacket;
        String string = messageSerialization.encode(message);

        datagramPacket = new DatagramPacket(string.getBytes(), string.length(),
                group, portMulticast);

        try {
            multicastSocket.send(datagramPacket);
        } catch (IOException e) {
            logger.error("Error writing multicast socket", e);
        }
    }

    @Override
    public void start(Map<String, String> properties) {
        try {
            portMulticast = Optional.ofNullable(properties.get(MulticastManagerUDPProperties.PORT_MULTICAST.name().toLowerCase()))
                    .map(Integer::parseInt)
                    .orElseThrow(() -> new IllegalArgumentException("Property port_multicast not found"));

            group = Optional.ofNullable(properties.get(MulticastManagerUDPProperties.IP_MULTICAST.name().toLowerCase()))
                    .map(ip -> {
                        try {
                            return InetAddress.getByName(ip);
                        } catch (UnknownHostException e) {
                            throw new IllegalArgumentException("Problem wih ip_multicast property", e);
                        }
                    })
                    .orElseThrow(() -> new IllegalArgumentException("Property ip_multicast not found"));

            Integer bufferSize = Optional.ofNullable(properties.get(MulticastManagerUDPProperties.BUFFER_SIZE_MULTICAST.name().toLowerCase()))
                    .map(Integer::parseInt)
                    .orElseThrow(() -> new IllegalArgumentException("Property buffer_size_multicast not found"));

            this.multicastSocket = new MulticastSocket(portMulticast);

            this.multicastSocket.joinGroup(group);

            this.buffer = new byte[bufferSize];
        } catch (IOException e) {
            logger.error("Error creating multicast socket", e);
            throw new IllegalStateException("Error creating multicast socket", e);
        }
    }

    @Override
    public void close() {
        multicastSocket.close();
    }

}
