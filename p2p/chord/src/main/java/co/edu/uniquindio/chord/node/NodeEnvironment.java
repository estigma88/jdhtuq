/*
 *  Chord project implement of lookup algorithm Chord
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
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
 *
 */

package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.chord.protocol.Protocol.*;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.Message.SendType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

/**
 * The <code>NodeEnvironment</code> class is the node responsible for handling
 * with the messages. This class is notified when a message arrives and decides
 * what the chord node must do. This class also initializes and starts the
 * {@link StableRing} of the node.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see ChordNode
 * @since 1.0
 */
@Slf4j
class NodeEnvironment implements MessageProcessor {
    /**
     * Communication manager
     */
    private final CommunicationManager communicationManager;

    /**
     * Defines if an arrive message must be process.
     */
    private boolean process = true;

    /**
     * The reference of the chord node.
     */
    private final ChordNode chordNode;

    /**
     * The thread that uses the commands for stabilizing the node.
     */
    private final KeyFactory keyFactory;
    private final IdGenerator sequenceGenerator;

    NodeEnvironment(CommunicationManager communicationManager, ChordNode chordNode, KeyFactory keyFactory, IdGenerator sequenceGenerator) {
        this.communicationManager = communicationManager;
        this.chordNode = chordNode;
        this.keyFactory = keyFactory;
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    /**
     * This method is called when a new message has arrived.
     *
     * @param message  The income message.
     */
    public Message process(Message message) {

        log.debug("Message to '" + chordNode.getKey().getValue() + "', ["
                + message.toString());

        Message response = null;

        /*
         * Discards the message that comes if process==false
         */
        if (!process) {
            return null;
        }

        if (message.getMessageType().equals(Protocol.LOOKUP)) {
            response = processLookUp(message);
        }
        if (message.getMessageType().equals(Protocol.PING)) {
            response = processPing(message);
        }
        if (message.getMessageType().equals(Protocol.NOTIFY)) {
            response = processNotify(message);
        }
        if (message.getMessageType().equals(Protocol.GET_PREDECESSOR)) {
            response = processGetPredecessor(message);
        }
        if (message.getMessageType().equals(Protocol.BOOTSTRAP)) {
            response = processBootStrap(message);
        }
        if (message.getMessageType().equals(Protocol.SET_PREDECESSOR)) {
            response = processSetPredecessor(message);
        }
        if (message.getMessageType().equals(Protocol.SET_SUCCESSOR)) {
            response = processSetSuccessor(message);
        }
        if (message.getMessageType().equals(Protocol.GET_SUCCESSOR_LIST)) {
            response = processGetSuccessorList(message);
        }

        return response;

    }

    /**
     * Process message of type is GET_SUCCESSOR_LIST
     *
     * @param message Message GET_SUCCESSOR_LIST
     */
    private Message processGetSuccessorList(Message message) {
        String successorList;
        Message getSuccesorListResponseMessage;

        successorList = chordNode.getSuccessorList().toString();

        getSuccesorListResponseMessage = Message.builder()
                .id(message.getId())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.GET_SUCCESSOR_LIST_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(chordNode.getKey().getValue())
                        .build())
                .param(GetSuccessorListResponseParams.SUCCESSOR_LIST.name(), successorList)
                .build();

        return getSuccesorListResponseMessage;
    }

    /**
     * Process message of type is SET_SUCCESSOR
     *
     * @param message Message SET_SUCCESSOR
     */
    private Message processSetSuccessor(Message message) {
        ChordKey nodeSucessor;

        nodeSucessor = (ChordKey) keyFactory.newKey(message.getParam(SetSuccessorParams.SUCCESSOR
                .name()));

        chordNode.setSuccessor(nodeSucessor);

        return null;
    }

    /**
     * Process message of type is SET_PREDECESSOR
     *
     * @param message Message SET_PREDECESSOR
     */
    private Message processSetPredecessor(Message message) {
        ChordKey nodePredecessor;

        nodePredecessor = (ChordKey) keyFactory.newKey(message
                .getParam(SetPredecessorParams.PREDECESSOR.name()));

        chordNode.setPredecessor(nodePredecessor);

        return null;
    }

