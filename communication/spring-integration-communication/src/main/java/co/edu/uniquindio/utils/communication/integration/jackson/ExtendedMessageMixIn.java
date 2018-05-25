package co.edu.uniquindio.utils.communication.integration.jackson;

import co.edu.uniquindio.utils.communication.integration.sender.ExtendedMessage;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ExtendedMessage.ExtendedMessageBuilder.class)
public interface ExtendedMessageMixIn {
}
