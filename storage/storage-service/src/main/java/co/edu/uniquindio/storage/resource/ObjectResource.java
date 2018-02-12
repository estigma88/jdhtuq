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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import co.edu.uniquindio.utils.hashing.DigestGenerator;

/**
 * The <code>ObjectResource</code> class implement all services to management
 * objects persisten
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class ObjectResource extends SerializableResource {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Object to management
	 */
	private Object object;

	/**
	 * Builds a ObjectResource from key and object
	 * 
	 * @param key
	 *            Name object
	 * @param object
	 *            Object
	 */
	public ObjectResource(String key, Object object) {
		super(key);

		if (object == null) {
			throw new IllegalArgumentException("The object must not null");
		}

		this.object = object;
	}

	/**
	 * Not implement
	 */
	public void delete(Map<String, Object> params) throws ResourceException {

	}

	/**
	 * Gets check sum from DigestGenerator of serializable object invoking
	 * <code>getSerializable</code>
	 */
	public String getCheckSum() {
		return DigestGenerator.getInstance().getCheckSum(getSerializable());
	}

	/**
	 * Gets serializable object
	 */
	public byte[] getSerializable() {
		ByteArrayOutputStream byteArrayOutputStream;
		ObjectOutputStream objectOutputStream;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();

			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			objectOutputStream.writeObject(this);

			return byteArrayOutputStream.toByteArray();

		} catch (IOException e) {
			throw new IllegalStateException("Error reading object", e);
		}
	}

	/**
	 * Not implement
	 */
	public void persist(Map<String, Object> params) throws ResourceException {

	}

	/**
	 * Gets object
	 * 
	 * @return Object
	 */
	public Object getObject() {
		return object;
	}

}
