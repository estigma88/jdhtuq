package co.edu.uniquindio.utils.communication.transfer.network.jackson;

import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.network.MessageSerialization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MessageJsonSerialization implements MessageSerialization {
    private final ObjectMapper objectMapper;

    public MessageJsonSerialization(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.objectMapper.addMixIn(Message.class, MessageMixIn.class)
                .addMixIn(MessageType.class, MessageTypeMixIn.class)
                .addMixIn(Address.class, AddressMixIn.class)
                .addMixIn(Message.MessageBuilder.class, MessageBuilderMixIn.class)
                .addMixIn(MessageType.MessageTypeBuilder.class, MessageTypeBuilderMixIn.class)
                .addMixIn(Address.AddressBuilder.class, AddressBuilderMixIn.class);
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
