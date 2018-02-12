/*
 *  LookupService project defined all services an lookup algorithm
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

package co.edu.uniquindio.overlay;

import java.net.InetAddress;

/**
 * The <code>OverlayNodeFactory</code> abstract class defined methods for to
 * create Overlay nodes. The class is a single instance.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public abstract class OverlayNodeFactory {

	/**
	 * Single OverlayNodeFactory instance
	 */
	private static OverlayNodeFactory overlayNodeFactory;

	/**
	 * Creates a single instance of qualifyClass. The qualifyClass must to be an
	 * <code>OverlayNodeFactory</code>
	 * 
	 * @param qualifyClass
	 *            Class to instanciate
	 * @return Instance of OverlayNodeFactory
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	public static OverlayNodeFactory getInstance(String qualifyClass)
			throws OverlayException {

		if (overlayNodeFactory != null) {
			return overlayNodeFactory;
		}

		try {
			Class<?> classIn = Class.forName(qualifyClass);
			overlayNodeFactory = (OverlayNodeFactory) classIn.newInstance();
		} catch (ClassNotFoundException e) {
			throw new OverlayException("Error creating OverlayNodeFactory", e);
		} catch (InstantiationException e) {
			throw new OverlayException("Error creating OverlayNodeFactory", e);
		} catch (IllegalAccessException e) {
			throw new OverlayException("Error creating OverlayNodeFactory", e);
		} catch (ClassCastException e) {
			throw new OverlayException("Error creating OverlayNodeFactory", e);
		}

		return overlayNodeFactory;
	}

	/**
	 * Gets current instance of OverlayNodeFactory. If you have not invoke to
	 * <code>getInstance(String qualifyClass)</code>, this return null
	 * 
	 * @return Single current instance of OverlayNodeFactory
	 */
	public static OverlayNodeFactory getInstance() {
		return overlayNodeFactory;
	}

	/**
	 * Creates a node
	 * 
	 * @return Overlay node
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	public abstract OverlayNode createNode() throws OverlayException;

	/**
	 * Creates a node with a specified name
	 * 
	 * @param name
	 *            of node
	 * @return node
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	public abstract OverlayNode createNode(String name) throws OverlayException;

	/**
	 * Creates a node with a specified InetAddress
	 * 
	 * @param inetAddress
	 *            Internet address
	 * @return node Overlay node
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	public abstract OverlayNode createNode(InetAddress inetAddress)
			throws OverlayException;

	/**
	 * Destroy node by name
	 * 
	 * @param name
	 *            Node name
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	public abstract void destroyNode(String name) throws OverlayException;

}
