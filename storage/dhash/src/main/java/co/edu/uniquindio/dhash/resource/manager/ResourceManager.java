package co.edu.uniquindio.dhash.resource.manager;

import co.edu.uniquindio.storage.resource.Resource;

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
    void save(Resource resource);

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
