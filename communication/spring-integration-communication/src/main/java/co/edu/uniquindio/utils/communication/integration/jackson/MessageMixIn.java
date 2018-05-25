package co.edu.uniquindio.utils.communication.integration.jackson;

import co.edu.uniquindio.utils.communication.message.Message;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Message.MessageBuilder.class)
@JsonIgnoreProperties({"messageFromMySelf", "paramsKey"})
public interface MessageMixIn {
}
