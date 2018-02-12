/*
 *  StorageService project defined all services an storage management
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

package co.edu.uniquindio.storage.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * The <code>Resource</code> abstract class definer all services for resource
 * storage. This class is serializable. <code>valueOf</code> is implemented
 * unserialized bytes.
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public abstract class SerializableResource implements Serializable,Resource {

	/**
	 * Params to persist and delete
	 * 
	 * @author Daniel Pelaez
	 * @version 1.0, 17/06/2010
	 * @since 1.0F
	 */
	public enum ResourceParams {

		/**
		 * Name of who invoke
		 */
		MANAGER_NAME,

		/**
		 * Type of persist
		 */
		PERSIST_TYPE
	}

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Resource key
	 */
	protected String key;

	/**
	 * 
	 * @param key
	 */
	protected SerializableResource(String key) {

		if (key == null) {
			throw new IllegalArgumentException("The key must not null");
		}

		this.key = key;
	}

	/**
	 * Gets key of resource
	 * 
	 * @return Key of resource
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets key of resource
	 * 
	 * @param key
	 *            Resource key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Unserialized resource. resourceObject must to be an resource object
	 * serialized
	 * 
	 * @param resourceObject
	 *            Resource bytes
	 * @return Unserialized resource
	 * @throws IOException
	 *             throw when an error occur
	 * @throws ClassNotFoundException
	 *             throw when an error occur
	 */
	public static SerializableResource valueOf(byte[] resourceObject) throws IOException,
			ClassNotFoundException {

		if (resourceObject == null) {
			throw new IllegalArgumentException(
					"The resourceObject must not null");
		}
		
		ObjectInputStream objectInputStream;
		ByteArrayInputStream byteArrayInputStream;

		byteArrayInputStream = new ByteArrayInputStream(resourceObject);

		objectInputStream = new ObjectInputStream(byteArrayInputStream);

		return (SerializableResource) objectInputStream.readObject();
	}
}
