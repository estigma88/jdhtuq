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
	private Map<Long, WaitingResult<T>> results;

	/**
	 * Create a synchronized instance of the class HashMap
	 */

	public ReturnsManagerCommunication() {
		results = Collections
				.synchronizedMap(new HashMap<Long, WaitingResult<T>>());
	}

	/**
	 * Creates a WaitingResult object for the message with the specified
	 * sequence number
	 * 
	 * @param sequence
	 *            . The sequence number of the message
	 * @return Returns the {@link WaitingResult} created
	 */
	public WaitingResult<T> createWaitingResult(long sequence, long timeOut) {
		WaitingResult<T> resultInWait = new WaitingResult<T>(sequence, this, timeOut);

		results.put(sequence, resultInWait);

		return resultInWait;
	}

	/**
	 * This method is responsible for releasing the WaitingResult reference with
	 * the specified sequence number by set the response for the message.
	 * 
	 * @param sequence
	 *            . The sequence number of the message
	 * @param result
	 *            . The response for the message
	 */
	public synchronized void releaseWaitingResult(long sequence, T result) {
		if (results.get(sequence) == null) {
			return;
		}
		results.remove(sequence).setResult(result);
	}
}
