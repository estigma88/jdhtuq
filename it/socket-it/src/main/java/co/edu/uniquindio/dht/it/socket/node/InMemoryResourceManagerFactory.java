package co.edu.uniquindio.dht.it.socket.node;

import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.manager.ResourceManagerFactory;

public class InMemoryResourceManagerFactory implements ResourceManagerFactory{
    @Override
    public ResourceManager of(String name) {
        return new InMemoryResourceManager();
    }
}
