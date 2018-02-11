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

import co.edu.uniquindio.utils.communication.message.MalformedMessageException;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;

/**
 * The <code>UnicastManagerUDP</code> class implemented transfer message based
 * in UDP
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class UnicastManagerUDP implements Communicator {
    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(UnicastManagerUDP.class);
    /**
     * Is the {@code DatagramSocket} used for sending and receiving messages.
     */
    private DatagramSocket datagramSocket;

    /**
     * Is the buffer used for the datagramSocket when reading a message.
     */
    private long bufferSize;

    /**
     * Stores the value of the port used for the UDP communication.
     */
    private int portUdp;

    public UnicastManagerUDP(int portUdp, long bufferUdp) {
        this.portUdp = portUdp;
        this.bufferSize = bufferUdp;

        try {
            this.datagramSocket = new DatagramSocket(portUdp);
        } catch (SocketException e) {
            logger.error("Error creating DatagramSocket", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * co.edu.uniquindio.utils.communication.transfer.Communicator#reciever()
     */
    public Message reciever() {
        DatagramPacket datagramPacket;
        String string;
        Message message;
        byte[] buffer;

        buffer = new byte[(int) bufferSize];

        datagramPacket = new DatagramPacket(buffer, buffer.length);

        try {
            datagramSocket.receive(datagramPacket);

            string = new String(datagramPacket.getData(), 0, datagramPacket
                    .getLength());

            message = MessageXML.valueOf(string);

            return message;
        } catch (IOException e) {
            logger.error("Error reading DatagramSocket", e);
        } catch (MalformedMessageException e) {
            logger.error("Error reading message", e);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * co.edu.uniquindio.utils.communication.transfer.Communicator#send(co.edu
     * .uniquindio.utils.communication.message.Message)
     */
    public void send(Message message) {
        InetAddress destinationAddress;
        DatagramPacket datagramPacket;
        String string;

        try {
            destinationAddress = InetAddress.getByName(message.getMessageDestination());

            string = message.toString();

            datagramPacket = new DatagramPacket(string.getBytes(), string
                    .length(), destinationAddress, portUdp);

            datagramSocket.send(datagramPacket);
        } catch (UnknownHostException e) {
            logger.error("Error writting DatagramSocket", e);
        } catch (IOException e) {
            logger.error("Error writting DatagramSocket", e);
        }
    }

    /**
     * Gets port UDP
     *
     * @return Port UDP
     */
    public int getPortUdp() {
        return portUdp;
    }

    /**
     * Sets por UDP
     *
     * @param portUdp Port UDP
     */
    public void setPortUdp(int portUdp) {
        this.portUdp = portUdp;
    }

    /*
     * (non-Javadoc)
     *
     * @see co.edu.uniquindio.utils.communication.transfer.Stoppable#stop()
     */
    public void stop() {
        datagramSocket.close();
    }

}
