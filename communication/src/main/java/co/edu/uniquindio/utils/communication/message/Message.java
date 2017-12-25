package co.edu.uniquindio.utils.communication.message;

import java.util.Set;

/**
 * Interface that defined all methods for message
 * 
 * @author Daniel Pelaez
 * 
 */
public interface Message {

	/**
	 * Type of send
	 * 
	 * @author Daniel Pelaez
	 * 
	 */
	public static enum SendType {
		REQUEST, RESPONSE
	}

	/**
	 * This method is used for adding data to the message
	 * 
	 * @param dataAdd
	 *            . The data that will be added to the message
	 */
	public void addParam(String name, String value);

	/**
	 * This method is used for getting a specific data from the message
	 * 
	 * @param position
	 *            . The position of the data in the array
	 * @return Returns the data that is stored in the given position
	 */
	public String getParam(String name);

	/**
	 * This method is used for knowing if the message is the same source and
	 * destination node
	 * 
	 * @return Returns true if the message is the same source and destination
	 *         node
	 */
	public boolean isMessageFromMySelf();

	/**
	 * Gets the message type
	 * 
	 * @return Message type
	 */
	public String getType();

	/**
	 * Gets the message type
	 * 
	 * @return Send type
	 */
	public SendType getSendType();

	/**
	 * Gets the message source address
	 * 
	 * @return Message source
	 */
	public String getMessageSource();

	/**
	 * Gets the message destination address
	 * 
	 * @return Message destination
	 */
	public String getMessageDestination();

	/**
	 * Gets param keys
	 * 
	 * @return Param keys
	 */
	public Set<String> getParamsKey();

	/**
	 * Gets the value of the sequenceNumber property.
	 * 
	 */
	public long getSequenceNumber();
}
