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

package co.edu.uniquindio.chord.configurations;

import co.edu.uniquindio.chord.ChordException;

/**
 * The <code>ChordPropertiesException</code> class handles a configuration
 * exception, that could happen when trying to load the configuration files from
 * Chord project.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class ChordPropertiesException extends ChordException {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Builds a exception by message
	 * 
	 * @param message
	 *            Message
	 */
	public ChordPropertiesException(String message) {
		super(message);
	}

	/**
	 * Builds a exception by message and throwable
	 * 
	 * @param message
	 *            Message
	 * @param throwable
	 *            Exception or error
	 */
	public ChordPropertiesException(String message, Throwable throwable) {
		super(message, throwable);
	}
}