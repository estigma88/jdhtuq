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

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.communication.transfer.ReceiverMessageCommand;
import co.edu.uniquindio.utils.communication.transfer.Responder;
import co.edu.uniquindio.utils.communication.transfer.Stoppable;

/**
 * The <code>MessagesReciever</code> class is an
 * <code>Observable<Message></code> and <code>Stoppable</code>. Implemented all
 * for to reciever messages from three diferent sources
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class MessagesReciever extends Observable<Message> implements Stoppable {
	/**
	 * Running recieving
	 */
	private boolean run;
	/**
	 * Reciever thread for unicast
	 */
	private RecieverThreadMessage recieverUnicastMessage;
	/**
	 * Reciever thread for multicast
	 */
	private RecieverThreadMessage recieverMulticastMessage;
	/**
	 * Reciever thread for transfer bytes
	 */
	private RecieverThreadMessage recieverTransferBytesMessage;

	/**
	 * Responder message manager
	 */
	private Responder responder;

	/**
	 * Builds a MessagesReciever. Creates three
	 * <code>RecieverThreadMessage</code> and starts
	 * 
	 * @param multicastManager
	 *            Communicator for multicast
	 * @param unicastManager
	 *            Communicator for unicast
	 * @param transferBytesManager
	 *            Communicator for transfer bytes
	 */
	public MessagesReciever(Communicator multicastManager,
			Communicator unicastManager, Communicator transferBytesManager,
			Responder responder) {
		super();
		this.run = true;

		if (unicastManager != null) {
			recieverUnicastMessage = new RecieverThreadMessage(unicastManager);
			recieverUnicastMessage.receptionStart();
		}

		if (multicastManager != null) {
			recieverMulticastMessage = new RecieverThreadMessage(
					multicastManager);
			recieverMulticastMessage.receptionStart();
		}

		if (transferBytesManager != null) {
			recieverTransferBytesMessage = new RecieverThreadMessage(
					transferBytesManager);
			recieverTransferBytesMessage.receptionStart();
		}

		this.responder = responder;
	}

	/**
	 * Stopped
	 */
	public void stop() {
		run = false;
	}

	/**
	 * Creates a <code>ReceiverMessageCommand</code>.
	 * 
	 * @param message
	 *            Message to notify
	 */
	private void createRecieverThread(Message message) {

		ReceiverMessageCommand receiverMessageCommand;
		receiverMessageCommand = new ReceiverMessageCommand(message, this);

		receiverMessageCommand.execute();
	}

	/**
	 * The <code>RecieverThreadMessage</code> class reads message from a
	 * Communicator and sending to destination.
	 * 
	 * @author Daniel Pelaez
	 * @version 1.0, 17/06/2010
	 * @since 1.0
	 * 
	 */
	class RecieverThreadMessage implements Runnable {
		/**
		 * Communicator
		 */
		private Communicator communicator;

		/**
		 * Builds an RecieverThreadMessage
		 * 
		 * @param communicator
		 *            Communicator
		 */
		RecieverThreadMessage(Communicator communicator) {
			this.communicator = communicator;
		}

		/**
		 * Start reception
		 */
		public void receptionStart() {
			Thread thread = new Thread(this);
			thread.start();
		}

		/**
		 * Read message an invoke to <code>createRecieverThread(message)</code>
		 */
		public void run() {
			Message message;
			while (run) {
				message = communicator.reciever();

				if (!responder.releaseResponse(message)) {

					createRecieverThread(message);
				}
			}
		}
	}

}
