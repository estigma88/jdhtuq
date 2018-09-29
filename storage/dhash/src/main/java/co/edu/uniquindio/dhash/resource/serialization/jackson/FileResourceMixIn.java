package co.edu.uniquindio.dhash.resource.serialization.jackson;

import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.utils.communication.message.Address;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.InputStream;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

@JsonDeserialize(builder = FileResource.WithInputStreamBuilder.class)
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface FileResourceMixIn {
    @JsonIgnore
    InputStream getInputStream();
}
