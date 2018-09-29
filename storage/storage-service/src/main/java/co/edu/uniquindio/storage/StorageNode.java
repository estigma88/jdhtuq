/*
 *  StorageService project defined all services an storage management
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

package co.edu.uniquindio.storage;

import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;

import java.util.concurrent.CompletableFuture;

/**
 * The {@code StorageNode} interface offer the services of {@code put} and {@code
 * get} of a specific file.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see StorageNodeFactory
 * @since 1.0
 */
public interface StorageNode {
    /**
     * Gets a resource with the given name.
     *
     * @param id The name of the required resource.
     * @return The {@link Resource}
     */
    CompletableFuture<Resource> get(String id, ProgressStatus progressStatus);

    /**
     * Puts the specified resource into the network.
     *
     * @param resource The resource that will be put.
     * @return False if the resource already exists, true if the put was successful
     */
    CompletableFuture<Boolean> put(Resource resource, ProgressStatus progressStatus);

    /**
     * Allows the node to leave in a regular mode.
     *
     * @throws StorageException throw when occur an error.
     */
    void leave(ProgressStatus progressStatus) throws StorageException;

    /**
     * Gets the name of the storage node.
     */
    String getName();
}
