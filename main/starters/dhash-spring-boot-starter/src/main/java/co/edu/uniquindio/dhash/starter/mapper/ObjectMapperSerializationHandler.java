package co.edu.uniquindio.dhash.starter.mapper;

import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.dhash.resource.NetworkResource;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.dhash.starter.mapper.jackson.FileResourceMixin;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class ObjectMapperSerializationHandler implements SerializationHandler {
    private final ObjectMapper objectMapper;

    public ObjectMapperSerializationHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.objectMapper.addMixIn(FileResource.class, FileResourceMixin.class);
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
            NetworkResource networkResource = objectMapper.readValue(resource, NetworkResource.class);
            networkResource.setInputStream(inputStream);

            return networkResource;
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem decoding", e);
        }
    }
}
