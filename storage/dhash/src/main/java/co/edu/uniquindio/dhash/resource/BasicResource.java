package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.InputStream;

@Data
public abstract class BasicResource implements Resource{
    private final String id;
    private final InputStream inputStream;

    protected BasicResource(String id, InputStream inputStream) {
        this.id = id;
        this.inputStream = inputStream;
    }
}
