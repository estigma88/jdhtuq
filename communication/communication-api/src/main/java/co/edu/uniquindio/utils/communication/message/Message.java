/*
 *  Communication project implement communication point to point and multicast
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
    private Map<String, String> params;

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
        return params.get(name);
    }


    /**
     * Gets data by name
     *
     * @param name Data name
     * @return Data
     */
    public byte[] getData(String name) {
        return datas.get(name);
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
