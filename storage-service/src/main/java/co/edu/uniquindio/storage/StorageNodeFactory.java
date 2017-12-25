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

package co.edu.uniquindio.storage;

import java.net.InetAddress;

/**
 * The <code>StorageNodeFactory</code> abstract class defined methods for to create
 * StorageNode nodes. The class is a single instance.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public abstract class StorageNodeFactory {

	/**
	 * Single StorageNodeFactory instance
	 */
	private static StorageNodeFactory dHashFactory;

	/**
	 * Creates a single instance of qualifyClass. The qualifyClass must to be an
	 * <code>StorageNodeFactory</code>
	 * 
	 * @param qualifyClass
	 *            Class to instanciate
	 * @return Instance of OverlayNodeFactory
	 * @throws StorageException
	 *             throw when occur an error
	 */
	public static StorageNodeFactory getInstance(String qualifyClass)
			throws StorageException {

		if (dHashFactory != null) {
			return dHashFactory;

		}
		try {
			Class<?> classIn = Class.forName(qualifyClass);
			dHashFactory = (StorageNodeFactory) classIn.newInstance();
		} catch (ClassNotFoundException e) {
			throw new StorageException("Error creating StorageNodeFactory", e);
		} catch (InstantiationException e) {
			throw new StorageException("Error creating StorageNodeFactory", e);
		} catch (IllegalAccessException e) {
			throw new StorageException("Error creating StorageNodeFactory", e);
		} catch (ClassCastException e) {
			throw new StorageException("Error creating StorageNodeFactory", e);
		}

		return dHashFactory;
	}

	/**
	 * Gets current instance of StorageNodeFactory. If you have not invoke to
	 * <code>getInstance(String qualifyClass)</code>, this return null
	 * 
	 * @return Single current instance of StorageNodeFactory
	 */
	public static StorageNodeFactory getInstance() {
		return dHashFactory;
	}

	/**
	 * Creates a storage node
	 * 
	 * @return StorageNode node
	 * @throws StorageException
	 *             throw when occur an error
	 */
	public abstract StorageNode createNode() throws StorageException;

	/**
	 * Creates a storage node from name
	 * 
	 * @param name
	 *            StorageNode node name
	 * @return StorageNode node
	 * @throws StorageException
	 *             throw when occur an error
	 */
	public abstract StorageNode createNode(String name) throws StorageException;

	/**
	 * Creates node from InetAddress
	 * 
	 * @param inetAddress
	 *            Internet Address
	 * @return StorageNode node
	 * @throws StorageException
	 *             throw when occur an error
	 */
	public abstract StorageNode createNode(InetAddress inetAddress)
			throws StorageException;

	/**
	 * Destroy node by name
	 * 
	 * @param name
	 *            Node name
	 * @throws StorageException
	 *             throw when occur an error
	 */
	public abstract void destroyNode(String name) throws StorageException;
}
