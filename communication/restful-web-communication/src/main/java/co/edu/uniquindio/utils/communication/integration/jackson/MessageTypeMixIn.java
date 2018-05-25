package co.edu.uniquindio.utils.communication.integration.jackson;

import co.edu.uniquindio.utils.communication.message.MessageType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = MessageType.MessageTypeBuilder.class)
public interface MessageTypeMixIn {
}
