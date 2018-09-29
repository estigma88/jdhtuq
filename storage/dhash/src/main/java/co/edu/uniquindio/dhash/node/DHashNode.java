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

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.protocol.Protocol.ContainParams;
import co.edu.uniquindio.dhash.protocol.Protocol.PutParams;
import co.edu.uniquindio.dhash.protocol.Protocol.GetParams;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageStream;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

/**
 * The {@code DHashNode} class implements the services of {@code put} and
 * {@code get} of a specific resource.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class DHashNode implements StorageNode {
    private static final Logger logger = Logger
            .getLogger(DHashNode.class);

    private final CommunicationManager communicationManager;
    private final OverlayNode overlayNode;
    private final int replicationFactor;
    private final String name;
    private final SerializationHandler serializationHandler;
    private final ResourceManager resourceManager;
    private final KeyFactory keyFactory;
    private final IdGenerator idGenerator;
    private final ExecutorService executorService;

    public DHashNode(OverlayNode overlayNode, int replicationFactor, String name, CommunicationManager communicationManager, SerializationHandler serializationHandler, ResourceManager resourceManager, KeyFactory keyFactory, IdGenerator idGenerator, ExecutorService executorService) {
        this.overlayNode = overlayNode;
        this.replicationFactor = replicationFactor;
        this.name = name;
        this.communicationManager = communicationManager;
        this.serializationHandler = serializationHandler;
        this.resourceManager = resourceManager;
        this.keyFactory = keyFactory;
        this.idGenerator = idGenerator;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<Resource> get(String id, ProgressStatus progressStatus) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getSync(id, progressStatus);
            } catch (StorageException e) {
                throw new CompletionException("Get failed", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Boolean> put(Resource resource, ProgressStatus progressStatus) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return putSync(resource, progressStatus);
            } catch (StorageException e) {
                throw new CompletionException("Put failed", e);
            }
        }, executorService);
    }

    Resource getSync(String id, ProgressStatus progressStatus) throws StorageException {

        progressStatus.status("overlay-node-lookup", 0L, 1L);

        Key lookupKey = overlayNode.lookUp(keyFactory.newKey(id));

        if (lookupKey == null) {
            logger.error("Impossible to do get to resource: " + id
                    + " in this moment");
            throw new StorageException(
                    "Impossible to do get to resource, lookup fails");
        }

        progressStatus.status("overlay-node-lookup", 1L, 1L);
        progressStatus.status("dhash-file-validation", 0L, 1L);

        Message getMessage = Message.builder()
                .id(idGenerator.newId())
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.CONTAIN)
                .address(Address.builder()
                        .destination(lookupKey.getValue())
                        .source(name)
                        .build())
                .param(ContainParams.RESOURCE_KEY.name(), id)
                .build();

        Boolean hasResource = communicationManager.send(getMessage,
                Boolean.class);

        progressStatus.status("dhash-file-validation", 1L, 1L);

        if (hasResource) {
            Message resourceTransferMessage = Message.builder()
                    .sendType(Message.SendType.REQUEST)
                    .id(idGenerator.newId())
                    .messageType(Protocol.GET)
                    .address(Address.builder()
                            .destination(lookupKey.getValue())
                            .source(name)
                            .build())
                    .param(GetParams.RESOURCE_KEY.name(), id)
                    .build();

            MessageStream resource = communicationManager
                    .receive(resourceTransferMessage, progressStatus::status);

            return serializationHandler.decode(resource.getMessage().getParam(Protocol.GetResponseData.RESOURCE.name()), resource.getInputStream());

        } else {
            return null;
        }

    }

    boolean putSync(Resource resource, ProgressStatus progressStatus) throws StorageException {

        progressStatus.status("overlay-node-lookup", 0L, 1L);

        Key key = keyFactory.newKey(resource.getId());

        logger.debug("Resource to put: [" + resource.getId() + "] Hashing: ["
                + key.getHashing() + "]");

        Key lookupKey = overlayNode.lookUp(key);

        progressStatus.status("overlay-node-lookup", 1L, 1L);

        if (lookupKey == null) {

            logger.error("Imposible to do put to resource: "
                    + resource.getId() + " in this moment");
            throw new StorageException("Imposible to do put to resource: "
                    + resource.getId() + " in this moment");
        }

        logger.debug("Lookup key for " + key.getHashing() + ": ["
                + lookupKey.getValue() + "]");

        return put(resource, lookupKey, true, progressStatus);
    }

    /**
     * Replicates the specified file in its successors.
     *
     * @param resourceId The specified {@link Resource} to replicate.
     * @throws OverlayException
     */
    public void replicateData(String resourceId, ProgressStatus progressStatus)
            throws OverlayException, StorageException {
        Key[] succesorList = overlayNode.getNeighborsList();

        for (int i = 0; i < Math.min(replicationFactor, succesorList.length); i++) {
            Resource resource = resourceManager.find(resourceId);

            logger
                    .debug("Replicate File: [" + resource.getId()
                            + "] Hashing: ["
                            + succesorList[i].getHashing() + "]");
            logger.debug("Replicate to " + succesorList[i].getHashing());

            put(resource, succesorList[i], false, progressStatus);
        }
    }

    /**
     * Puts the specified resource into the network.
     *
     * @param resource       The resource to put.
     * @param lookupKey      The key where the file will be put.
     * @param replicate      Determines if the file will be replicated.
     * @param progressStatus
     * @return False if the resource already exists, true if it does not exist
     */
    boolean put(Resource resource, Key lookupKey, boolean replicate, ProgressStatus progressStatus) throws StorageException {
        Message putMessage = Message.builder()
                .id(idGenerator.newId())
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.PUT)
                .address(Address.builder()
                        .destination(lookupKey.getValue())
                        .source(name)
                        .build())
                .param(Protocol.PutDatas.RESOURCE.name(), serializationHandler.encode(resource))
                .param(PutParams.REPLICATE.name(), String.valueOf(replicate))
                .build();

        communicationManager.send(MessageStream.builder()
                .message(putMessage)
                .inputStream(resource.getInputStream())
                .size(resource.getSize())
                .build(), progressStatus::status);

        return true;
    }

    /**
     * Relocates the resources of the node.
     *
     * @param key The node where the files will be relocated.
     */
    public void relocateAllResources(Key key, ProgressStatus progressStatus) throws StorageException {

        Set<String> resourcesNames = resourceManager.getAllKeys();

        logger.info("Relocating Files...");
        logger.debug("Number of files: [" + resourcesNames.size() + "]");
        int filesRelocated = 0;

        for (String name : resourcesNames) {
            Resource resource = resourceManager.find(name);

            Key fileKey = getFileKey(name);

            if (!fileKey.isBetween(key, overlayNode.getKey())) {
                boolean relocate = put(resource, key, false, progressStatus);

                filesRelocated++;
            }
        }

        logger.info("Files relocated: [" + filesRelocated + "]");
    }

    Key getFileKey(String name) {
        return keyFactory.newKey(name);
    }

    public void leave(ProgressStatus progressStatus) throws StorageException {
        try {
            Key[] keys = overlayNode.leave();

            Set<String> resourcesNames = resourceManager.getAllKeys();

            logger.info("Leaving...");
            logger.debug("Number of files to transfer: ["
                    + resourcesNames.size() + "]");

            if (!keys[0].equals(overlayNode.getKey())) {
                for (String name : resourcesNames) {
                    Resource resource = resourceManager.find(name);

                    put(resource, keys[0], false, progressStatus);
                }
            }

            resourceManager.deleteAll();

            communicationManager.removeObserver(overlayNode.getKey().getValue());
        } catch (OverlayException e) {
            logger.error("Error while leaving dhash node: '"
                    + overlayNode.getKey().toString() + "'");
        }
    }

    /**
     * Gets the key of the dht node.
     *
     * @return The {@link Key} of the dht node.
     */
    public String getName() {
        return name;
    }

    public OverlayNode getOverlayNode() {
        return overlayNode;
    }
}
