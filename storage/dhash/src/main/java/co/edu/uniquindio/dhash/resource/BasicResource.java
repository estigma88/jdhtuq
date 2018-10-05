package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.Resource;
import lombok.Data;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

@Data
public abstract class BasicResource implements Resource{
    @NonNull
    private final String id;
    @NonNull
    private final InputStream inputStream;
    private final Long size;

    protected BasicResource(String id, InputStream inputStream, Long size) {
        this.id = id;
        this.inputStream = inputStream;
        this.size = size;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
