package co.edu.uniquindio.dhash.resource.serialization;

import co.edu.uniquindio.storage.resource.Resource;

/**
 * Serialization handler
 */
public interface SerializationHandler {
    /**
     * Encode a resource
     *
     * @param resource resource
     * @return bytes
     */
    byte[] encode(Resource resource);

    /**
     * Decode a resource
     *
     * @param bytes data
     * @return Resource
     */
    Resource decode(byte[] bytes);
}