    /**
     * Process message of type is LEAVE
     *
     * @param message Message LEAVE
     */
    private Message processLeave(Message message) {

        Message setSuccessorMessage;
        Message setPredecessorMessage;

        process = false;

        if (!chordNode.getSuccessor().equals(chordNode.getKey())) {

            setSuccessorMessage = Message.builder()
                    .id(sequenceGenerator.newId())
                    .sendType(SendType.REQUEST)
                    .messageType(Protocol.SET_SUCCESSOR)
                    .address(Address.builder()
                            .destination(chordNode.getPredecessor().getValue())
                            .source(chordNode.getKey().getValue())
                            .build())
                    .param(SetSuccessorParams.SUCCESSOR.name(), chordNode.getSuccessor().getValue())
                    .build();

            communicationManager.send(setSuccessorMessage);

            setPredecessorMessage = Message.builder()
                    .id(sequenceGenerator.newId())
                    .sendType(SendType.REQUEST)
                    .messageType(Protocol.SET_PREDECESSOR)
                    .address(Address.builder()
                            .destination(chordNode.getSuccessor().getValue())
                            .source(chordNode.getKey().getValue())
                            .build())
                    .param(SetPredecessorParams.PREDECESSOR.name(), chordNode.getPredecessor().toString())
                    .build();

            communicationManager.send(setPredecessorMessage);
        }

        communicationManager.removeMessageProcessor(this.chordNode.getKey().getValue());

        return null;
    }

    /**
     * Process message of type is BOOTSTRAP
     *
     * @param message Message BOOTSTRAP
     */
    private Message processBootStrap(Message message) {

        Message bootstrapResponseMessage = null;

        if (message.getAddress().getSource().equals(chordNode.getKey().getValue())) {
            return bootstrapResponseMessage;
        }

        bootstrapResponseMessage = Message.builder()
                .id(message.getId())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.BOOTSTRAP_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(chordNode.getKey().getValue())
                        .build())
                .param(BootStrapResponseParams.NODE_FIND.name(), chordNode.getKey().toString())
                .build();

        return bootstrapResponseMessage;
    }

    /**
     * Process message of type is GET_PREDECESSOR
     *
     * @param message Message GET_PREDECESSOR
     */
    private Message processGetPredecessor(Message message) {

        Message.MessageBuilder getPredecessorResponseMessage;

        getPredecessorResponseMessage = Message.builder()
                .id(message.getId())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.GET_PREDECESSOR_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(chordNode.getKey().getValue())
                        .build());

        if (chordNode.getPredecessor() == null) {
            getPredecessorResponseMessage.param(GetPredecessorResponseParams.PREDECESSOR.name(), null);
        } else {
            getPredecessorResponseMessage.param(GetPredecessorResponseParams.PREDECESSOR.name(), chordNode
                    .getPredecessor().toString());
        }

        return getPredecessorResponseMessage.build();
    }

    /**
     * Process message of type is NOTIFY
     *
     * @param message Message NOTIFY
     */
    private Message processNotify(Message message) {
        if (!message.isMessageFromMySelf()) {
            ChordKey node;

            node = (ChordKey) keyFactory.newKey(message.getAddress().getSource());

            chordNode.notify(node);
        }
        return null;
    }

    /**
     * Process message of type is PING
     *
     * @param message Message PING
     */
    private Message processPing(Message message) {

        Message pingMessage;

        pingMessage = Message.builder()
                .id(message.getId())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.PING_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(chordNode.getKey().getValue())
                        .build())
                .param(PingResponseParams.PING.name(), Boolean.TRUE.toString())
                .build();

        return pingMessage;
    }

    /**
     * Process message of type is LOOKUP
     *
     * @param message Message LOOKUP
     */
    private Message processLookUp(Message message) {

        Message.MessageBuilder lookupResponseMessage;

        lookupResponseMessage = Message.builder()
                .id(message.getId())
                .sendType(SendType.RESPONSE)
                .messageType(Protocol.LOOKUP_RESPONSE)
                .address(Address.builder()
                        .destination(message.getAddress().getSource())
                        .source(chordNode.getKey().getValue())
                        .build())
                .param(LookupResponseParams.TYPE.name(), message.getParam(LookupParams.TYPE.name()));

        /* Discards the message that comes from the same node */
        if (message.isMessageFromMySelf()) {
            lookupResponseMessage.param(LookupResponseParams.NODE_FIND
                    .name(), chordNode.getKey().toString());

        } else {

            ChordKey response;
            ChordKey id;

            id = (ChordKey) keyFactory.newKey(new BigInteger(message.getParam(LookupParams.HASHING
                    .name())));

            response = chordNode.findSuccessor(id, LookupType.valueOf(message
                    .getParam(LookupParams.TYPE.name())));

            if (response == null) {
                lookupResponseMessage.param(LookupResponseParams.NODE_FIND
                        .name(), chordNode.getKey().toString());
            } else {
                lookupResponseMessage.param(LookupResponseParams.NODE_FIND
                        .name(), response.toString());
            }

        }

        return lookupResponseMessage.build();
    }

    /**
     * Gets the reference of the chordNode
     *
     * @return The {@link ChordNode} reference
     */
    public ChordNode getChordNode() {
        return chordNode;
    }

    /**
     * Gets chord node name from key value
     */
    /*public String getName() {
        return chordNode.getKey().getValue();
    }*/

    void setProcess(boolean process) {
        this.process = process;
    }
}
