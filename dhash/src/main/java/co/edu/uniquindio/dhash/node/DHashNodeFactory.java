/*
 *  DHash project implement a storage management 
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

package co.edu.uniquindio.dhash.node;

import java.net.InetAddress;

import co.edu.uniquindio.dhash.configurations.DHashProperties;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.EscapeChars;
import co.edu.uniquindio.utils.communication.configurations.CommunicationProperties;
import co.edu.uniquindio.utils.communication.configurations.CommunicationPropertiesException;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerCache;
import co.edu.uniquindio.utils.hashing.DigestGenerator;
import org.apache.log4j.Logger;

/**
 * The <code>DHashNodeFactory</code> class creates nodes for storage management
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see StorageNode
 * @see DHashNode
 */
public class DHashNodeFactory extends StorageNodeFactory {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger
			.getLogger(DHashNodeFactory.class);

	/**
	 * Communication manager
	 */
	private CommunicationManager communicationManager;
	
	/**
	 * Qualified name of the class that handles digest
	 */
	private static final String DIGEST_CLASS = "co.edu.uniquindio.utils.hashing.DigestGeneratorImp";

	/**
	 * Attribute that difine communication name
	 */
	public static final String DHASH = DHashNodeFactory.class.getName();
	/**
	 * Attribute that define properties file communication
	 */
	private static final String COMMUNICATION_PROPERTIES = "resources/dhash_properties/communication.xml";

	/**
	 * Overlay node factory to creates Overlay nodes
	 */
	private OverlayNodeFactory overlayNodeFactory;

	/**
	 * Builds a DHashNodeFactory. Load a CommunicationProperties from
	 * COMMUNICATION_PROPERTIES, initialized DigestGenerator from DIGEST_CLASS
	 * and creates OverlayNodeFactory from property factory class in properties
	 * file dhash.xml
	 */
	public DHashNodeFactory() {
		CommunicationProperties communicationProperties = null;

		try {
			communicationProperties = CommunicationProperties.load(
					DHashNodeFactory.class, COMMUNICATION_PROPERTIES);

			communicationManager=CommunicationManagerCache.createCommunicationManager(DHASH, communicationProperties);

			overlayNodeFactory = OverlayNodeFactory.getInstance(DHashProperties
					.getInstance().getOverlay().getFactoryClass());

		} catch (CommunicationPropertiesException e) {
			logger.fatal("The communication properties is not load", e);
		} catch (OverlayException e) {
			logger.fatal("The communication properties is not load", e);
		}

		DigestGenerator.load(DIGEST_CLASS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.edu.uniquindio.storage.StorageNodeFactory#createNode(java.lang.String)
	 */
	public StorageNode createNode(String name) throws DHashFactoryException {

		OverlayNode overlayNode;
		DHashNode dhashNode;

		try {
			overlayNode = overlayNodeFactory.createNode(name);

			dhashNode = createNode(name, overlayNode);

		} catch (OverlayException e) {
			throw new DHashFactoryException("Error creating overlay node", e);
		}

		return dhashNode;
	}

	/**
	 * Creates dhash node by name and overlay node. The name is escaped using
	 * <code>EscapeChars.forHTML</code>. Adds observer to the communication
	 * 
	 * @param name
	 *            Node name
	 * @param overlayNode
	 *            Overlay node
	 * @return DHash node
	 */
	private DHashNode createNode(String name, OverlayNode overlayNode) {

		DHashNode dhashNode;
		DHashEnvironment dHashEnviroment;

		dhashNode = new DHashNode(overlayNode, EscapeChars.forHTML(name, false));

		dHashEnviroment = new DHashEnvironment(dhashNode);

		communicationManager.addObserver(dHashEnviroment);

		logger.debug("DHash Node " + name + " Created");

		return dhashNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see co.edu.uniquindio.storage.StorageNodeFactory#createNode()
	 */
	public StorageNode createNode() throws DHashFactoryException {

		OverlayNode overlayNode;
		DHashNode dhashNode;

		try {
			overlayNode = overlayNodeFactory.createNode();

			dhashNode = createNode(overlayNode.getKey().getValue(), overlayNode);

		} catch (OverlayException e) {
			throw new DHashFactoryException("Error creating overlay node", e);
		}

		return dhashNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.edu.uniquindio.storage.StorageNodeFactory#createNode(java.net.InetAddress)
	 */
	public StorageNode createNode(InetAddress inetAddress)
			throws DHashFactoryException {
		OverlayNode overlayNode;
		DHashNode dhashNode;

		try {
			overlayNode = overlayNodeFactory.createNode(inetAddress);

			dhashNode = createNode(overlayNode.getKey().getValue(), overlayNode);

		} catch (OverlayException e) {
			throw new DHashFactoryException("Error creating overlay node", e);
		}

		return dhashNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.edu.uniquindio.storage.StorageNodeFactory#destroyNode(java.lang.String)
	 */
	/**
	 * Removed observer from communication
	 */
	public void destroyNode(String name) {
		communicationManager.removeObserver(name);
	}
}
