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

package co.edu.uniquindio.dhash.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.storage.resource.ResourceException;
import co.edu.uniquindio.storage.resource.SerializableResource;
import co.edu.uniquindio.storage.resource.SerializableResource.ResourceParams;
import co.edu.uniquindio.utils.logger.LoggerDHT;

/**
 * The {@code ResourceManager} class is responsible for managing the resources
 * on the node, maintaining reference and persist and delete.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class ResourceManager {

	/**
	 * Logger
	 */
	private static final LoggerDHT logger = LoggerDHT
			.getLogger(ResourceManager.class);

	/**
	 * Maps the name of the file and its respective path.
	 */
	private Map<String, Resource> resources;

	/**
	 * Node name
	 */
	private String name;

	/**
	 * Is the constructor of the class. Sets the idDHashNode and creates the
	 * paths where the files will be store.
	 * 
	 * @param idDHashNode
	 */
	public ResourceManager(String name) {
		this.resources = new HashMap<String, Resource>();
		this.name = name;
	}

	/**
	 * Stores a new reference of a resource with its name.
	 * 
	 * @param key
	 *            The name of the file to map.
	 * @param bytes
	 *            The resource serialized.
	 */
	public void put(String key, byte[] bytes) {
		try {
			put(key, SerializableResource.valueOf(bytes));
		} catch (IOException e) {
			logger.error("Error put in ResourceManager", e);
		} catch (ClassNotFoundException e) {
			logger.error("Error put in ResourceManager", e);
		}
	}

	/**
	 * Stores a new reference of a resource with its name and persist the
	 * resource sending two param, MANAGER_NAME and PERSIST_TYPE
	 * 
	 * @param key
	 *            The name of the file to map.
	 * @param resource
	 *            The resource.
	 */
	public void put(String key, SerializableResource resource) {

		resources.put(key, resource);

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(ResourceParams.MANAGER_NAME.name(), name);
			params.put(ResourceParams.PERSIST_TYPE.name(), "put");

			resource.persist(params);
		} catch (ResourceException e) {
			logger.error("Error persist", e);
		}

		logger.fine("Resource in node: " + name + " " + resources.toString());

		logger.info("Resource in node: " + name);
	}

	/**
	 * Verifies if the node has the specified resource.
	 * 
	 * @param key
	 *            The name of the resource that will be verified.
	 * @return {@code true} if the node has the specified resource, {@code
	 *         false} if the node does not have the specified resource
	 */
	public boolean hasResource(String key) {
		return resources.containsKey(key);
	}

	/**
	 * Gets the resources of the specified key name.
	 * 
	 * @param key
	 *            The name of the resource.
	 * @return The {@link SerializableResource} associated with that name.
	 */
	public Resource get(String key) {

		if (hasResource(key)) {
			return resources.get(key);
		} else {
			throw new IllegalArgumentException("The resource not found");
		}

	}

	/**
	 * Gets the list of the names of the resources.
	 * 
	 * @return A {@link Set} with the names of the files.
	 */
	public Set<String> getResourcesNames() {
		return resources.keySet();
	}

	/**
	 * Deletes a specified resource with the name. Called to
	 * <code>Resource.delete</code> and sends the param MANAGER_NAME
	 * 
	 * @param name
	 *            The name of the resource that will be deleted.
	 */
	public boolean delete(String name) {

		Resource resource = resources.remove(name);

		if (resource != null) {
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ResourceParams.MANAGER_NAME.name(), name);

				resource.delete(params);

				logger.fine("Delete Resource: '" + resource.getKey() + "'");

				return true;
			} catch (ResourceException e) {
				logger.warn("Resource not Delete for complet"
						+ resource.getKey() + "'", e);

				return false;
			}

		} else {
			logger.warn("Resource not Delete for complet" + name + "'");

			return true;
		}

	}

	/**
	 * Delete all resources. Invoke for each resource
	 * <code>Resource.delete</code>
	 */
	public void deleteResources() {
		Set<String> resourcesNames = resources.keySet();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ResourceParams.MANAGER_NAME.name(), name);

		for (String name : resourcesNames) {
			Resource resource = resources.get(name);

			try {
				resource.delete(params);
			} catch (ResourceException e) {
				logger.warn("Resource not Delete for complet"
						+ resource.getKey() + "'", e);
			}
		}
	}
}
