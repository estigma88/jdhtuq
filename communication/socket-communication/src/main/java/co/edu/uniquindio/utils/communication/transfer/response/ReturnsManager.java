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

/**
 * The <code>ReturnsManager</code> interface have all services for waiting
 * results
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public interface ReturnsManager<T> {

	/**
	 * Creates a waiting result for sequence number and time out
	 * 
	 * @param sequence
	 *            Sequence number
	 * @param timeOut
	 *            Time out of waiting
	 * @return WaitingResult
	 */
	public WaitingResult<T> createWaitingResult(long sequence, long timeOut);

	/**
	 * Release waiting result by sequence number and response
	 * 
	 * @param sequence
	 *            Sequence number
	 * @param result
	 *            Response
	 */
	public void releaseWaitingResult(long sequence, T result);
}
