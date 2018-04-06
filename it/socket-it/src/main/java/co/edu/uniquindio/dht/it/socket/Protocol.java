/*
 *  Chord project implement of lookup algorithm Chord
 *  Copyright (C) 2010  Daniel Pelaez, Daniel Lopez, Hector Hurtado
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

package co.edu.uniquindio.dht.it.socket;

import co.edu.uniquindio.utils.communication.message.MessageType;

public final class Protocol {

    /**
     * PUT BEGIN
     */
    public static final MessageType PUT = MessageType.builder()
            .name("PUT")
            .amountParams(PutParams.values().length)
            .build();

    public enum PutParams {
        RESOURCE_NAME
    }

    public enum PutDatas {
        RESOURCE
    }

    /**
     * PUT END
     */

    /**
     * PUT_RESPONSE BEGIN
     */
    public static final MessageType PUT_RESPONSE = MessageType.builder()
            .name("PUT_RESPONSE")
            .amountParams(PutResponseParams.values().length)
            .build();

    public enum PutResponseParams {
        MESSAGE
    }

    /**
     * PUT_RESPONSE END
     */

    /**
     * GET BEGIN
     */
    public static final MessageType GET = MessageType.builder()
            .name("GET")
            .amountParams(GetParams.values().length)
            .build();

    public enum GetParams {
        RESOURCE_NAME
    }

    /**
     * GET END
     */

    /**
     * GET_RESPONSE BEGIN
     */
    public static final MessageType GET_RESPONSE = MessageType.builder()
            .name("GET_RESPONSE")
            .amountParams(GetResponseParams.values().length)
            .build();

    public enum GetResponseParams {
        MESSAGE
    }

    public enum GetResponseDatas {
        RESOURCE
    }
}
