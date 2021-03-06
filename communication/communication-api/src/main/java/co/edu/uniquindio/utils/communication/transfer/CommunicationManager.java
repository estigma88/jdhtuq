/*
 *  Communication project implement communication point to point and multicast
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
 */

package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The {@code CommunicationManager} interface is used to send messages
 * regardless of the implementation of the way in which they are sent.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0.2, 17/06/2010
 * @since 1.0.2
 */
public interface CommunicationManager extends StreamManager{

    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to send message
     *
     * @param message    The type of the message that will be sent.
     * @param typeReturn The type of the response
     * @return An object <T> of the specified type.
     */
    <T> T send(Message message, Class<T> typeReturn);

    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to send message
     *
     * @param message         The type of the message that will be sent.
     * @param typeReturn      The type of the response
     * @param paramNameResult Param name of result
     * @return An object <T> of the specified type.
     */
    <T> T send(Message message, Class<T> typeReturn,
               String paramNameResult);


    /**
     * Sends a message specifying its type, the type of the response and the
     * data. Used Communicator instance called unicastManager to send message
     *
     * @param message Messages to send
     */
    void send(Message message);

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMultiCast(message)</code> to send message
     *
     * @param <T>        Type return
     * @param message    Message
     * @param typeReturn Type return
     * @return Response
     */
    <T> T sendMultiCast(Message message, Class<T> typeReturn);

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMultiCast(message)</code> to send message
     *
     * @param <T>             Type return
     * @param message         Message
     * @param typeReturn      Type return
     * @param paramNameResult Param name of result
     * @return Response
     */
    <T> T sendMultiCast(Message message, Class<T> typeReturn,
                        String paramNameResult);

    /**
     * Sends a multicast message specifying its type, the type of the response
     * and the data. Used Communicator instance called multicastManager to send
     * message
     *
     * @param message Messages to send
     */
    void sendMultiCast(Message message);

    /**
     * Adds observer to communication
     *
     * @param observer Observer to add
     */
    void addObserver(Observer<Message> observer);

    /**
     * Remove observer to communication
     *
     * @param observer Observer to remove
     */
    void removeObserver(Observer<Message> observer);

    /**
     * Remove observer by name
     *
     * @param name Observer name
     */
    void removeObserver(String name);


    /**
     * Adds observer to communication
     *
     * @param observer Observer to add
     */
    void addStreamObserver(Observer<MessageStream> observer);

    /**
     * Remove observer to communication
     *
     * @param observer Observer to remove
     */
    void removeStreamObserver(Observer<MessageStream> observer);

    /**
     * Remove observer by name
     *
     * @param name Observer name
     */
    void removeStreamObserver(String name);

    /**
     * Add a message processor to handle any kind of message
     *
     * @param name             of the message processor
     * @param messageProcessor message processor
     */
    void addMessageProcessor(String name, MessageProcessor messageProcessor);

    /**
     * Add a message processor to handle any kind of message with additional input stream
     *
     * @param name             of the message processor
     */
    void addMessageStreamProcessor(String name, MessageStreamProcessor messageStreamProcessor);

    /**
     * Remove a message processor
     *
     * @param name of the message processor
     */
    void removeMessageProcessor(String name);

    /**
     * Remove a message processor
     *
     * @param name of the message processor
     */
    void removeMessageStreamProcessor(String name);

    /**
     * Initialize communication manager.
     */
    void init();

    /**
     * Stop all process
     */
    void stopAll();

}
