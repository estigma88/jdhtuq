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
 * The <code>CommunicationManagerUDP</code> class is an
 * <code>CommunicationManagerNetworkLAN</code>. Implemented the creation of
 * unicast manager. Required params: PORT_UDP and BUFFER_SIZE_UDP
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class CommunicationManagerUDP extends CommunicationManagerNetworkLAN {

	public enum CommunicationManagerUDPProperties {
		PORT_UDP, BUFFER_SIZE_UDP
	}

	/**
	 * Logger
	 */
	private static final LoggerDHT logger = LoggerDHT
			.getLogger(CommunicationManagerUDP.class);

	/**
	 * Not implemented
	 */
	protected Communicator createUnicastBigManager() {
		return null;
	}

	/**
	 * Creates a UnicastManagerUDP instance. Required param in
	 * CommunicationProperties called PORT_UDP and BUFFER_SIZE_UDP
	 */
	protected Communicator createUnicastManager() {
		int portUdp;
		long bufferUdp;

		if (communicationProperties
				.containsKey(CommunicationManagerUDPProperties.PORT_UDP.name())) {
			portUdp = Integer.parseInt(communicationProperties
					.get(CommunicationManagerUDPProperties.PORT_UDP.name()));
		} else {
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
					"Property PORT_UDP not found");

			logger.error("Property PORT_UDP not found",
					illegalArgumentException);

			throw illegalArgumentException;
		}

		if (communicationProperties
				.containsKey(CommunicationManagerUDPProperties.BUFFER_SIZE_UDP
						.name())) {
			bufferUdp = Long.parseLong(communicationProperties
					.get(CommunicationManagerUDPProperties.BUFFER_SIZE_UDP
							.name()));
		} else {
			IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
					"Property BUFFER_SIZE_UDP not found");

			logger.error("Property BUFFER_SIZE_UDP not found",
					illegalArgumentException);

			throw illegalArgumentException;
		}
		unicastManager = new UnicastManagerUDP(portUdp, bufferUdp);

		return unicastManager;
	}

}
