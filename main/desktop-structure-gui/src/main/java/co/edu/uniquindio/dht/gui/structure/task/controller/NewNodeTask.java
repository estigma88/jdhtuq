package co.edu.uniquindio.dht.gui.structure.task.controller;

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NewNodeTask extends ControllerTask {
    private final String nodeName;

    public NewNodeTask(JFrame jFrame, Controller controller, String nodeName) {
        super(jFrame, controller);
        this.nodeName = nodeName;
    }


    @Override
    protected Void doInBackground() throws Exception {
        controller.createNode(nodeName);

        return null;
    }
}
