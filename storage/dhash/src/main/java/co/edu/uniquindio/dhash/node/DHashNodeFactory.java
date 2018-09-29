/*
 *  DHash project implement a storage management
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
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

import co.edu.uniquindio.dhash.resource.checksum.ChecksumCalculator;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.manager.ResourceManagerFactory;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;

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
    private static final Logger logger = Logger
            .getLogger(DHashNodeFactory.class);

    private final int replicationFactor;
    private final CommunicationManager communicationManager;
    private final OverlayNodeFactory overlayNodeFactory;
    private final SerializationHandler serializationHandler;
    private final ChecksumCalculator checksumeCalculator;
    private final ResourceManagerFactory resourceManagerFactory;
    private final KeyFactory keyFactory;
    private final SequenceGenerator sequenceGenerator;
    private final ExecutorService executorService;

    public DHashNodeFactory(CommunicationManager communicationManager, OverlayNodeFactory overlayNodeFactory, SerializationHandler serializationHandler, ChecksumCalculator checksumeCalculator, ResourceManagerFactory resourceManagerFactory, int replicationFactor, KeyFactory keyFactory, SequenceGenerator sequenceGenerator, ExecutorService executorService) {
        this.communicationManager = communicationManager;
        this.overlayNodeFactory = overlayNodeFactory;
        this.serializationHandler = serializationHandler;
        this.checksumeCalculator = checksumeCalculator;
        this.resourceManagerFactory = resourceManagerFactory;
        this.replicationFactor = replicationFactor;
        this.keyFactory = keyFactory;
        this.sequenceGenerator = sequenceGenerator;
        this.executorService = executorService;
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

        ResourceManager resourceManager = resourceManagerFactory.of(name);

        dhashNode = getDhashNode(name, overlayNode, resourceManager);

        ReAssignObserver reAssignObserver = getReAssignObserver(dhashNode);

        overlayNode.getObservable().addObserver(reAssignObserver);

        dHashEnviroment = getDHashEnviroment(dhashNode, resourceManager);

        communicationManager.addMessageProcessor(name, dHashEnviroment);
        communicationManager.addMessageInputStreamProcessor(name, getMessageStreamProcessor(dhashNode, resourceManager));

        logger.debug("DHash Node " + name + " Created");

        return dhashNode;
    }

    MessageStreamProcessorGateway getMessageStreamProcessor(DHashNode dhashNode, ResourceManager resourceManager) {
        return new MessageStreamProcessorGateway(communicationManager, resourceManager, dhashNode, serializationHandler);
    }

    ReAssignObserver getReAssignObserver(DHashNode dhashNode) {
        return new ReAssignObserver(dhashNode, keyFactory);
    }

    DHashEnvironment getDHashEnviroment(DHashNode dhashNode, ResourceManager resourceManager) {
        return new DHashEnvironment(communicationManager, dhashNode, serializationHandler, checksumeCalculator, resourceManager);
    }

    DHashNode getDhashNode(String name, OverlayNode overlayNode, ResourceManager resourceManager) {
        return new DHashNode(overlayNode, replicationFactor, name, communicationManager, serializationHandler, resourceManager, keyFactory, sequenceGenerator, executorService);
    }

    @Override
    public void destroyNode(StorageNode storageNode, ProgressStatus progressStatus) throws StorageException {
        storageNode.leave(progressStatus);
    }
}
