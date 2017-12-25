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

package co.edu.uniquindio.utils.communication.transfer.structure;

import co.edu.uniquindio.utils.communication.message.BigMessage;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.BytesTransfer;

/**
 * The <code>TransferObjectManagerStructure</code> class is an
 * <code>BytesTransfer</code> Implement all transfer for data structure
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class TransferObjectManagerStructure implements BytesTransfer {

	/**
	 * Reference to CommunicationDataStructure
	 */
	private CommunicationDataStructure communicationDataStructure;

	/**
	 * Builds a TransferObjectManagerStructure
	 * 
	 * @param communicationDataStructure
	 */
	public TransferObjectManagerStructure(
			CommunicationDataStructure communicationDataStructure) {
		this.communicationDataStructure = communicationDataStructure;
	}

	/**
	 * Send message for CommunicationDataStructure
	 */
	public void send(Message message) {
		communicationDataStructure.notifyUnicast(message);
	}

	/**
	 * Send big message for CommunicationDataStructure
	 */
	public void send(BigMessage message) {
		communicationDataStructure.notifyUnicast(message);
	}

	/**
	 * Not implemented
	 */
	public Message reciever() {
		return null;
	}

	/**
	 * Not implemented
	 */
	public void stop() {

	}

}
