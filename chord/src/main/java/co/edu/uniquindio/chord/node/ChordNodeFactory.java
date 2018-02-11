/*
 *  Chord project implement of lookup algorithm Chord 
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
 *  
 */

package co.edu.uniquindio.chord.node;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import co.edu.uniquindio.chord.Chord;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.configurations.CommunicationProperties;
import co.edu.uniquindio.utils.communication.configurations.CommunicationPropertiesException;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerCache;
import co.edu.uniquindio.utils.hashing.HashingGenerator;
import co.edu.uniquindio.utils.hashing.Key;
import org.apache.log4j.Logger;

/**
 * The <code>ChordNodeFactory</code> class creates nodes <code>ChordNode</code>.
 * Load properties file for communication called
 * chord_properties/communication.xml and initialized hashing class
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see Chord
 * @see ChordNode
 */
public class ChordNodeFactory extends OverlayNodeFactory {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger
			.getLogger(ChordNodeFactory.class);
	
	/**
	 * Communication manager
	 */
	private CommunicationManager communicationManager;

	/**
	 * Attribute that difine communication name
	 */
	public static final String CHORD = ChordNodeFactory.class.getName();

	/**
	 * Qualified name of the class that handles hashing
	 */
	public static final String HASHING_CLASS = "co.edu.uniquindio.utils.hashing.HashingGeneratorImp";

	/**
	 * Attribute that define properties file communication
	 */
	private static final String COMMUNICATION_PROPERTIES = "resources/chord_properties/communication.xml";

	/**
	 * Allows to create a node with no specified name.
	 */
	private int nodeKey;
	/**
	 * List of names of the nodes created
	 */
	private Set<String> names;

	/**
	 * Returns the concrete factory created.
	 * 
	 * @return {@link ChordNodeFactory}
	 * @throws ChordNodeFactoryException
	 *             if there is an error when trying to instantiate the class of
	 *             the concrete factory.
	 */
	public ChordNodeFactory() {
		CommunicationProperties communicationProperties = null;

		try {
			communicationProperties = CommunicationProperties.load(
					ChordNodeFactory.class, COMMUNICATION_PROPERTIES);

			communicationManager=CommunicationManagerCache.createCommunicationManager(CHORD, communicationProperties);

		} catch (CommunicationPropertiesException e) {
			logger.fatal("The communication properties is not load", e);
		}

		HashingGenerator.load(HASHING_CLASS);

		names = new HashSet<String>();

	}

	ChordNodeFactory(CommunicationManager communicationManager, Set<String> names) {
		this.communicationManager = communicationManager;
		this.names = names;
	}

	/**
	 * Creates a ChordNode with a default name and starts the node.
	 * 
	 * @return {@link Chord}
	 * @throws ChordNodeFactoryException
	 * 
	 */
	public Chord createNode() throws ChordNodeFactoryException {

		nodeKey++;
		while (names.contains(String.valueOf(nodeKey))) {
			nodeKey++;
		}

		return createNode(String.valueOf(nodeKey));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.edu.uniquindio.overlay.OverlayNodeFactory#createNode(java.lang.String)
	 */
	public Chord createNode(String name) throws ChordNodeFactoryException {
		ChordNode nodeChord;
		NodeEnvironment nodeEnviroment;
		Key key;

		if (!names.contains(name)) {
			names.add(name);
		} else {
			NameAlreadyInUseException exception = new NameAlreadyInUseException(
					"The specified name is already being used");

			logger.fatal("The specified name is already being used", exception);

			throw exception;
		}

		key = getKey(name);
		nodeChord = getNodeChord(key);

		logger.info("Created node with name '" + nodeChord.getKey().getValue()
				+ "' and hashing '" + nodeChord.getKey().getStringHashing()
				+ "'");

		nodeEnviroment = getNodeEnviroment(nodeChord);

		communicationManager.addObserver(nodeEnviroment);

		BootStrap.boot(nodeChord);

		nodeEnviroment.startStableRing();

		return nodeChord;
	}

	Key getKey(String name) {
		return new Key(name);
	}

	NodeEnvironment getNodeEnviroment(ChordNode nodeChord) {
		return new NodeEnvironment(nodeChord);
	}

	ChordNode getNodeChord(Key key) {
		return new ChordNode(key);
	}

	/**
	 * Removes the node form the ring.
	 * 
	 * @param key
	 *            The key of the node that will be destroyed.
	 * 
	 */
	public void destroyNode(String name) {
		names.remove(name);

		communicationManager.removeObserver(name);
	}

	/**
	 * Creates node from Internet Address
	 */
	public OverlayNode createNode(InetAddress inetAddress)
			throws ChordNodeFactoryException {
		return createNode(inetAddress.getHostAddress());
	}

	Set<String> getNames() {
		return names;
	}
}
