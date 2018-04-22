package co.edu.uniquindio.dht.gui.structure.task.controller;

import co.edu.uniquindio.dht.gui.structure.controller.Controller;

import javax.swing.*;

public class DeleteNodeTask extends ControllerTask {
    private final String nodeName;

    public DeleteNodeTask(JFrame jFrame, Controller controller, String nodeName) {
        super(jFrame, controller);
        this.nodeName = nodeName;
    }


    @Override
    protected Void doInBackground() throws Exception {
        controller.deleteNode(nodeName);

        return null;
    }
}
