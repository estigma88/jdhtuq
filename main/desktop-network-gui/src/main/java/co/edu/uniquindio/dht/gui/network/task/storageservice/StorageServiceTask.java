package co.edu.uniquindio.dht.gui.network.task.storageservice;

import co.edu.uniquindio.dht.gui.network.task.GeneralTask;
import co.edu.uniquindio.storage.StorageNode;

import javax.swing.*;

public abstract class StorageServiceTask extends GeneralTask{
    protected final StorageNode storageNode;
    protected StorageServiceTask(JFrame jFrame, StorageNode storageNode) {
        super(jFrame);
        this.storageNode = storageNode;
    }
}
