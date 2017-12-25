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

package co.edu.uniquindio.chord;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.utils.hashing.Key;

/**
 * The {@code Chord} interface defines the basic methods for creating the node
 * on chord's ring.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see ChordNode
 * @see Key
 * @see OverlayNode
 */
public interface Chord extends OverlayNode {
	/**
	 * Finds the node on the ring that is the successor of the given key.
	 * 
	 * @param id
	 *            The key to find
	 * @return A {@link Key} that is the successor for the id.
	 */
	public Key lookUp(Key id);

	/**
	 * Gets the {@link Key} of the chord node.
	 * 
	 * @return The {@link Key} of the chord node.
	 */
	public Key getKey();
}
