package co.edu.uniquindio.utils.communication.web.restful.jackson;

import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.web.restful.ExtendedMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ExtendedMessage.ExtendedMessageBuilder.class)
public interface ExtendedMessageMixIn {
}
