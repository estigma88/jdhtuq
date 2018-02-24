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
public interface OverlayNodeFactory {
	/**
	 * Creates a node
	 * 
	 * @return Overlay node
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	OverlayNode createNode() throws OverlayException;

	/**
	 * Creates a node with a specified name
	 * 
	 * @param name
	 *            of node
	 * @return node
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	OverlayNode createNode(String name) throws OverlayException;

	/**
	 * Creates a node with a specified InetAddress
	 * 
	 * @param inetAddress
	 *            Internet address
	 * @return node Overlay node
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	OverlayNode createNode(InetAddress inetAddress)
			throws OverlayException;

	/**
	 * Destroy node by name
	 * 
	 * @param name
	 *            Node name
	 * @throws OverlayException
	 *             throw when occur an error
	 */
	void destroyNode(String name) throws OverlayException;

}
