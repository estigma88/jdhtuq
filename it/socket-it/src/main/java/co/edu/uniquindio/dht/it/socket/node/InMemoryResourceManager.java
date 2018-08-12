package co.edu.uniquindio.dht.it.socket.node;

import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.storage.resource.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryResourceManager implements ResourceManager{
    private final Map<String, Resource> resources;

    public InMemoryResourceManager() {
        resources = new HashMap<>();
    }

    @Override
    public void save(Resource resource) {
        resources.put(resource.getId(), resource);
    }

    @Override
    public void deleteAll() {
        resources.clear();
    }

    @Override
    public boolean hasResource(String key) {
        return resources.containsKey(key);
    }

    @Override
    public Set<String> getAllKeys() {
        return resources.keySet();
    }

    @Override
    public Resource find(String key) {
        return resources.get(key);
    }
}
