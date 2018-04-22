package co.edu.uniquindio.dht.gui.structure.task.controller;

import co.edu.uniquindio.dht.gui.structure.controller.Controller;

import javax.swing.*;
import java.io.File;

public class NewNodesFromFileTask extends ControllerTask {
    private final File file;

    public NewNodesFromFileTask(JFrame jFrame, Controller controller, File file) {
        super(jFrame, controller);
        this.file = file;
    }


    @Override
    protected Void doInBackground() throws Exception {
        controller.createNodesFromPath(file);

        return null;
    }
}
