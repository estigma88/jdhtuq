package co.edu.uniquindio.dhash.resource.serialization.jackson;

import co.edu.uniquindio.dhash.resource.FileResource;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.io.InputStream;

@JsonPOJOBuilder(withPrefix="")
@JsonIgnoreProperties(ignoreUnknown = true)
public interface FileResourceBuilderMixIn {
    @JacksonInject
    FileResource.WithInputStreamBuilder inputStream(InputStream inputStream);
}
