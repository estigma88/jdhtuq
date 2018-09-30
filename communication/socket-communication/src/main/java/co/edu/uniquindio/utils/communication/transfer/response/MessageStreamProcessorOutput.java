package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.message.MessageStream;

import java.io.OutputStream;

public interface MessageStreamProcessorOutput {
    MessageStream process(MessageStream messageStream, OutputStream outputStream);
}
