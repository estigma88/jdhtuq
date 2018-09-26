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

package co.edu.uniquindio.dhash.resource.manager;

import co.edu.uniquindio.storage.resource.Resource;

import java.io.OutputStream;
import java.util.Set;

/**
 * Resource manager
 */
public interface ResourceManager {
    /**
     * Save a ressource
     *
     * @param resource resource
     */
    Resource save(Resource resource);

    /**
     * Delete all resources
     */
    void deleteAll();

    /**
     * Validate if the resource with key exists
     *
     * @param key name of the resource
     * @return true if the resource exists
     */
    boolean hasResource(String key);

    /*
     * Get all keys resources
     *
     * @return keys resources
     */
    Set<String> getAllKeys();

    /**
     * Find a resource by key
     *
     * @param key resource key
     * @return The resource with the key, null if there is not a resource with that key
     */
    Resource find(String key);
}
