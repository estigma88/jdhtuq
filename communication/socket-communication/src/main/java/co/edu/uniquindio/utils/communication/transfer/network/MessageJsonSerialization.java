package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MessageJsonSerialization implements MessageSerialization {
    private final ObjectMapper objectMapper;

    public MessageJsonSerialization(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String encode(Message message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Problem encoding", e);
        }
    }

    @Override
    public Message decode(String message) {
        try {
            return objectMapper.readValue(message, Message.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem decoding", e);
        }
    }
}
