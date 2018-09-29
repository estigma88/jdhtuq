package co.edu.uniquindio.utils.communication.transfer;

public interface ProgressStatusTransfer {
    void status(String name, Long current, Long size);
}
