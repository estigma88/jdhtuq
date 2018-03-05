package co.edu.uniquindio.utils.communication.message;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;
import java.util.Set;

/**
 * Class that defined all methods for message
 *
 * @author Daniel Pelaez
 */
@Builder
@Data
public class Message {

    /**
     * Type of send
     *
     * @author Daniel Pelaez
     */
    public enum SendType {
        REQUEST, RESPONSE
    }

    /**
     * Send type
     */
    private final SendType sendType;

    /**
     * Message type
     */
    private final MessageType messageType;

    /**
     * Address
     */
    private final Address address;

    /**
     * Params
     */
    @Singular
    private final Map<String, String> params;

    /**
     * Hash map of names with datas
     */
    @Singular
    private final Map<String, byte[]> datas;

    /**
     * Sequence number
     */
    private final long sequenceNumber;


    /**
     * This method is used for getting a specific data from the message
     *
     * @return Returns the data that is stored in the given position
     */
    public String getParam(String name) {
        if (!params.containsKey(name)) {
            throw new IllegalArgumentException("The message type "
                    + messageType.getName() + " not contains param '" + name
                    + "'");
        } else {
            return params.get(name);
        }
    }


    /**
     * Gets data by name
     *
     * @param name Data name
     * @return Data
     */
    public byte[] getData(String name) {
        if (!datas.containsKey(name)) {
            throw new IllegalArgumentException("The big message type "
                    + messageType.getName() + " not contains param '" + name
                    + "'");
        } else {
            return datas.get(name);
        }
    }

    /**
     * This method is used for knowing if the message is the same source and
     * destination node
     *
     * @return Returns true if the message is the same source and destination
     * node
     */
    public boolean isMessageFromMySelf() {
        return address.isMessageFromMySelf();
    }

    public Set<String> getParamsKey() {
        return params.keySet();
    }

}
