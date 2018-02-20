package co.edu.uniquindio.storage.resource;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
    /**
     * Gets key of resource
     *
     * @return Key of resource
     */
    String getKey();

    byte[] getBytes();
}
