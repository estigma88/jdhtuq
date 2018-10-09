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

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import co.edu.uniquindio.utils.communication.transfer.MessageStreamProcessor;
import co.edu.uniquindio.utils.communication.transfer.ProgressStatusTransfer;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The <code>CommunicationDataStructure</code> class management all observer
 * that must to reciever messages from communication
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class CommunicationDataStructure implements CommunicationManager {
    /**
     * Data struture map
     */
    private final Map<String, MessageProcessor> dataStructure;
    private final Map<String, MessageStreamProcessor> dataStructureStream;
    private final Observable<Message> observable;
    private final Observable<MessageStream> observableStream;

    /**
     * Builds a CommunicationDataStructure
     * @param dataStructure
     * @param dataStructureStream
     * @param observable
     * @param observableStream
     */
    public CommunicationDataStructure(Map<String, MessageProcessor> dataStructure, Map<String, MessageStreamProcessor> dataStructureStream, Observable<Message> observable, Observable<MessageStream> observableStream) {
        this.dataStructure = dataStructure;
        this.dataStructureStream = dataStructureStream;
        this.observable = observable;
        this.observableStream = observableStream;
    }

    public Message notifyUnicast(Message message) {
        Message response = null;

        MessageProcessor messageProcessor = dataStructure.get(message.getAddress().getDestination());

        observable.notifyMessage(message);

        if (messageProcessor != null) {
            response = messageProcessor.process(message);
        }

        observable.notifyMessage(response);

        return response;
    }

    /**
     * Notify multicast message. Choose randomly an observer who is not sent and
     * send message
     *
     * @param message Message to send
     */
    public Message notifyMulticast(Message message) {
        String nameSource = message.getAddress().getSource();
        String nameDestination = nameSource;
        Message response = null;
        int randomNumber;

        if (dataStructure.size() != 1) {
            List<String> keys = new ArrayList<>(dataStructure.keySet());
            while (nameSource.equals(nameDestination)) {
                randomNumber = (int) (Math.random() * dataStructure.size());

                nameDestination = keys.get(randomNumber);
            }

            MessageProcessor messageProcessor = dataStructure.get(nameDestination);

            observable.notifyMessage(message);

            if (messageProcessor != null) {
                response = messageProcessor.process(message);
            }

            observable.notifyMessage(response);
        }

        return response;
    }

    @Override
    public <T> T send(Message message, Class<T> typeReturn) {
        Message response = notifyUnicast(message);

        return processResponse(response, typeReturn, null);
    }

    @Override
    public <T> T send(Message message, Class<T> typeReturn, String paramNameResult) {
        Message response = notifyUnicast(message);

        return processResponse(response, typeReturn, paramNameResult);
    }

    @Override
    public void send(Message message) {
        notifyUnicast(message);
    }

    @Override
    public <T> T sendMultiCast(Message message, Class<T> typeReturn) {
        Message response = notifyMulticast(message);

        return processResponse(response, typeReturn, null);
    }

    @Override
    public <T> T sendMultiCast(Message message, Class<T> typeReturn, String paramNameResult) {
        Message response = notifyMulticast(message);

        return processResponse(response, typeReturn, paramNameResult);
    }

    @Override
    public void sendMultiCast(Message message) {
        notifyMulticast(message);
    }

    @Override
    public MessageStream receive(Message message, ProgressStatusTransfer progressStatusTransfer) {
        MessageStream response = null;

        MessageStreamProcessor messageProcessor = dataStructureStream.get(message.getAddress().getDestination());

        observable.notifyMessage(message);

        if (messageProcessor != null) {
            response = messageProcessor.process(MessageStream.builder()
                    .message(message)
                    .build());
        }

        observableStream.notifyMessage(response);

        return response;
    }

    @Override
    public Message send(MessageStream messageStream, ProgressStatusTransfer progressStatusTransfer) {
        MessageStream response = null;

        MessageStreamProcessor messageProcessor = dataStructureStream.get(messageStream.getMessage().getAddress().getDestination());

        observableStream.notifyMessage(messageStream);

        if (messageProcessor != null) {
            response = messageProcessor.process(messageStream);
        }

        observableStream.notifyMessage(response);

        return response.getMessage();
    }

    @Override
    public void stopAll() {

    }

    @Override
    public void addMessageProcessor(String name, MessageProcessor messageProcessor) {
        dataStructure.put(name, messageProcessor);
    }

    @Override
    public void addMessageStreamProcessor(String name, MessageStreamProcessor messageStreamProcessor) {
        dataStructureStream.put(name, messageStreamProcessor);
    }

    @Override
    public void removeMessageProcessor(String name) {
        dataStructure.remove(name);
    }

    @Override
    public void removeMessageStreamProcessor(String name) {
        dataStructureStream.remove(name);
    }


    @Override
    public void addObserver(Observer<Message> observer) {
        observable.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer<Message> observer) {
        observable.removeObserver(observer);
    }

    @Override
    public void removeObserver(String name) {
        observable.removeObserver(name);
    }

    @Override
    public void addStreamObserver(Observer<MessageStream> observer) {
        observableStream.addObserver(observer);
    }

    @Override
    public void removeStreamObserver(Observer<MessageStream> observer) {
        observableStream.removeObserver(observer);
    }

    @Override
    public void removeStreamObserver(String name) {
        observableStream.removeObserver(name);
    }

    @Override
    public void init() {

    }

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
}
