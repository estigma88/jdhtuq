package co.edu.uniquindio.utils.communication.message;

import java.util.Iterator;

public interface BigMessage extends Message {

	/**
	 * Gets data by name
	 * 
	 * @param name
	 *            Data name
	 * @return Data
	 */
	public byte[] getData(String name);

	/**
	 * Adds data to message
	 * 
	 * @param name
	 *            Data name
	 * @param data
	 *            Data value
	 */
	public void addData(String name, byte[] data);

	/**
	 * Gets keys of datas
	 * 
	 * @return Data keys
	 */
	public Iterator<String> getDatasKey();
}
