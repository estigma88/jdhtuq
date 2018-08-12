/*
 *  Chord project implement of lookup algorithm Chord
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
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
 *
 */

package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.overlay.OverlayException;

/**
 * The <code>ChordNodeFactoryException</code> class handles a node factory
 * exception, that may occur when trying to create or instantiate the factory
 * class.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see ChordNodeFactory
 */
public class ChordNodeFactoryException extends OverlayException {
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
	public ChordNodeFactoryException(String message) {
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
	public ChordNodeFactoryException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
