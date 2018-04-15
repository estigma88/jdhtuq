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
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

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
public class CommunicationDataStructure extends Observable<Message> implements CommunicationManager {
    /**
     * Data struture map
     */
    private Map<String, MessageProcessor> dataStructure;

    /**
     * Builds a CommunicationDataStructure
     */
    public CommunicationDataStructure() {
        dataStructure = new HashMap<String, MessageProcessor>();
    }

    public Message notifyUnicast(Message message) {
        Message response = null;

        MessageProcessor messageProcessor = dataStructure.get(message.getAddress().getDestination());

        super.notifyMessage(message);

        if (messageProcessor != null) {
            response = messageProcessor.process(message);
        }

        super.notifyMessage(response);

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

            super.notifyMessage(message);

            if (messageProcessor != null) {
                response = messageProcessor.process(message);
            }

            super.notifyMessage(response);
        }

        return response;
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn) {
        Message response = notifyUnicast(message);

        return processResponse(response, typeReturn, null);
    }

    @Override
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn, String paramNameResult) {
        Message response = notifyUnicast(message);

        return processResponse(response, typeReturn, paramNameResult);
    }

    @Override
    public void sendMessageUnicast(Message message) {
        notifyUnicast(message);
    }

    @Override
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn) {
        Message response = notifyMulticast(message);

        return processResponse(response, typeReturn, null);
    }

    @Override
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn, String paramNameResult) {
        Message response = notifyMulticast(message);

        return processResponse(response, typeReturn, paramNameResult);
    }

    @Override
    public void sendMessageMultiCast(Message message) {
        notifyMulticast(message);
    }

    @Override
    public void stopAll() {

    }

    @Override
    public void addMessageProcessor(String name, MessageProcessor messageProcessor) {
        dataStructure.put(name, messageProcessor);
    }

    @Override
    public void removeMessageProcessor(String name) {
        dataStructure.remove(name);
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
