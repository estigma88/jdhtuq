package co.edu.uniquindio.dht.gui.network.task.storageservice;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.storage.StorageNode;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;

public class PutTask extends StorageServiceTask {
    private final File file;

    public PutTask(JFrame jFrame, StorageNode storageNode, File file) {
        super(jFrame, storageNode);
        this.file = file;
    }


    @Override
    protected Void doInBackground() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            BytesResource fileResource = new BytesResource(file.getName(), IOUtils.toByteArray(fileInputStream));

            storageNode.put(fileResource);
        }
        return null;
    }
}
