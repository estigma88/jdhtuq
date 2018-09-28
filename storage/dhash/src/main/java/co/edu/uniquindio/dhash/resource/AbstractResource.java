package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.Resource;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public abstract class AbstractResource implements Resource{
    private final String id;

    protected AbstractResource(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
