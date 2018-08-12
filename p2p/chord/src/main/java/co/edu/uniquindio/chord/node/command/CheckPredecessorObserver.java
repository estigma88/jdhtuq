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

package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.chord.node.StableRing;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 * 
 * The <code>CheckPredecessorObserver</code> class is responsible for executing
 * the method {@code ChordNode.checkPredecessor} in the chord node at once.
 * 
 * Instantiated periodically in {@link StableRing}.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see StableRing
 */
public class CheckPredecessorObserver implements Observer {
	/**
	 * Executes the method {@code ChordNode.checkPredecessor} on the chord node
	 * at once.
	 */

	@Override
	public void update(Observable observable, Object o) {
		Objects.nonNull(o);

		ChordNode chordNode = (ChordNode) o;

		chordNode.checkPredecessor();
	}
}
