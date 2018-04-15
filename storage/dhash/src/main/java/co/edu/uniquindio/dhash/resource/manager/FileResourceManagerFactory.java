package co.edu.uniquindio.dhash.resource.manager;

import java.util.HashSet;

public class FileResourceManagerFactory implements ResourceManagerFactory {
    private final String directory;

    public FileResourceManagerFactory(String directory) {
        this.directory = directory;
    }

    @Override
    public ResourceManager of(String name) {
        return new FileResourceManager(directory, name, new HashSet<>());
    }
}
