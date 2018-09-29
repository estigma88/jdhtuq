package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.Resource;
import lombok.Data;

import java.io.InputStream;

@Data
public abstract class BasicResource implements Resource{
    private final String id;
    private final InputStream inputStream;
    private final Long size;

    protected BasicResource(String id, InputStream inputStream, Long size) {
        this.id = id;
        this.inputStream = inputStream;
        this.size = size;
    }
}
