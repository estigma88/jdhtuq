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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import co.edu.uniquindio.utils.communication.message.BigMessageXML;
import co.edu.uniquindio.utils.communication.message.MalformedMessageException;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.logger.LoggerDHT;
import org.apache.log4j.Logger;

/**
 * The <code>UnicastBigManagerTCP</code> class is an
 * <code>Communicator</code>. Implemented the communication for network on TCP
 * protocol.
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class UnicastBigManagerTCP implements Communicator {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerDHT
			.getLogger(UnicastBigManagerTCP.class);
	/**
	 * The server socket that will be waiting for connection.
	 */
	private ServerSocket serverSocket;

	/**
	 * The value of the port used to create the socket.
	 */
	private int portTcp;

	public UnicastBigManagerTCP(int portTcp) {
		this.portTcp = portTcp;

		try {
			this.serverSocket = new ServerSocket(portTcp);
		} catch (IOException e) {
			logger.error("The communication socket could not be created", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.edu.uniquindio.utils.communication.transfer.Communicator#reciever()
	 */
	public Message reciever() {
		Socket socket = null;
		String stringMessage;
		Message message = null;
		ObjectInputStream objectInputStream;

		try {
			socket = serverSocket.accept();

			objectInputStream = new ObjectInputStream(socket.getInputStream());
			stringMessage = (String) objectInputStream.readObject();

			if(stringMessage.contains("bigMessage")){
				message = BigMessageXML.valueOf(stringMessage);
			}else{
				message = MessageXML.valueOf(stringMessage);
			}

		} catch (IOException e) {
			logger.error("Error reading socket", e);
		} catch (ClassNotFoundException e) {
			logger.error("Error reading socket", e);
		} catch (MalformedMessageException e) {
			logger.error("Error reading message", e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error("Error closed socket", e);
			}
		}

		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.edu.uniquindio.utils.communication.transfer.Communicator#send(co.edu
	 * .uniquindio.utils.communication.message.Message)
	 */
	public void send(Message message) {

		Socket socket = null;
		try {
			socket = new Socket(message.getMessageDestination(), portTcp);

			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					socket.getOutputStream());
			objectOutputStream.writeObject(message.toString());
			objectOutputStream.flush();

		} catch (UnknownHostException e) {
			logger.error("Error sending message", e);
		} catch (IOException e) {
			logger.error("Error sending message", e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error("Error closed socket", e);
			}
		}

	}

	/**
	 * Gets port TCP
	 * 
	 * @return Port TCP
	 */
	public int getPortTcp() {
		return portTcp;
	}

	/**
	 * Sets port TCP
	 * 
	 * @param portTcp
	 *            Port TCP
	 */
	public void setPortTcp(int portTcp) {
		this.portTcp = portTcp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see co.edu.uniquindio.utils.communication.transfer.Stoppable#stop()
	 */
	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Error closed socked", e);
		}
	}

}
