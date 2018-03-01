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

package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.Message.SendType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * The {@code CommunicationManagerWaitingResult} class is used to send messages
 * regardless of the implementation of the way in which they are sent. Its
 * abstract because the implementation of some methods could be different.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0.2, 17/06/2010
 * @see CommunicationManager
 * @see WaitingResult
 * @see ReturnsManager
 * @since 1.0.2
 */
public abstract class CommunicationManagerWaitingResult implements
        CommunicationManager, Responder {

    /**
     * Property
     */
    private static final String RESPONSE_TIME = "RESPONSE_TIME";

    /**
     * Observable of message
     */
    protected Observable<Message> observableCommunication;
    /**
     * Multicast manager
     */
    protected Communicator multicastManager;
    /**
     * Unicast manager
     */
    protected Communicator unicastManager;
    /**
     * Unicast big message
     */
    protected Communicator unicastBigManager;
    /**
     * Communication properties
     */
    protected Map<String, String> communicationProperties;

    /**
     * Returns manager
     */
    private ReturnsManager<Message> returnsManager;

    /**
     * Builds an CommunicationManagerWaitingResult
     */
    protected CommunicationManagerWaitingResult() {
        this.returnsManager = new ReturnsManagerCommunication<Message>();
        this.observableCommunication = new Observable<Message>();
    }

    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to send message. The
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
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn) {

        /*
         * Created WaitingResult for message sequence number to send
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getSequenceNumber(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        sendMessageUnicast(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return processResponse(messageResponse, typeReturn, null);
    }

    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to send message. The
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
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn,
                                    String paramNameResult) {

        /*
         * Created WaitingResult for message sequence number to send
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getSequenceNumber(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        sendMessageUnicast(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return processResponse(messageResponse, typeReturn, paramNameResult);
    }

    /**
     * Sends a message specifying its type, the type of the response and the
     * data. Used Communicator instance called unicastManager to send message
     *
     * @param message Messages to send
     */
    public void sendMessageUnicast(Message message) {
        unicastManager.send(message);
    }

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMessageMultiCast(message)</code> to send message. The
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
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn) {

        /*
         * Created WaitingResult for message sequence number to send
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getSequenceNumber(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        sendMessageMultiCast(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return processResponse(messageResponse, typeReturn, null);
    }

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMessageMultiCast(message)</code> to send message. The
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
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn,
                                      String paramNameResult) {

        /*
         * Created WaitingResult for message sequence number to send
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getSequenceNumber(), Long
                        .parseLong(communicationProperties.get(RESPONSE_TIME)));

        sendMessageMultiCast(message);

        /*
         * Waiting for response
         */
        Message messageResponse = waitingResult.getResult();

        /*
         * Waiting for response
         */
        return processResponse(messageResponse, typeReturn, paramNameResult);
    }

    /**
     * Sends a multicast message specifying its type, the type of the response
     * and the data. Used Communicator instance called multicastManager to send
     * message
     *
     * @param message Messages to send
     */
    public void sendMessageMultiCast(Message message) {
        multicastManager.send(message);
    }

    /**
     * Send big message. Used ByteTransfer instance called byteTransferManager
     * to send message
     *
     * @param bigMessage Message to send
     */
    public void sendBigMessage(BigMessage bigMessage) {
        unicastBigManager.send(bigMessage);
    }

    /**
     * Send request to reciever big message. Used BytesTransfer instance to send
     * request
     *
     * @param message Request message
     * @return Response
     */
    public BigMessage recieverBigMessage(Message message) {

        /*
         * Created WaitingResult for message sequence number to send
         */
        WaitingResult<Message> waitingResult = returnsManager
                .createWaitingResult(message.getSequenceNumber(), -1);

        unicastBigManager.send(message);

        /*
         * Waiting for response
         */
        return (BigMessageXML) waitingResult.getResult();
    }

    /**
     * Stop all process
     */
    public void stopAll() {
        stop();
        multicastManager.stop();
        unicastManager.stop();
        unicastBigManager.stop();
    }

    /**
     * Release return in wait.
     *
     * @param message Message
     * @return True if return is release
     */
    public boolean releaseResponse(Message message) {
        if (message.getSendType().equals(SendType.RESPONSE)) {
            returnsManager.releaseWaitingResult(message.getSequenceNumber(),
                    message);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Convert message to return type
     *
     * @param <T>     Return type
     * @param message Message
     * @param type    Return type
     * @return Return
     */
    @SuppressWarnings("unchecked")
    private <T> T processResponse(Message message, Class<T> type,
                                  String paramNameResult) {

        T typeInstance = null;

        if (message == null) {
            return null;
        }

        if (type.equals(Message.class)) {
            return (T) message;
        }

        if (type.isInterface() || type.isAnnotation() || type.isArray()) {
            throw new IllegalArgumentException("The type must a class ("
                    + type.getName() + ")");
        }

        Set<String> params = message.getParamsKey();

        String paramValue;

        if (paramNameResult == null) {

            if (params.size() != 1) {
                throw new IllegalArgumentException(
                        "The message contains more than one parameter, you can not convert to "
                                + type.getName());
            }

            String paramName = (String) params.toArray()[0];

            if (paramName == null || paramName.isEmpty()) {
                throw new IllegalArgumentException(
                        "The message contains a param name null or empty");
            }

            paramValue = message.getParam(paramName);
        } else {

            paramValue = message.getParam(paramNameResult);
        }

        if (paramValue == null || paramValue.isEmpty()) {
            return null;
        }

        try {
            Method valueOf = type
                    .getMethod("valueOf", String.class);

            typeInstance = (T) valueOf.invoke(null, paramValue);
        } catch (Exception e) {
            try {

                Constructor<T> constructorString = type
                        .getDeclaredConstructor(String.class);

                typeInstance = constructorString.newInstance(paramValue);
            } catch (Exception e1) {
                throw new IllegalArgumentException(
                        "The method valueOf(String) not must to be invoked in class "
                                + type.getName(), e1);
            }
        }

        return typeInstance;
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

    /**
     * Initialize communication manager. Invoke to
     * <code>createUnicastManager()</code>,
     * <code>createMulticastManager()</code> and
     * <code>createTransferObjectManager()</code>
     */
    public void init() {
        this.unicastManager = createUnicastManager();
        this.multicastManager = createMulticastManager();
        this.unicastBigManager = createUnicastBigManager();
    }

    /**
     * Creates a Communicator instance for unicast
     *
     * @return Communicator
     */
    protected abstract Communicator createUnicastManager();

    /**
     * Creates a Communicator instance for multicast
     *
     * @return Communicator
     */
    protected abstract Communicator createMulticastManager();

    /**
     * Creates a Communicator instance for unicast big
     *
     * @return Communicator
     */
    protected abstract Communicator createUnicastBigManager();

    /**
     * Stop this instance of communication manager
     */
    protected abstract void stop();

    /**
     * Gets properties of communication
     *
     * @return CommunicationProperties
     */
    public Map<String, String> getCommunicationProperties() {
        return communicationProperties;
    }

    /**
     * Sets properties for communication
     *
     * @param communicationProperties Properties
     */
    public void setCommunicationProperties(
            Map<String, String> communicationProperties) {
        this.communicationProperties = communicationProperties;
    }

}
