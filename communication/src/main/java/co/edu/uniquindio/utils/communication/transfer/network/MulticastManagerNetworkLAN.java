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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import co.edu.uniquindio.utils.communication.message.MalformedMessageException;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.logger.LoggerDHT;

/**
 * The <code>MulticastManagerNetworkLAN</code> class implemented the transfer
 * messages on multicast UDP
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class MulticastManagerNetworkLAN implements Communicator {

	/**
	 * Logger
	 */
	private static final LoggerDHT logger = LoggerDHT
			.getLogger(MulticastManagerNetworkLAN.class);

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

	/**
	 * Builds a MulticastManagerNetworkLAN and started multicast socket
	 * 
	 * @param portMulticast
	 *            Port multicast
	 * @param group
	 *            Internet address multicast
	 * @param bufferSize
	 *            Buffer size for to reader
	 */
	public MulticastManagerNetworkLAN(int portMulticast, InetAddress group,
			long bufferSize) {
		try {
			this.portMulticast = portMulticast;

			this.multicastSocket = new MulticastSocket(portMulticast);

			this.group = group;

			this.multicastSocket.joinGroup(group);

			this.buffer = new byte[(int) bufferSize];
		} catch (IOException e) {
			logger.error("Error creating multicast socket", e);
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

		datagramPacket = new DatagramPacket(buffer, buffer.length);

		try {
			multicastSocket.receive(datagramPacket);

			string = new String(datagramPacket.getData(), 0, datagramPacket
					.getLength());

			message = MessageXML.valueOf(string);

			return message;
		} catch (IOException e) {
			logger.error("Error reading multicast socket", e);
		} catch (MalformedMessageException e) {
			logger.error("Error reading messages", e);
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
		DatagramPacket datagramPacket;
		String string = message.toString();

		datagramPacket = new DatagramPacket(string.getBytes(), string.length(),
				group, portMulticast);

		try {
			multicastSocket.send(datagramPacket);
		} catch (IOException e) {
			logger.error("Error writting multicast socket", e);
		}
	}

	/**
	 * Gets buffer size to reader
	 * 
	 * @return Buffer size
	 */
	public long getBufferSize() {
		return bufferSize;
	}

	/**
	 * Sets buffer size to reader
	 * 
	 * @param bufferSize
	 *            Buffer size
	 */
	public void setBufferSize(long bufferSize) {
		this.bufferSize = bufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see co.edu.uniquindio.utils.communication.transfer.Stoppable#stop()
	 */
	public void stop() {
		multicastSocket.close();
	}

}
