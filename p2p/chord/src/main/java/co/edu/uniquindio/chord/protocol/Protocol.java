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

package co.edu.uniquindio.chord.protocol;

import co.edu.uniquindio.utils.communication.message.MessageType;

/**
 * The <code>Protocol</code> class contains all message type for communication
 * protocol in Chord
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public final class Protocol {

    /**
     * BOOTSTRAP BEGIN
     */
    public static final MessageType BOOTSTRAP = MessageType.builder()
            .name("BOOTSTRAP")
            .amountParams(BootStrapParams.values().length)
            .build();

    public enum BootStrapParams {

    }

    /**
     * BOOTSTRAP END
     */

    /**
     * BOOTSTRAP_RESPONSE BEGIN
     */
    public static final MessageType BOOTSTRAP_RESPONSE = MessageType.builder()
            .name("BOOTSTRAP_RESPONSE")
            .amountParams(BootStrapResponseParams.values().length)
            .build();

    public enum BootStrapResponseParams {
        NODE_FIND
    }

    /**
     * BOOTSTRAP_RESPONSE END
     */

    /**
     * CHANGED_PREDECESSOR BEGIN
     */
    public static final MessageType CHANGED_PREDECESSOR = MessageType.builder()
            .name("CHANGED_PREDECESSOR")
            .amountParams(ChangedPredecessorParams.values().length)
            .build();

    public enum ChangedPredecessorParams {
        PREDECESSOR
    }

    /**
     * CHANGED_PREDECESSOR END
     */

    /**
     * GET_PREDECESSOR BEGIN
     */
    public static final MessageType GET_PREDECESSOR = MessageType.builder()
            .name("GET_PREDECESSOR")
            .amountParams(GetPredecessorParams.values().length)
            .build();

    public enum GetPredecessorParams {

    }

    /**
     * GET_PREDECESSOR END
     */

    /**
     * GET_PREDECESSOR_RESPONSE BEGIN
     */
    public static final MessageType GET_PREDECESSOR_RESPONSE = MessageType.builder()
            .name("GET_PREDECESSOR_RESPONSE")
            .amountParams(GetPredecessorResponseParams.values().length)
            .build();

    public enum GetPredecessorResponseParams {
        PREDECESSOR
    }

    /**
     * GET_PREDECESSOR_RESPONSE END
     */

    /**
     * GET_SUCCESSOR_LIST BEGIN
     */
    public static final MessageType GET_SUCCESSOR_LIST = MessageType.builder()
            .name("GET_SUCCESSOR_LIST")
            .amountParams(GetSuccessorListParams.values().length)
            .build();

    public enum GetSuccessorListParams {

    }

    /**
     * GET_SUCCESSOR_LIST END
     */

    /**
     * GET_SUCCESSOR_LIST_RESPONSE BEGIN
     */
    public static final MessageType GET_SUCCESSOR_LIST_RESPONSE = MessageType.builder()
            .name("GET_SUCCESSOR_LIST_RESPONSE")
            .amountParams(GetSuccessorListResponseParams.values().length)
            .build();

    public enum GetSuccessorListResponseParams {
        SUCCESSOR_LIST
    }

    /**
     * GET_SUCCESSOR_LIST_RESPONSE END
     */

    /**
     * LEAVE BEGIN
     */
    public static final MessageType LEAVE = MessageType.builder()
            .name("LEAVE")
            .amountParams(LeaveParams.values().length)
            .build();

    public enum LeaveParams {

    }

    /**
     * LEAVE END
     */

    /**
     * LOOKUP BEGIN
     */
    public static final MessageType LOOKUP = MessageType.builder()
            .name("LOOKUP")
            .amountParams(LookupParams.values().length)
            .build();

    public enum LookupParams {
        HASHING, TYPE
    }

    /**
     * LOOKUP END
     */

    /**
     * LOOKUP_RESPONSE BEGIN
     */
    public static final MessageType LOOKUP_RESPONSE = MessageType.builder()
            .name("LOOKUP_RESPONSE")
            .amountParams(LookupResponseParams.values().length)
            .build();

    public enum LookupResponseParams {
        NODE_FIND, TYPE
    }

    /**
     * LOOKUP_RESPONSE END
     */

    /**
     * NOTIFY BEGIN
     */
    public static final MessageType NOTIFY = MessageType.builder()
            .name("NOTIFY")
            .amountParams(NotifyParams.values().length)
            .build();

    public enum NotifyParams {

    }

    /**
     * NOTIFY END
     */

    /**
     * PING BEGIN
     */
    public static final MessageType PING = MessageType.builder()
            .name("PING")
            .amountParams(PingParams.values().length)
            .build();

    public enum PingParams {

    }

    /**
     * PING END
     */

    /**
     * PING_RESPONSE BEGIN
     */
    public static final MessageType PING_RESPONSE = MessageType.builder()
            .name("PING_RESPONSE")
            .amountParams(PingResponseParams.values().length)
            .build();

    public enum PingResponseParams {
        PING
    }

    /**
     * PING_RESPONSE END
     */

    /**
     * SET_PREDECESSOR BEGIN
     */
    public static final MessageType SET_PREDECESSOR = MessageType.builder()
            .name("SET_PREDECESSOR")
            .amountParams(SetPredecessorParams.values().length)
            .build();

    public enum SetPredecessorParams {
        PREDECESSOR
    }

    /**
     * SET_PREDECESSOR END
     */

    /**
     * SET_SUCCESSOR BEGIN
     */
    public static final MessageType SET_SUCCESSOR = MessageType.builder()
            .name("SET_SUCCESSOR")
            .amountParams(SetSuccessorParams.values().length)
            .build();

    public enum SetSuccessorParams {
        SUCCESSOR
    }
    /**
     * SET_SUCCESSOR END
     */

    /**
     * RE_ASSIGN BEGIN
     */
    public static final MessageType RE_ASSIGN = MessageType.builder()
            .name("RE_ASSIGN")
            .amountParams(SetSuccessorParams.values().length)
            .build();

    public enum ReAssignParams {
        PREDECESSOR
    }
    /**
     * RE_ASSIGN END
     */
}
