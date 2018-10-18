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
    @NonNull
    private final Long size;
    @NonNull
    private final String checkSum;

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
