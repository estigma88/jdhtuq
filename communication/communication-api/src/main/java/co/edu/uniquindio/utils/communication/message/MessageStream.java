package co.edu.uniquindio.utils.communication.message;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Builder
@Data
public class MessageStream {
    private final Message message;
    private transient final InputStream inputStream;
}
