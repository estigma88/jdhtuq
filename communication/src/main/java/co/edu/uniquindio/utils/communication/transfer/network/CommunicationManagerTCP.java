/*
 *  Communication project implement communication point to point and multicast
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

package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.logger.LoggerDHT;

/**
 * The <code>CommunicationManagerTCP</code> class is an
 * <code>CommunicationManagerNetworkLAN</code>. Implemented the creation of
 * transfer object and unicast manager. Required params: PORT_TCP_RESOURCE and
 * PORT_TCP
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class CommunicationManagerTCP extends CommunicationManagerNetworkLAN {

	/**
	 * The <code>CommunicationManagerTCPProperties</code> enum contains params
	 * required for communication
	 * 
	 * @author dpelaez
	 * 
	 */
	public enum CommunicationManagerTCPProperties {
		PORT_TCP_RESOURCE, PORT_TCP
	}

	/**
	 * Logger
	 */
	private static final LoggerDHT logger = LoggerDHT
			.getLogger(CommunicationManagerTCP.class);

	/**
	 * Creates a BytesTransferManagerTCP instance. Required param in
	 * CommunicationProperties called PORT_TCP_RESOURCE
	 */
	protected Communicator createUnicastBigManager() {
		int portTcp;
		if (communicationProperties
				.containsKey(CommunicationManagerTCPProperties.PORT_TCP_RESOURCE
						.name())) {
			portTcp = Integer.parseInt(communicationProperties
					.get(CommunicationManagerTCPProperties.PORT_TCP_RESOURCE
							.name()));
		} else {
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
					"Property PORT_TCP_RESOURCE not found");

			logger.error("Property PORT_TCP_RESOURCE not found",
					illegalArgumentException);

			throw illegalArgumentException;
		}
		unicastBigManager = new UnicastBigManagerTCP(portTcp);

		return unicastBigManager;
	}

	/**
	 * Creates a UnicastManagerTCP instance. Required param in
	 * CommunicationProperties called PORT_TCP.
	 */
	protected Communicator createUnicastManager() {
		int portTcp;
		if (communicationProperties
				.containsKey(CommunicationManagerTCPProperties.PORT_TCP.name())) {
			portTcp = Integer.parseInt(communicationProperties
					.get(CommunicationManagerTCPProperties.PORT_TCP.name()));
		} else {
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
					"Property PORT_TCP not found");

			logger.error("Property PORT_TCP not found",
					illegalArgumentException);

			throw illegalArgumentException;
		}
		unicastManager = new UnicastManagerTCP(portTcp);

		return unicastManager;
	}

}
