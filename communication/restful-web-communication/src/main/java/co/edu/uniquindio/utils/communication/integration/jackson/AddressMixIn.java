package co.edu.uniquindio.utils.communication.integration.jackson;

import co.edu.uniquindio.utils.communication.message.Address;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Address.AddressBuilder.class)
@JsonIgnoreProperties({"messageFromMySelf"})
public interface AddressMixIn {
}
