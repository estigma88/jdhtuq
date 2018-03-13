package co.edu.uniquindio.dhash.resource.manager;

import co.edu.uniquindio.storage.resource.Resource;

import java.util.Set;

public interface ResourceManager {
    void save(Resource resource);

    void deleteAll();

    boolean hasResource(String key);

    Set<String> getAllKeys();

    Resource find(String key);
}
