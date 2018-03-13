package co.edu.uniquindio.dhash.resource.manager;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.storage.resource.Resource;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Set;

public class FileResourceManager implements ResourceManager {
    private final String directory;
    private final String name;
    private final Set<String> keys;

    public FileResourceManager(String directory, String name, Set<String> keys) {
        this.directory = directory;
        this.name = name;
        this.keys = keys;
    }

    @Override
    public void save(Resource resource) {
        FileOutputStream fileOutputStream;
        StringBuilder directoryPath;
        File directoryFile;

        BytesResource bytesResource = (BytesResource) resource;
        try {
            byte[] bytesFile = bytesResource.getBytes();
            if (bytesFile == null) {
                return;
            }

            directoryPath = new StringBuilder(directory);
            directoryPath.append(name);
            directoryPath.append("/");

            directoryFile = new File(directoryPath.toString());
            directoryFile.mkdirs();

            File file = new File(directoryFile, resource.getId());

            fileOutputStream = new FileOutputStream(file);

            fileOutputStream.write(bytesFile);

            fileOutputStream.close();

            bytesFile = null;

            keys.add(resource.getId());

        } catch (IOException e) {
            throw new IllegalStateException("Error reading file", e);
        }
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasResource(String key) {
        return keys.contains(key);
    }

    @Override
    public Set<String> getAllKeys() {
        return keys;
    }

    @Override
    public Resource find(String key) {
        if (hasResource(key)) {
            StringBuilder directoryPath = new StringBuilder(directory);
            directoryPath.append(name);
            directoryPath.append("/");

            File directoryFile = new File(directoryPath.toString());
            directoryFile.mkdirs();

            File file = new File(directoryFile, key);

            try {
                return new BytesResource(key, IOUtils.toByteArray(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("", e);
            } catch (IOException e) {
                throw new RuntimeException("", e);
            }
        } else {
            return null;
        }
    }
}
