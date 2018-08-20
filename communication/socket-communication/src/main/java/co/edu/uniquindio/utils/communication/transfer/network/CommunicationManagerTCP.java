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
import co.edu.uniquindio.utils.communication.transfer.*;
import co.edu.uniquindio.utils.communication.transfer.response.MessagesReceiver;
import co.edu.uniquindio.utils.communication.transfer.response.ResponseReleaser;
import co.edu.uniquindio.utils.communication.transfer.response.ReturnsManager;
import co.edu.uniquindio.utils.communication.transfer.response.ReturnsManagerCommunication;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

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
public class CommunicationManagerTCP implements
        CommunicationManager, ResponseReleaser {

    /**
     * The <code>CommunicationManagerTCPProperties</code> enum contains params
     * required for communication
     *
     * @author dpelaez
     */
    public enum CommunicationManagerTCPProperties {
        PORT_TCP_RESOURCE, PORT_TCP
    }

    /**
     * Properties for configuration CommunicationManagerNetworkLAN
     *
     * @author Daniel Pelaez
     * @author Hector Hurtado
     * @author Daniel Lopez
     * @version 1.0, 17/06/2010
     * @since 1.0
     */
    public enum CommunicationManagerNetworkLANProperties {
        BUFFER_SIZE_MULTICAST, IP_MULTICAST, PORT_MULTICAST
    }

    private static final Logger logger = Logger
            .getLogger(CommunicationManagerTCP.class);

    private static final String RESPONSE_TIME = "RESPONSE_TIME";

    private final MessageSerialization messageSerialization;
    private final Observable<Message> observableCommunication;
    private Communicator multicastManager;
    private Communicator unicastManager;
    private Map<String, String> communicationProperties;
    private ReturnsManager<Message> returnsManager;
    private MessageProcessor messageProcessor;
    private MessagesReceiver messagesReciever;

    public CommunicationManagerTCP(MessageSerialization messageSerialization) {
        this.returnsManager = new ReturnsManagerCommunication<Message>();
        this.observableCommunication = new Observable<Message>();
        this.messageSerialization = messageSerialization;
    }

    /**
     * Creates a BytesTransferManagerTCP instance. Required param in
     * CommunicationProperties called PORT_TCP_RESOURCE
     */
    protected Communicator createUnicastBigManager() {
        int portTcp;
        if (communicationProperties
                .containsKey(CommunicationManagerTCPProperties.PORT_TCP_RESOURCE
                        .name())) {
            portTcp = Integer.parseInt(communicationProperties
                    .get(CommunicationManagerTCPProperties.PORT_TCP_RESOURCE
                            .name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property PORT_TCP_RESOURCE not found");

            logger.error("Property PORT_TCP_RESOURCE not found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }
        unicastBigManager = new UnicastBigManagerTCP(portTcp, messageSerialization);

        return unicastBigManager;
    }

    /**
     * Creates a UnicastManagerTCP instance. Required param in
     * CommunicationProperties called PORT_TCP.
     */
    protected Communicator createUnicastManager() {
        int portTcp;
        if (communicationProperties
                .containsKey(CommunicationManagerTCPProperties.PORT_TCP.name())) {
            portTcp = Integer.parseInt(communicationProperties
                    .get(CommunicationManagerTCPProperties.PORT_TCP.name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property PORT_TCP not found");

            logger.error("Property PORT_TCP not found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }
        unicastManager = new UnicastManagerTCP(portTcp, messageSerialization);

        return unicastManager;
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
     * Stop all process
     */
    public void stopAll() {
        messagesReciever.close();
        multicastManager.close();
        multicastManager.close();
        unicastManager.close();
    }

    /**
     * Release return in wait.
     *
     * @param message Message
     * @return True if return is release
     */
    public boolean release(Message message) {
        if (message.getSendType().equals(Message.SendType.RESPONSE)) {
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

    @Override
    public void addMessageProcessor(String name, MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void removeMessageProcessor(String name) {
        this.messageProcessor = null;
    }

    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }


    /*
     * (non-Javadoc)
     *
     * @seeco.edu.uniquindio.utils.communication.transfer.
     * CommunicationManagerWaitingResult#init ()
     */
    public void init() {
        this.unicastManager = createUnicastManager();
        this.multicastManager = createMulticastManager();
        this.messagesReciever = new MessagesReceiver(multicastManager,
                unicastManager, unicastBigManager, this, this);
        this.observableCommunication = messagesReciever;
    }

    /**
     * Creates a Communicator for multicast manager. Are required of params in
     * communication properties: PORT_MULTICAST, IP_MULTICAST and
     * BUFFER_SIZE_MULTICAST
     */
    protected Communicator createMulticastManager() {

        int portMulticast;
        String ipMulticast;
        long bufferSize;

        if (communicationProperties
                .containsKey(CommunicationManagerNetworkLAN.CommunicationManagerNetworkLANProperties.PORT_MULTICAST
                        .name())) {
            portMulticast = Integer
                    .parseInt(communicationProperties
                            .get(CommunicationManagerNetworkLAN.CommunicationManagerNetworkLANProperties.PORT_MULTICAST
                                    .name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property PORT_MULTICAST not found");

            logger.error("Property PORT_MULTICAST no found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }

        if (communicationProperties
                .containsKey(CommunicationManagerNetworkLAN.CommunicationManagerNetworkLANProperties.IP_MULTICAST
                        .name())) {
            ipMulticast = communicationProperties
                    .get(CommunicationManagerNetworkLAN.CommunicationManagerNetworkLANProperties.IP_MULTICAST
                            .name());
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property IP_MULTICAST not found");

            logger.error("Property IP_MULTICAST no found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }

        if (communicationProperties
                .containsKey(CommunicationManagerNetworkLAN.CommunicationManagerNetworkLANProperties.BUFFER_SIZE_MULTICAST
                        .name())) {
            bufferSize = Long
                    .parseLong(communicationProperties
                            .get(CommunicationManagerNetworkLAN.CommunicationManagerNetworkLANProperties.BUFFER_SIZE_MULTICAST
                                    .name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property BUFFER_SIZE_MULTICAST not found");

            logger.error("Property BUFFER_SIZE_MULTICAST no found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }

        try {
            multicastManager = new MulticastManagerUDP(portMulticast,
                    InetAddress.getByName(ipMulticast), bufferSize, messageSerialization);
        } catch (UnknownHostException e) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Error of ipmulticast", e);

            logger.error("Error of ipmulticast", illegalArgumentException);

            throw illegalArgumentException;
        }

        return multicastManager;
    }

}
