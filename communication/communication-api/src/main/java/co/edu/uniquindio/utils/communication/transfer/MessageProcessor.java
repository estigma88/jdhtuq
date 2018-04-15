package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.message.Message;

/**
 * Message Processor to handle messages
 */
public interface MessageProcessor {
    /**
     * Process a message and define a response
     * @param request message
     * @return message response
     */
    Message process(Message request);
}
