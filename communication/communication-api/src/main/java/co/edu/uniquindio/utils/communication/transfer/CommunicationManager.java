package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;

import java.util.Map;

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
public interface CommunicationManager {

    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to send message
     *
     * @param message    The type of the message that will be sent.
     * @param typeReturn The type of the response
     * @return An object <T> of the specified type.
     */
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn);

    /**
     * Creates and sends a message specifying its type, the type of the response
     * and the data. Used <code>sendMessage(message)</code> to send message
     *
     * @param message         The type of the message that will be sent.
     * @param typeReturn      The type of the response
     * @param paramNameResult Param name of result
     * @return An object <T> of the specified type.
     */
    public <T> T sendMessageUnicast(Message message, Class<T> typeReturn,
                                    String paramNameResult);

    /**
     * Sends a message specifying its type, the type of the response and the
     * data. Used Communicator instance called unicastManager to send message
     *
     * @param message Messages to send
     */
    public void sendMessageUnicast(Message message);

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMessageMultiCast(message)</code> to send message
     *
     * @param <T>        Type return
     * @param message    Message
     * @param typeReturn Type return
     * @return Response
     */
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn);

    /**
     * Creates and sends a multicast message specifying its type, the type of
     * the response and the data. Used
     * <code>sendMessageMultiCast(message)</code> to send message
     *
     * @param <T>             Type return
     * @param message         Message
     * @param typeReturn      Type return
     * @param paramNameResult Param name of result
     * @return Response
     */
    public <T> T sendMessageMultiCast(Message message, Class<T> typeReturn,
                                      String paramNameResult);

    /**
     * Sends a multicast message specifying its type, the type of the response
     * and the data. Used Communicator instance called multicastManager to send
     * message
     *
     * @param message Messages to send
     */
    public void sendMessageMultiCast(Message message);

    /**
     * Stop all process
     */
    public void stopAll();

    /**
     * Adds observer to communication
     *
     * @param observer Observer to add
     */
    public void addObserver(Observer<Message> observer);

    /**
     * Remove observer to communication
     *
     * @param observer Observer to remove
     */
    public void removeObserver(Observer<Message> observer);

    /**
     * Remove observer by name
     *
     * @param name Observer name
     */
    public void removeObserver(String name);


    public void addMessageProcessor(String name, MessageProcessor messageProcessor);

    public void removeMessageProcessor(String name);

    /**
     * Initialize communication manager.
     */
    public void init();

    /**
     * Gets properties of communication
     *
     * @return CommunicationProperties
     */
    public Map<String, String> getCommunicationProperties();

    /**
     * Sets properties for communication
     *
     * @param communicationProperties Properties
     */
    public void setCommunicationProperties(
            Map<String, String> communicationProperties);
}
