package co.edu.uniquindio.storage.resource;

import java.util.Map;

public interface Resource {
	/**
	 * Gets key of resource
	 * 
	 * @return Key of resource
	 */
	public String getKey();
	/**
	 * Gets serializable resource
	 * 
	 * @return Serializable resource
	 */
	public byte[] getSerializable();
	/**
	 * Persist the resource using params
	 * 
	 * @param params
	 *            Params to persist
	 * @throws ResourceException
	 *             throw when an error occur
	 */
	public void persist(Map<String, Object> params)
			throws ResourceException;
	/**
	 * Delete the resource using params
	 * 
	 * @param params
	 *            Params to delete
	 * @throws ResourceException
	 *             throw when an error occur
	 */
	public void delete(Map<String, Object> params)
			throws ResourceException;
	/**
	 * Gets check sum from resource
	 * 
	 * @return Check sum
	 */
	public abstract String getCheckSum();
}
