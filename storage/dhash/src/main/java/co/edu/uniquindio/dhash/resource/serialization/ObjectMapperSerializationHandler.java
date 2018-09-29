package co.edu.uniquindio.dhash.resource.serialization;

import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.dhash.resource.serialization.jackson.FileResourceBuilderMixIn;
import co.edu.uniquindio.dhash.resource.serialization.jackson.FileResourceMixIn;
import co.edu.uniquindio.storage.resource.Resource;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ObjectMapperSerializationHandler implements SerializationHandler {
    private final ObjectMapper objectMapper;

    public ObjectMapperSerializationHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.WRAPPER_OBJECT);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        this.objectMapper.addMixIn(FileResource.class, FileResourceMixIn.class)
                .addMixIn(FileResource.WithInputStreamBuilder.class, FileResourceBuilderMixIn.class);
    }

    @Override
    public String encode(Resource resource) {
        try {
            return objectMapper.writeValueAsString(resource);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Problem with json convertion", e);
        }
    }

    @Override
    public Resource decode(String resource, InputStream inputStream) {
        try {
            InjectableValues inject = new InjectableValues.Std()
                    .addValue(InputStream.class, inputStream);

            return objectMapper.reader(inject)
                    .forType(Resource.class)
                    .readValue(resource);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem decoding", e);
        }
    }
}
