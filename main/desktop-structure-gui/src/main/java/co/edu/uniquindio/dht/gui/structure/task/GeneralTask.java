package co.edu.uniquindio.dht.gui.structure.task;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public abstract class GeneralTask extends SwingWorker<Void, Void> {
    private final JFrame jFrame;

    protected GeneralTask(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            JOptionPane.showMessageDialog(jFrame, e
                    .getCause().getMessage());
        }
    }
}
