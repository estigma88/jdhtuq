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

package co.edu.uniquindio.utils.logger;

import org.apache.log4j.Logger;

/**
 * The <code>LoggerFactory</code> class defined factory of logger used
 * <code>LoggerDHT</code>
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class LoggerFactory implements org.apache.log4j.spi.LoggerFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.log4j.spi.LoggerFactory#makeNewLoggerInstance(java.lang.String
	 * )
	 */
	public Logger makeNewLoggerInstance(String name) {
		return new LoggerDHT(name);
	}

}
