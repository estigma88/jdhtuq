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

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.protocol.Protocol.*;
import co.edu.uniquindio.dhash.resource.ResourceAlreadyExistException;
import co.edu.uniquindio.dhash.resource.checksum.ChecksumeCalculator;
import co.edu.uniquindio.dhash.resource.persistence.PersistenceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.BigMessage;
import co.edu.uniquindio.utils.communication.message.BigMessageXML;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.Message.SendType;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.apache.log4j.Logger;

/**
 * The <code>DHashEnviroment</code> class is the node responsible for handling
 * with the messages. This class is notified when a message arrives and decides
 * what the dhash node must do.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see DHashNode
 * @since 1.0
 */
public class DHashEnvironment implements Observer<Message> {
    private static final Logger logger = Logger
            .getLogger(DHashEnvironment.class);

    private CommunicationManager communicationManager;
    private DHashNode dHashNode;
    private SerializationHandler serializationHandler;
    private ChecksumeCalculator checksumeCalculator;
    private PersistenceManager persistenceManager;

    DHashEnvironment(CommunicationManager communicationManager, DHashNode dHashNode, SerializationHandler serializationHandler, ChecksumeCalculator checksumeCalculator, PersistenceManager persistenceManager) {
        this.communicationManager = communicationManager;
        this.dHashNode = dHashNode;
        this.serializationHandler = serializationHandler;
        this.checksumeCalculator = checksumeCalculator;
        this.persistenceManager = persistenceManager;
    }

    /**
     * This method is called when a new message has arrived, and uses a
     * TransferManagerto receive the messages
     * The interface that will be used for receiving the messages.
     */
    public void update(Message message) {

        logger.debug("Message to: " + dHashNode.getName() + " Message:["
                + message.toString() + "]");
        logger.debug("Node " + dHashNode.getName() + ", arrived message of "
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
     * @param message Message RESOURCE_TRANSFER
     */
    private void processResourceTransfer(Message message) {

        BigMessage resourceTransferResponseMessage;

        resourceTransferResponseMessage = new BigMessageXML(message
                .getSequenceNumber(), SendType.RESPONSE,
                Protocol.RESOURCE_TRANSFER_RESPONSE,
                message.getMessageSource(), dHashNode.getName());

        if (persistenceManager.hasResource(
                message.getParam(ResourceTransferParams.RESOURCE_KEY.name()))) {

            Resource resource = persistenceManager.find(
                    message
                            .getParam(ResourceTransferParams.RESOURCE_KEY
                                    .name()));

            resourceTransferResponseMessage.addData(
                    ResourceTransferResponseData.RESOURCE.name(), serializationHandler.encode(resource));

        } else {

            resourceTransferResponseMessage.addData(
                    ResourceTransferResponseData.RESOURCE.name(), new byte[0]);

        }

        communicationManager.sendBigMessage(resourceTransferResponseMessage);
    }

    /**
     * Process message of type is RESOURCE_COMPARE
     *
     * @param message Message RESOURCE_COMPARE
     */
    private void processResourceCompare(Message message) {

        boolean isChecksumEquals = false;
        Message resourceCompareResponseMessage;

        if (persistenceManager.hasResource(
                message.getParam(ResourceCompareParams.RESOURCE_KEY.name()))) {

            String checkSum = checksumeCalculator.calculate(persistenceManager
                    .find(
                            message.getParam(ResourceCompareParams.RESOURCE_KEY
                                    .name())));

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
     * @param message Message GET
     */
    private void processGet(Message message) {

        Message getResponseMessage = new MessageXML(
                message.getSequenceNumber(), SendType.RESPONSE,
                Protocol.GET_RESPONSE, message.getMessageSource(), dHashNode
                .getName());

        if (persistenceManager.hasResource(
                message.getParam(GetParams.RESOURCE_KEY.name()))) {

            getResponseMessage.addParam(GetResponseParams.HAS_RESOURCE.name(),
                    String.valueOf(true));

        } else {

            getResponseMessage.addParam(GetResponseParams.HAS_RESOURCE.name(),
                    String.valueOf(false));

        }

        communicationManager.sendMessageUnicast(getResponseMessage);

        logger.debug("Node " + dHashNode.getName() + ", confirmation for ");
        logger.debug("Response message: [" + getResponseMessage.toString()
                + "]");
    }

    /**
     * Process message of type is PUT
     *
     * @param message Message PUT
     */
    private void processPut(Message message) {

        if (message instanceof BigMessage) {

            BigMessage bigMessage = (BigMessage) message;

            try {
                Resource resource = serializationHandler.decode(
                        bigMessage.getParam(PutParams.RESOURCE_KEY.name()),
                        bigMessage.getData(PutDatas.RESOURCE.name()));

                persistenceManager.persist(resource);

                Boolean replicate = Boolean.valueOf(bigMessage
                        .getParam(PutParams.REPLICATE.name()));

                if (replicate) {
                    dHashNode.replicateData(resource);
                }
            } catch (ResourceAlreadyExistException e) {
                logger.error("Error replicating data", e);
            } catch (OverlayException e) {
                logger.error("Error replicating data", e);
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
