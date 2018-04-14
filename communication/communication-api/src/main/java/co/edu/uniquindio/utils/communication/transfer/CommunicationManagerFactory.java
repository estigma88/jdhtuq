package co.edu.uniquindio.utils.communication.transfer;

/**
 * Factory to build CommunicationManagers
 */
public interface CommunicationManagerFactory {
    /**
     * Create a new CommunicationManager
     * @param name of the CommunicationManager
     * @return CommunicationManager
     */
    CommunicationManager newCommunicationManager(String name);
}
