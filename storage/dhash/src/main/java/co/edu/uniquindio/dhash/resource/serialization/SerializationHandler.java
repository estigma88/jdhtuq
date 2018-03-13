package co.edu.uniquindio.dhash.resource.serialization;

import co.edu.uniquindio.storage.resource.Resource;

public interface SerializationHandler {
    byte[] encode(Resource resource);

    Resource decode(byte[] bytes);
}
