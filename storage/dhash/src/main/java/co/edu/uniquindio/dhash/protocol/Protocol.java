/*
 *  DHash project implement a storage management
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

package co.edu.uniquindio.dhash.protocol;

import co.edu.uniquindio.utils.communication.message.MessageType;

/**
 * The <code>Protocol</code> class contains all message type for communication
 * protocol in DHash
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public final class Protocol {

    /**
     * PUT BEGIN
     */
    public static final MessageType PUT = MessageType.builder()
            .name("PUT")
            .amountParams(PutParams.values().length)
            .build();

    public enum PutParams {
        RESOURCE_KEY, REPLICATE, RESOURCE
    }

    /**
     * PUT END
     */

    /**
     * PUT_RESPONSE BEGIN
     */
    public static final MessageType PUT_RESPONSE = MessageType.builder()
            .name("PUT_RESPONSE")
            .amountParams(PutParams.values().length)
            .build();

    public enum PutResponseParams {
        TRANSFER_VALID, MESSAGE
    }

    /**
     * PUT END
     */

    /**
     * CONTAIN BEGIN
     */
    public static final MessageType CONTAIN = MessageType.builder()
            .name("CONTAIN")
            .amountParams(ContainParams.values().length)
            .build();

    public enum ContainParams {
        RESOURCE_KEY
    }

    /**
     * CONTAIN END
     */

    /**
     * CONTAIN_RESPONSE BEGIN
     */
    public static final MessageType CONTAIN_RESPONSE = MessageType.builder()
            .name("CONTAIN_RESPONSE")
            .amountParams(ContainResponseParams.values().length)
            .build();

    public enum ContainResponseParams {
        HAS_RESOURCE
    }

    /**
     * CONTAIN_RESPONSE END
     */

    /**
     * GET BEGIN
     */
    public static final MessageType GET = MessageType.builder()
            .name("GET")
            .amountParams(GetParams.values().length)
            .build();

    public enum GetParams {
        RESOURCE_KEY
    }

    /**
     * GET_RESPONSE BEGIN
     */
    public static final MessageType GET_RESPONSE = MessageType.builder()
            .name("GET_RESPONSE")
            .amountParams(0)
            .build();

    public enum GetResponseParams {
        RESOURCE, TRANSFER_VALID, MESSAGE
    }

    /**
     * TRANSFER_FILES END
     */
}
