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
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.*;
import co.edu.uniquindio.utils.communication.transfer.response.MessageResponseProcessor;
import co.edu.uniquindio.utils.communication.transfer.response.MessagesReceiver;
import co.edu.uniquindio.utils.communication.transfer.response.ReturnsManager;
import co.edu.uniquindio.utils.communication.transfer.response.WaitingResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * The <code>CommunicationManagerTCP</code> class is an
 * <code>CommunicationManagerNetworkLAN</code>. Implemented the creation of
 * transfer object and unicast manager. Required params: PORT_TCP_RESOURCE and
 * PORT_TCP
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */

@Slf4j
public class CommunicationManagerTCP implements
        CommunicationManager {
    private static final String RESPONSE_TIME = "response_time";

    private final StreamCommunicator unicastManager;
    private final ConnectionReceiver unicastConnectionReceiver;
    private final Communicator multicastManager;
    private final MessagesReceiver multicastMessagesReciever;
    private final MessageResponseProcessor messageResponseProcessor;
    private final Observable<Message> observableCommunication;
    private final ReturnsManager<Message> returnsManager;
    private final ExecutorService messagesReceiverExecutor;
    private MessageProcessor messageProcessor;
    private MessageStreamProcessor messageStreamProcessor;
    private Map<String, String> communicationProperties;

    public CommunicationManagerTCP(StreamCommunicator unicastManager, ConnectionReceiver unicastConnectionReceiver, Communicator multicastManager, MessagesReceiver multicastMessagesReciever, MessageResponseProcessor messageResponseProcessor, Observable<Message> observableCommunication, ReturnsManager<Message> returnsManager, ExecutorService messagesReceiverExecutor) {
        this.unicastManager = unicastManager;
        this.unicastConnectionReceiver = unicastConnectionReceiver;
        this.multicastManager = multicastManager;
        this.multicastMessagesReciever = multicastMessagesReciever;
        this.messageResponseProcessor = messageResponseProcessor;
        this.observableCommunication = observableCommunication;
        this.returnsManager = returnsManager;
        this.messagesReceiverExecutor = messagesReceiverExecutor;
    }


    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to transfer message. The
     * typeReturn must to have a method by signed <code>T valueOf(String)</code>
     * and class must to have constructor without parameters, or an constructor
     * <code>T(String)</code> . If typeReturn is a Message class, the return is
     * message object. If message response contains several params, you must set
     * typeReturn like <code>Message</code>
     *
     * @param message    The type of the message that will be sent.
     * @param typeReturn The type of the response
     * @return An object <T> of the specified type.
     */
    @Override
    public <T> T send(Message message, Class<T> typeReturn) {

        /*
         * Created WaitingResult for message sequence number to transfer
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getId(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        send(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return messageResponseProcessor.process(messageResponse, typeReturn, null);
    }

    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to transfer message. The
     * typeReturn must to have a method by signed <code>T valueOf(String)</code>
     * and class must to have constructor without parameters, or an constructor
     * <code>T(String)</code> . If typeReturn is a Message class, the return is
     * message object. The message response must contains a param called
     * <code>paramNameResult</code>, the value of this is used for create result
     *
     * @param message         The type of the message that will be sent.
     * @param typeReturn      The type of the response
     * @param paramNameResult Param name of result
     * @return An object <T> of the specified type.
     */
    @Override
    public <T> T send(Message message, Class<T> typeReturn,
                      String paramNameResult) {

        /*
         * Created WaitingResult for message sequence number to transfer
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getId(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        send(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return messageResponseProcessor.process(messageResponse, typeReturn, paramNameResult);
    }

    /**
     * Sends a message specifying its type, the type of the response and the
     * data. Used Communicator instance called unicastManager to transfer message
     *
     * @param message Messages to transfer
     */
    @Override
    public void send(Message message) {
        unicastManager.send(message);
    }

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMultiCast(message)</code> to transfer message. The
     * typeReturn must to have a method by signed <code>T valueOf(String)</code>
     * and class must to have constructor without parameters, or an constructor
     * <code>T(String)</code> . If typeReturn is a Message class, the return is
     * message object. The message response must contains a param called
     * <code>paramNameResult</code>, the value of this is used for create result
     *
     * @param <T>        Type return
     * @param message    Message
     * @param typeReturn Type return
     * @return Response
     */
    public <T> T sendMultiCast(Message message, Class<T> typeReturn) {

        /*
         * Created WaitingResult for message sequence number to transfer
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getId(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        sendMultiCast(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return messageResponseProcessor.process(messageResponse, typeReturn, null);
    }

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMultiCast(message)</code> to transfer message. The
     * typeReturn must to have a method by signed <code>T valueOf(String)</code>
     * and class must to have constructor without parameters, or an constructor
     * <code>T(String)</code> . If typeReturn is a Message class, the return is
     * message object. If message response contains several params, you must set
     * typeReturn like <code>Message</code>
     *
     * @param <T>             Type return
     * @param message         Message
     * @param typeReturn      Type return
     * @param paramNameResult Param name of result
     * @return Response
     */
    public <T> T sendMultiCast(Message message, Class<T> typeReturn,
                               String paramNameResult) {

        /*
         * Created WaitingResult for message sequence number to transfer
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getId(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        sendMultiCast(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return messageResponseProcessor.process(messageResponse, typeReturn, paramNameResult);
    }

    /**
     * Sends a multicast message specifying its type, the type of the response
     * and the data. Used Communicator instance called multicastManager to transfer
     * message
     *
     * @param message Messages to transfer
     */
    public void sendMultiCast(Message message) {
        multicastManager.send(message);
    }

    @Override
    public void sendTo(Message message, OutputStream destination) {
        unicastManager.sendTo(message, destination);
    }

    /**
     * Sends a message specifying its type, the type of the response and the
     * data. Used Communicator instance called unicastManager to transfer message.
     * Besides, it sends an input stream
     *
     * @param message Messages to transfer
     */
    @Override
    public void send(MessageStream message, ProgressStatusTransfer progressStatusTransfer) {
        unicastManager.send(message, progressStatusTransfer);
    }

    @Override
    public void transfer(InputStream source, OutputStream destination, Long size, ProgressStatusTransfer progressStatusTransfer) throws IOException {
        unicastManager.transfer(source, destination, size, progressStatusTransfer);
    }

    @Override
    public MessageStream receive(Message resourceTransferMessage, ProgressStatusTransfer progressStatusTransfer) {
        return unicastManager.receive(resourceTransferMessage, progressStatusTransfer);
    }

    /**
     * Stop all process
     */
    public void stopAll() {
        try {
            multicastManager.close();
            multicastMessagesReciever.close();
            unicastManager.close();
            unicastConnectionReceiver.close();
            messagesReceiverExecutor.shutdown();
        } catch (IOException e) {
            throw new IllegalStateException("Problem stopping communication", e);
        }
    }

    /**
     * Adds observer to communication
     *
     * @param observer Observer to add
     */
    public void addObserver(Observer<Message> observer) {
        observableCommunication.addObserver(observer);
    }

    /**
     * Remove observer to communication
     *
     * @param observer Observer to remove
     */
    public void removeObserver(Observer<Message> observer) {
        observableCommunication.removeObserver(observer);
    }

    /**
     * Remove observer by name
     *
     * @param name Observer name
     */
    public void removeObserver(String name) {
        observableCommunication.removeObserver(name);
    }

    @Override
    public void addMessageProcessor(String name, MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void addMessageStreamProcessor(String name, MessageStreamProcessor messageStreamProcessor) {
        this.messageStreamProcessor = messageStreamProcessor;
    }

    @Override
    public void removeMessageProcessor(String name) {
        this.messageProcessor = null;
    }

    @Override
    public void removeMessageStreamProcessor(String name) {
        this.messageStreamProcessor = null;
    }

    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public MessageStreamProcessor getMessageStreamProcessor() {
        return messageStreamProcessor;
    }

    /*
     * (non-Javadoc)
     *
     * @seeco.edu.uniquindio.utils.communication.transfer.
     * CommunicationManagerWaitingResult#init ()
     */
    public void init() {
        this.unicastManager.start(communicationProperties);
        this.multicastManager.start(communicationProperties);

        messagesReceiverExecutor.execute(unicastConnectionReceiver);
        messagesReceiverExecutor.execute(multicastMessagesReciever);
    }

    public void init(Map<String, String> communicationProperties) {
        this.communicationProperties = communicationProperties;
        init();
    }
}
