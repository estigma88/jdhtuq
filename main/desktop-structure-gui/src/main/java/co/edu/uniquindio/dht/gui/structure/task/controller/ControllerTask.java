package co.edu.uniquindio.dht.gui.structure.task.controller;

import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.dht.gui.structure.task.GeneralTask;

import javax.swing.*;

public abstract class ControllerTask extends GeneralTask {
    protected final Controller controller;

    protected ControllerTask(JFrame jFrame, Controller controller) {
        super(jFrame);
        this.controller = controller;
    }
}
