/*
 *  DHash project implement a storage management 
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
 * 
 */
public final class Protocol {

	/**
	 * PUT BEGIN
	 */
	public static final MessageType PUT = new MessageType("PUT", PutParams
			.values().length);

	public enum PutParams {
		RESOURCE_KEY, REPLICATE
	}

	public enum PutDatas {
		RESOURCE
	}

	/**
	 * PUT END
	 */

	/**
	 * GET BEGIN
	 */
	public static final MessageType GET = new MessageType("GET", GetParams
			.values().length);

	public enum GetParams {
		RESOURCE_KEY
	}

	/**
	 * GET END
	 */

	/**
	 * GET_RESPONSE BEGIN
	 */
	public static final MessageType GET_RESPONSE = new MessageType(
			"GET_RESPONSE", GetResponseParams.values().length);

	public enum GetResponseParams {
		HAS_RESOURCE
	}

	/**
	 * GET_RESPONSE END
	 */

	/**
	 * RESOURCE_COMPARE BEGIN
	 */
	public static final MessageType RESOURCE_COMPARE = new MessageType(
			"RESOURCE_COMPARE", ResourceCompareParams.values().length);

	public enum ResourceCompareParams {
		CHECK_SUM, RESOURCE_KEY
	}

	/**
	 * RESOURCE_COMPARE END
	 */

	/**
	 * RESOURCE_COMPARE_RESPONSE BEGIN
	 */
	public static final MessageType RESOURCE_COMPARE_RESPONSE = new MessageType(
			"RESOURCE_COMPARE_RESPONSE",
			ResourceCompareResponseParams.values().length);

	public enum ResourceCompareResponseParams {
		EXIST_RESOURCE
	}

	/**
	 * RESOURCE_COMPARE_RESPONSE END
	 */

	/**
	 * TRANSFER_FILES BEGIN
	 */
	public static final MessageType RESOURCE_TRANSFER = new MessageType(
			"RESOURCE_TRANSFER", ResourceTransferParams.values().length);

	public enum ResourceTransferParams {
		RESOURCE_KEY
	}

	/**
	 * TRANSFER_FILES BEGIN
	 */
	public static final MessageType RESOURCE_TRANSFER_RESPONSE = new MessageType(
			"RESOURCE_TRANSFER_RESPONSE", 0);

	public enum ResourceTransferResponseData {
		RESOURCE
	}

	/**
	 * TRANSFER_FILES END
	 */

	/**
	 * TRANSFER_FILES_FINISHED BEGIN
	 */
	public static final MessageType TRANSFER_FILES_FINISHED = new MessageType(
			"TRANSFER_FILES_FINISHED",
			TransferFilesFinishedParams.values().length);

	public enum TransferFilesFinishedParams {

	}
	/**
	 * TRANSFER_FILES_FINISHED END
	 */
}
