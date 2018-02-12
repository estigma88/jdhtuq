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
 *  
 */

package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.chord.node.StableRing;
import co.edu.uniquindio.utils.command.ThreadCommand;

/**
 * The <code>NodeChordCommand</code> class implements the pattern command and
 * stores a reference of the chord node.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see CheckPredecessorCommand
 * @see FixFingersCommand
 * @see FixSuccessorsCommand
 * @see StabilizeCommand
 * @see StableRing
 */
public abstract class ChordNodeCommand extends ThreadCommand {
	/**
	 * Is the reference of the chord node.
	 */
	protected ChordNode nodeChord;

	/**
	 * The constructor of the class. Initializes the reference of the chord
	 * node.
	 * 
	 * @param nodeChord
	 *            The reference of the chord node.
	 */
	protected ChordNodeCommand(ChordNode nodeChord) {
		this.nodeChord = nodeChord;
	}

	/**
	 * Sets the reference of the chord node.
	 * 
	 * @param nodeChord
	 */
	public void setNodeChord(ChordNode nodeChord) {
		this.nodeChord = nodeChord;
	}

}
