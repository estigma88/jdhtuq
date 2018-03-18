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
import co.edu.uniquindio.dhash.resource.checksum.ChecksumeCalculator;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.Message.SendType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
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
public class DHashEnvironment implements MessageProcessor {
    private static final Logger logger = Logger
            .getLogger(DHashEnvironment.class);

    private final CommunicationManager communicationManager;
    private final DHashNode dHashNode;
    private final SerializationHandler serializationHandler;
    private final ChecksumeCalculator checksumeCalculator;
    private final ResourceManager resourceManager;

    DHashEnvironment(CommunicationManager communicationManager, DHashNode dHashNode, SerializationHandler serializationHandler, ChecksumeCalculator checksumeCalculator, ResourceManager resourceManager) {
        this.communicationManager = communicationManager;
        this.dHashNode = dHashNode;
        this.serializationHandler = serializationHandler;
        this.checksumeCalculator = checksumeCalculator;
        this.resourceManager = resourceManager;
    }

    /**
     * This method is called when a new message has arrived, and uses a
     * TransferManagerto receive the messages
     * The interface that will be used for receiving the messages.
     */
    @Override
    public Message process(Message message) {

        logger.debug("Message to: " + dHashNode.getName() + " Message:["
                + message.toString() + "]");
        logger.debug("Node " + dHashNode.getName() + ", arrived message of "
                + message.getMessageType());

        Message response = null;

        if (message.getMessageType().equals(Protocol.PUT)) {
            response = processPut(message);
        }

        if (message.getMessageType().equals(Protocol.RESOURCE_COMPARE)) {
            response = processResourceCompare(message);
        }

        if (message.getMessageType().equals(Protocol.GET)) {
            response = processGet(message);
        }
        if (message.getMessageType().equals(Protocol.RESOURCE_TRANSFER)) {
            response = processResourceTransfer(message);
        }

        return response;
    }

    /**
     * Process message of type is RESOURCE_TRANSFER
     *
     * @param message Message RESOURCE_TRANSFER
     */
    private Message processResourceTransfer(Message message) {

        Message.MessageBuilder resourceTransferResponseMessage;

        resourceTransferResponseMessage = Message.builder()
                .sequenceNumber(message.getSequenceNumber())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.RESOURCE_TRANSFER_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(dHashNode.getName())
                        .build());

        /*resourceTransferResponseMessage = new BigMessageXML(message
                .getSequenceNumber(), SendType.RESPONSE,
                Protocol.RESOURCE_TRANSFER_RESPONSE,
                message.getMessageSource(), dHashNode.getName());*/

        if (resourceManager.hasResource(
                message.getParam(ResourceTransferParams.RESOURCE_KEY.name()))) {

            Resource resource = resourceManager.find(
                    message
                            .getParam(ResourceTransferParams.RESOURCE_KEY
                                    .name()));

            resourceTransferResponseMessage.data(
                    ResourceTransferResponseData.RESOURCE.name(), serializationHandler.encode(resource));

        } else {

            resourceTransferResponseMessage.data(
                    ResourceTransferResponseData.RESOURCE.name(), new byte[0]);

        }

        //communicationManager.sendBigMessage(resourceTransferResponseMessage.build());

        return resourceTransferResponseMessage.build();
    }

    /**
     * Process message of type is RESOURCE_COMPARE
     *
     * @param message Message RESOURCE_COMPARE
     */
    private Message processResourceCompare(Message message) {

        boolean isChecksumEquals = false;
        Message resourceCompareResponseMessage;

        if (resourceManager.hasResource(
                message.getParam(ResourceCompareParams.RESOURCE_KEY.name()))) {

            String checkSum = checksumeCalculator.calculate(resourceManager
                    .find(
                            message.getParam(ResourceCompareParams.RESOURCE_KEY
                                    .name())));

            isChecksumEquals = checkSum.equals(message
                    .getParam(ResourceCompareParams.CHECK_SUM.name()));
        }

        resourceCompareResponseMessage = Message.builder()
                .sequenceNumber(message.getSequenceNumber())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.RESOURCE_COMPARE_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(dHashNode.getName())
                        .build())
                .param(ResourceCompareResponseParams.EXIST_RESOURCE.name(), String.valueOf(isChecksumEquals))
                .build();

        /*resourceCompareResponseMessage = new MessageXML(message
                .getSequenceNumber(), SendType.RESPONSE,
                Protocol.RESOURCE_COMPARE_RESPONSE, message.getMessageSource(),
                dHashNode.getName());
        resourceCompareResponseMessage.addParam(
                ResourceCompareResponseParams.EXIST_RESOURCE.name(), String
                        .valueOf(isChecksumEquals));*/

        //communicationManager.sendMessageUnicast(resourceCompareResponseMessage);

        return resourceCompareResponseMessage;
    }

    /**
     * Process message of type is GET
     *
     * @param message Message GET
     */
    private Message processGet(Message message) {

        Message.MessageBuilder getResponseMessage = Message.builder()
                .sequenceNumber(message.getSequenceNumber())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.RESOURCE_COMPARE_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(dHashNode.getName())
                        .build());

        /*Message getResponseMessage = new MessageXML(
                message.getSequenceNumber(), SendType.RESPONSE,
                Protocol.GET_RESPONSE, message.getMessageSource(), dHashNode
                .getName());*/

        if (resourceManager.hasResource(
                message.getParam(GetParams.RESOURCE_KEY.name()))) {

            getResponseMessage.param(GetResponseParams.HAS_RESOURCE.name(),
                    String.valueOf(true));

        } else {

            getResponseMessage.param(GetResponseParams.HAS_RESOURCE.name(),
                    String.valueOf(false));

        }

        //communicationManager.sendMessageUnicast(getResponseMessage.build());

        logger.debug("Node " + dHashNode.getName() + ", confirmation for ");
        logger.debug("Response message: [" + getResponseMessage.toString()
                + "]");

        return getResponseMessage.build();
    }

    /**
     * Process message of type is PUT
     *
     * @param message Message PUT
     */
    private Message processPut(Message message) {

        /*if (message instanceof BigMessage) {

            BigMessage bigMessage = (BigMessage) message;*/

        try {
            Resource resource = serializationHandler.decode(
                    message.getData(PutDatas.RESOURCE.name()));

            resourceManager.save(resource);

            Boolean replicate = Boolean.valueOf(message
                    .getParam(PutParams.REPLICATE.name()));

            if (replicate) {
                dHashNode.replicateData(resource);
            }
        }catch (OverlayException e) {
            logger.error("Error replicating data", e);
        }
        //}

        return null;
    }

    /**
     * Gets dhash node name
     */
    /*public String getName() {
        return dHashNode.getName();
    }*/
}
