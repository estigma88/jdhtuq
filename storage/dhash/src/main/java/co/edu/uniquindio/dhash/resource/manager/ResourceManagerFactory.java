package co.edu.uniquindio.dhash.resource.manager;

/**
 * Resource manager factory
 */
public interface ResourceManagerFactory {
    /**
     * Creates a resource manager by name
     *
     * @param name resource manager name
     * @return resource manager
     */
    ResourceManager of(String name);
}
