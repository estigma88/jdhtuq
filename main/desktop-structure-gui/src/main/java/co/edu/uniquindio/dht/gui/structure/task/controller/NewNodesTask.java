package co.edu.uniquindio.dht.gui.structure.task.controller;

import co.edu.uniquindio.dht.gui.structure.controller.Controller;

import javax.swing.*;

public class NewNodesTask extends ControllerTask {
    private final Integer amount;

    public NewNodesTask(JFrame jFrame, Controller controller, Integer amount) {
        super(jFrame, controller);
        this.amount = amount;
    }


    @Override
    protected Void doInBackground() throws Exception {
        controller.createNnodes(amount);

        return null;
    }
}
