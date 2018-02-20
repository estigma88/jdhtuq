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

import co.edu.uniquindio.dhash.resource.checksum.ChecksumeCalculator;
import co.edu.uniquindio.dhash.resource.persistence.PersistenceManager;
import co.edu.uniquindio.dhash.resource.persistence.PersistenceManagerFactory;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.dhash.utils.EscapeChars;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.apache.log4j.Logger;

import java.net.InetAddress;

/**
 * The <code>DHashNodeFactory</code> class creates nodes for storage management
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see StorageNode
 * @see DHashNode
 * @since 1.0
 */
public class DHashNodeFactory implements StorageNodeFactory {

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(DHashNodeFactory.class);

    private int replicationFactor;
    private CommunicationManager communicationManager;
    private OverlayNodeFactory overlayNodeFactory;
    private SerializationHandler serializationHandler;
    private ChecksumeCalculator checksumeCalculator;
    private PersistenceManagerFactory persistenceManagerFactory;

    public DHashNodeFactory(CommunicationManager communicationManager, OverlayNodeFactory overlayNodeFactory, SerializationHandler serializationHandler, ChecksumeCalculator checksumeCalculator, PersistenceManagerFactory persistenceManagerFactory, int replicationFactor) {
        this.communicationManager = communicationManager;
        this.overlayNodeFactory = overlayNodeFactory;
        this.serializationHandler = serializationHandler;
        this.checksumeCalculator = checksumeCalculator;
        this.persistenceManagerFactory = persistenceManagerFactory;
        this.replicationFactor = replicationFactor;
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
     * @param name        Node name
     * @param overlayNode Overlay node
     * @return DHash node
     */
    private DHashNode createNode(String name, OverlayNode overlayNode) {

        DHashNode dhashNode;
        DHashEnvironment dHashEnviroment;

        PersistenceManager persistenceManager = persistenceManagerFactory.of(name);

        dhashNode = getDhashNode(name, overlayNode, persistenceManager);

        ReAssignObserver reAssignObserver = getReAssignObserver(dhashNode);

        overlayNode.getObservable().addObserver(reAssignObserver);

        dHashEnviroment = getDHashEnviroment(dhashNode, persistenceManager);

        communicationManager.addObserver(dHashEnviroment);

        logger.debug("DHash Node " + name + " Created");

        return dhashNode;
    }

    ReAssignObserver getReAssignObserver(DHashNode dhashNode) {
        return new ReAssignObserver(dhashNode);
    }

    DHashEnvironment getDHashEnviroment(DHashNode dhashNode, PersistenceManager persistenceManager) {
        return new DHashEnvironment(communicationManager, dhashNode, serializationHandler, checksumeCalculator, persistenceManager);
    }

    DHashNode getDhashNode(String name, OverlayNode overlayNode, PersistenceManager persistenceManager) {
        return new DHashNode(overlayNode, replicationFactor, EscapeChars.forHTML(name, false), communicationManager, serializationHandler, checksumeCalculator, persistenceManager);
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
}
