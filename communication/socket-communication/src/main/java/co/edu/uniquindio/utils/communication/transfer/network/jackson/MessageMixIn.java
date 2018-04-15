package co.edu.uniquindio.utils.communication.transfer.network.jackson;

import co.edu.uniquindio.utils.communication.message.Message;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Message.MessageBuilder.class)
@JsonIgnoreProperties({"messageFromMySelf", "paramsKey"})
interface MessageMixIn {
}
