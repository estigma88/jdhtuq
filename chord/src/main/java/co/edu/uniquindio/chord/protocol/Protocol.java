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
 * 
 */
public final class Protocol {

	/**
	 * BOOTSTRAP BEGIN
	 */
	public static final MessageType BOOTSTRAP = new MessageType("BOOTSTRAP",
			BootStrapParams.values().length);

	public enum BootStrapParams {

	}

	/**
	 * BOOTSTRAP END
	 */

	/**
	 * BOOTSTRAP_RESPONSE BEGIN
	 */
	public static final MessageType BOOTSTRAP_RESPONSE = new MessageType(
			"BOOTSTRAP_RESPONSE", BootStrapResponseParams.values().length);

	public enum BootStrapResponseParams {
		NODE_FIND
	}

	/**
	 * BOOTSTRAP_RESPONSE END
	 */

	/**
	 * CHANGED_PREDECESSOR BEGIN
	 */
	public static final MessageType CHANGED_PREDECESSOR = new MessageType(
			"CHANGED_PREDECESSOR", ChangedPredecessorParams.values().length);

	public enum ChangedPredecessorParams {
		PREDECESSOR
	}

	/**
	 * CHANGED_PREDECESSOR END
	 */

	/**
	 * GET_PREDECESSOR BEGIN
	 */
	public static final MessageType GET_PREDECESSOR = new MessageType(
			"GET_PREDECESSOR", GetPredecessorParams.values().length);

	public enum GetPredecessorParams {

	}

	/**
	 * GET_PREDECESSOR END
	 */

	/**
	 * GET_PREDECESSOR_RESPONSE BEGIN
	 */
	public static final MessageType GET_PREDECESSOR_RESPONSE = new MessageType(
			"GET_PREDECESSOR_RESPONSE",
			GetPredecessorResponseParams.values().length);

	public enum GetPredecessorResponseParams {
		PREDECESSOR
	}

	/**
	 * GET_PREDECESSOR_RESPONSE END
	 */

	/**
	 * GET_SUCCESSOR_LIST BEGIN
	 */
	public static final MessageType GET_SUCCESSOR_LIST = new MessageType(
			"GET_SUCCESSOR_LIST", GetSuccessorListParams.values().length);

	public enum GetSuccessorListParams {

	}

	/**
	 * GET_SUCCESSOR_LIST END
	 */

	/**
	 * GET_SUCCESSOR_LIST_RESPONSE BEGIN
	 */
	public static final MessageType GET_SUCCESSOR_LIST_RESPONSE = new MessageType(
			"GET_SUCCESSOR_LIST_RESPONSE", GetSuccessorListResponseParams
					.values().length);

	public enum GetSuccessorListResponseParams {
		SUCCESSOR_LIST
	}

	/**
	 * GET_SUCCESSOR_LIST_RESPONSE END
	 */

	/**
	 * LEAVE BEGIN
	 */
	public static final MessageType LEAVE = new MessageType("LEAVE",
			LeaveParams.values().length);

	public enum LeaveParams {
		
	}

	/**
	 * LEAVE END
	 */

	/**
	 * LOOKUP BEGIN
	 */
	public static final MessageType LOOKUP = new MessageType("LOOKUP",
			LookupParams.values().length);

	public enum LookupParams {
		HASHING, TYPE
	}

	/**
	 * LOOKUP END
	 */

	/**
	 * LOOKUP_RESPONSE BEGIN
	 */
	public static final MessageType LOOKUP_RESPONSE = new MessageType(
			"LOOKUP_RESPONSE", LookupResponseParams.values().length);

	public enum LookupResponseParams {
		NODE_FIND, TYPE
	}

	/**
	 * LOOKUP_RESPONSE END
	 */

	/**
	 * NOTIFY BEGIN
	 */
	public static final MessageType NOTIFY = new MessageType("NOTIFY",
			NotifyParams.values().length);

	public enum NotifyParams {

	}

	/**
	 * NOTIFY END
	 */

	/**
	 * PING BEGIN
	 */
	public static final MessageType PING = new MessageType("PING", PingParams
			.values().length);

	public enum PingParams {

	}

	/**
	 * PING END
	 */

	/**
	 * PING_RESPONSE BEGIN
	 */
	public static final MessageType PING_RESPONSE = new MessageType(
			"PING_RESPONSE", PingResponseParams.values().length);

	public enum PingResponseParams {
		PING
	}

	/**
	 * PING_RESPONSE END
	 */

	/**
	 * SET_PREDECESSOR BEGIN
	 */
	public static final MessageType SET_PREDECESSOR = new MessageType(
			"SET_PREDECESSOR", SetPredecessorParams.values().length);

	public enum SetPredecessorParams {
		PREDECESSOR
	}

	/**
	 * SET_PREDECESSOR END
	 */

	/**
	 * SET_SUCCESSOR BEGIN
	 */
	public static final MessageType SET_SUCCESSOR = new MessageType(
			"SET_SUCCESSOR", SetSuccessorParams.values().length);

	public enum SetSuccessorParams {
		SUCCESSOR
	}
	/**
	 * SET_SUCCESSOR END
	 */
}
