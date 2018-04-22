package co.edu.uniquindio.dht.gui.network.task.storageservice;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.storage.StorageNode;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetTask extends StorageServiceTask {
    private final String resourceId;
    private final String resourceDirectory;

    public GetTask(JFrame jFrame, StorageNode storageNode, String resourceId, String resourceDirectory) {
        super(jFrame, storageNode);
        this.resourceId = resourceId;
        this.resourceDirectory = resourceDirectory;
    }


    @Override
    protected Void doInBackground() throws Exception {
        BytesResource resource = (BytesResource) storageNode.get(resourceId);

        Files.createDirectories(Paths.get(resourceDirectory + storageNode.getName() + "/gets/"));
        Files.copy(new ByteArrayInputStream(resource.getBytes()), Paths.get(resourceDirectory + storageNode.getName() + "/gets/" + resource.getId()));

        return null;
    }
}
