package co.edu.uniquindio.utils.communication.message;

import lombok.Builder;
import lombok.Data;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

@Builder
@Data
public class MessageStream implements Closeable{
    private final Message message;
    private transient final InputStream inputStream;
    private final Long size;

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
