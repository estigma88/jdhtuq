/*
 *  DHash project implement a storage management 
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

package co.edu.uniquindio.dhash.node;

import java.io.IOException;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.protocol.Protocol.GetParams;
import co.edu.uniquindio.dhash.protocol.Protocol.GetResponseParams;
import co.edu.uniquindio.dhash.protocol.Protocol.PutDatas;
import co.edu.uniquindio.dhash.protocol.Protocol.PutParams;
import co.edu.uniquindio.dhash.protocol.Protocol.ResourceCompareParams;
import co.edu.uniquindio.dhash.protocol.Protocol.ResourceCompareResponseParams;
import co.edu.uniquindio.dhash.protocol.Protocol.ResourceTransferParams;
import co.edu.uniquindio.dhash.protocol.Protocol.ResourceTransferResponseData;
import co.edu.uniquindio.dhash.resource.ResourceAlreadyExistException;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.storage.resource.SerializableResource;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.BigMessage;
import co.edu.uniquindio.utils.communication.message.BigMessageXML;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.message.Message.SendType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerCache;
import co.edu.uniquindio.utils.logger.LoggerDHT;

/**
 * The <code>DHashEnviroment</code> class is the node responsible for handling
 * with the messages. This class is notified when a message arrives and decides
 * what the dhash node must do.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see DHashNode
 */
public class DHashEnvironment implements Observer<Message> {

	/**
	 * Logger
	 */
	private static final LoggerDHT logger = LoggerDHT
			.getLogger(DHashEnvironment.class);

	/**
	 * Communication manager
	 */
	private CommunicationManager communicationManager;

	/**
	 * The references of the {@link DHashNode}.
	 */
	private DHashNode dHashNode;

	/**
	 * Is the constructor of the class. Sets the {@link DHashNode} with the
	 * object that comes as parameter.
	 * 
	 * @param dHashNode
	 *            The object that will be set.
	 */
	public DHashEnvironment(DHashNode dHashNode) {
		this.dHashNode = dHashNode;
		this.communicationManager = CommunicationManagerCache
				.getCommunicationManager(DHashNodeFactory.DHASH);
	}

	/**
	 * This method is called when a new message has arrived, and uses a
	 * {@link TransferManager} to receive the messages.
	 * 
	 * @param transferManager
	 *            The interface that will be used for receiving the messages.
	 */
	public void update(Message message) {

		logger.finest("Message to: " + dHashNode.getName() + " Message:["
				+ message.toString() + "]");
		logger.fine("Node " + dHashNode.getName() + ", arrived message of "
				+ message.getType());

		if (message.getType().equals(Protocol.PUT.getName())) {
			processPut(message);
			return;
		}

		if (message.getType().equals(Protocol.RESOURCE_COMPARE.getName())) {
			processResourceCompare(message);
			return;
		}

		if (message.getType().equals(Protocol.GET.getName())) {
			processGet(message);
			return;
		}
		if (message.getType().equals(Protocol.RESOURCE_TRANSFER.getName())) {
			processResourceTransfer(message);
			return;
		}
	}

	/**
	 * Process message of type is RESOURCE_TRANSFER
	 * 
	 * @param message
	 *            Message RESOURCE_TRANSFER
	 */
	private void processResourceTransfer(Message message) {

		BigMessage resourceTransferResponseMessage;

		resourceTransferResponseMessage = new BigMessageXML(message
				.getSequenceNumber(), SendType.RESPONSE,
				Protocol.RESOURCE_TRANSFER_RESPONSE,
				message.getMessageSource(), dHashNode.getName());

		if (dHashNode.getObjectManager().hasResource(
				message.getParam(ResourceTransferParams.RESOURCE_KEY.name()))) {

			Resource resource = dHashNode.getObjectManager().get(
					message
							.getParam(ResourceTransferParams.RESOURCE_KEY
									.name()));

			resourceTransferResponseMessage.addData(
					ResourceTransferResponseData.RESOURCE.name(), resource
							.getSerializable());

		} else {

			resourceTransferResponseMessage.addData(
					ResourceTransferResponseData.RESOURCE.name(), new byte[0]);

		}

		communicationManager.sendBigMessage(resourceTransferResponseMessage);
	}

	/**
	 * Process message of type is RESOURCE_COMPARE
	 * 
	 * @param message
	 *            Message RESOURCE_COMPARE
	 */
	private void processResourceCompare(Message message) {

		boolean isChecksumEquals = false;
		Message resourceCompareResponseMessage;

		if (dHashNode.getObjectManager().hasResource(
				message.getParam(ResourceCompareParams.RESOURCE_KEY.name()))) {

			String checkSum = dHashNode.getObjectManager()
					.get(
							message.getParam(ResourceCompareParams.RESOURCE_KEY
									.name())).getCheckSum();

			isChecksumEquals = checkSum.equals(message
					.getParam(ResourceCompareParams.CHECK_SUM.name()));
		}

		resourceCompareResponseMessage = new MessageXML(message
				.getSequenceNumber(), SendType.RESPONSE,
				Protocol.RESOURCE_COMPARE_RESPONSE, message.getMessageSource(),
				dHashNode.getName());
		resourceCompareResponseMessage.addParam(
				ResourceCompareResponseParams.EXIST_RESOURCE.name(), String
						.valueOf(isChecksumEquals));

		communicationManager.sendMessageUnicast(resourceCompareResponseMessage);

	}

	/**
	 * Process message of type is GET
	 * 
	 * @param message
	 *            Message GET
	 */
	private void processGet(Message message) {

		Message getResponseMessage = new MessageXML(
				message.getSequenceNumber(), SendType.RESPONSE,
				Protocol.GET_RESPONSE, message.getMessageSource(), dHashNode
						.getName());

		if (dHashNode.getObjectManager().hasResource(
				message.getParam(GetParams.RESOURCE_KEY.name()))) {

			getResponseMessage.addParam(GetResponseParams.HAS_RESOURCE.name(),
					String.valueOf(true));

		} else {

			getResponseMessage.addParam(GetResponseParams.HAS_RESOURCE.name(),
					String.valueOf(false));

		}

		communicationManager.sendMessageUnicast(getResponseMessage);

		logger.fine("Node " + dHashNode.getName() + ", confirmation for ");
		logger.finest("Response message: [" + getResponseMessage.toString()
				+ "]");
	}

	/**
	 * Process message of type is PUT
	 * 
	 * @param message
	 *            Message PUT
	 */
	private void processPut(Message message) {

		if (message instanceof BigMessage) {

			BigMessage bigMessage = (BigMessage) message;

			dHashNode.getObjectManager().put(
					bigMessage.getParam(PutParams.RESOURCE_KEY.name()),
					bigMessage.getData(PutDatas.RESOURCE.name()));

			Boolean replicate = Boolean.valueOf(bigMessage
					.getParam(PutParams.REPLICATE.name()));

			if (replicate) {
				try {
					dHashNode.replicateData(SerializableResource
							.valueOf(bigMessage.getData(PutDatas.RESOURCE
									.name())));
				} catch (ResourceAlreadyExistException e) {
					logger.error("Error replicating data", e);
				} catch (OverlayException e) {
					logger.error("Error replicating data", e);
				} catch (IOException e) {
					logger.error("Error replicating data", e);
				} catch (ClassNotFoundException e) {
					logger.error("Error replicating data", e);
				}
			}
		}

	}

	/**
	 * Gets dhash node name
	 */
	public String getName() {
		return dHashNode.getName();
	}
}