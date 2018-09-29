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

package co.edu.uniquindio.utils.communication.transfer.response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The <code>ReturnsManagerCommunication</code> class is responsible for
 * management the responses for the messages. The {@code
 * ReturnsManagerCommunication} use WaitingResults objects for having a
 * reference of the messages.
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see WaitingResult
 */
public class ReturnsManagerCommunication<T> implements ReturnsManager<T> {
	/**
	 * The map of the WaitingResult references with a specific sequence number
	 */
	private Map<String, WaitingResult<T>> results;

	/**
	 * Create a synchronized instance of the class HashMap
	 */

	public ReturnsManagerCommunication() {
		results = new ConcurrentHashMap<>();
	}

	/**
	 * Creates a WaitingResult object for the message with the specified
	 * id number
	 * 
	 * @param id
	 *            . The id number of the message
	 * @return Returns the {@link WaitingResult} created
	 */
	@Override
	public WaitingResult<T> createWaitingResult(String id, long timeOut) {
		WaitingResult<T> resultInWait = new WaitingResult<T>(id, this, timeOut);

		results.put(id, resultInWait);

		return resultInWait;
	}

	/**
	 * This method is responsible for releasing the WaitingResult reference with
	 * the specified id number by set the response for the message.
	 * 
	 * @param id
	 *            . The id number of the message
	 * @param result
	 *            . The response for the message
	 */
	@Override
	public synchronized void releaseWaitingResult(String id, T result) {
		if (results.get(id) == null) {
			return;
		}
		results.remove(id).setResult(result);
	}
}
