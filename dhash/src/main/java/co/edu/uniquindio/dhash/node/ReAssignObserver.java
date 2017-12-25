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

package co.edu.uniquindio.dhash.node;

import java.util.Arrays;

import co.edu.uniquindio.dhash.resource.ResourceAlreadyExistException;
import co.edu.uniquindio.utils.hashing.Key;
import co.edu.uniquindio.utils.logger.LoggerDHT;

/**
 * The <code>ReAssignObserver</code> class is notify when overlay node leave and
 * reassign all resource in the network using
 * <code>DHashNode.relocateAllResources</code>
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class ReAssignObserver extends OverlayObserver {
	/**
	 * Logger
	 */
	private static final LoggerDHT logger = LoggerDHT
			.getLogger(ReAssignObserver.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * co.edu.uniquindio.utils.communication.Observer#update(java.lang.Object)
	 */
	public void update(Object object) {
		if (object instanceof String[]) {

			String[] message = (String[]) object;

			if (message.length != 2) {
				logger
						.error(
								"Incorrect message",
								new IllegalArgumentException(
										"Message must to be an String[] and to have two elements only"));

				return;
			}

			logger.info("Update: " + Arrays.toString(message));

			if (message[0].equals("REASSIGN")) {
				try {
					dHashNode.relocateAllResources(new Key(message[1]));
				} catch (ResourceAlreadyExistException e) {
					logger.error("Error relocaled", e);
				}
			}
		}
	}

}
