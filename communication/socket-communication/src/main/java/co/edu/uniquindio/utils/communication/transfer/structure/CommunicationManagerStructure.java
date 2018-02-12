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

package co.edu.uniquindio.utils.communication.transfer.structure;

import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerWaitingResult;
import co.edu.uniquindio.utils.communication.transfer.Communicator;

/**
 * The <code>CommunicationManagerStructure</code> class is an
 * <code>CommunicationManagerWaitingResult</code>. Implement all services for
 * send message in data struture. Not require params
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class CommunicationManagerStructure extends
		CommunicationManagerWaitingResult {

	/**
	 * Instance of CommunicationDataStructure
	 */
	private CommunicationDataStructure communicationDataStructure;

	/**
	 * Builds a CommunicationManagerStructure. Instance a
	 * CommunicationDataStructure and invoke
	 * <code>createMulticastManager()</code>,
	 * <code>createTransferObjectManager()</code> and
	 * <code>createUnicastManager()</code>
	 */
	public CommunicationManagerStructure() {
		communicationDataStructure = new CommunicationDataStructure();
		observableCommunication = communicationDataStructure;

		createMulticastManager();
		createUnicastBigManager();
		createUnicastManager();
	}

	/**
	 * Creates a MulticastManagerStructure
	 */
	protected Communicator createMulticastManager() {
		multicastManager = new MulticastManagerStructure(
				communicationDataStructure, this);

		return multicastManager;
	}

	/**
	 * Creates a TransferObjectManagerStructure
	 */
	protected Communicator createUnicastBigManager() {
		unicastBigManager = new UnicastBigManagerStructure(
				communicationDataStructure, this);

		return unicastBigManager;
	}

	/**
	 * Creates a UnicastManagerStructure
	 */
	protected Communicator createUnicastManager() {
		unicastManager = new UnicastManagerStructure(
				communicationDataStructure, this);

		return unicastManager;
	}

	/**
	 * Not implement
	 */
	public void stop() {

	}

}
