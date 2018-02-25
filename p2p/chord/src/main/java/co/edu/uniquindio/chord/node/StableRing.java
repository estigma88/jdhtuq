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

package co.edu.uniquindio.chord.node;

import org.apache.log4j.Logger;

import java.util.Observable;

/**
 * The {@code StableRing} class is responsible for periodically execute commands
 * that deal with stabilizing the node within the logical network
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see ChordNode
 * @since 1.0
 */
public class StableRing extends Observable implements Runnable {

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(StableRing.class);

    /**
     * The reference of the chord node
     */
    private final ChordNode node;

    StableRing(ChordNode node) {
        this.node = node;
    }

    /**
     * Executes all the commands periodically while <code>run==true</code>.
     */
    public void run() {
        try {
            setChanged();

            notifyObservers(node);

            clearChanged();
        } catch (Exception e) {
            logger.error("Stable ring error", e);
            throw new IllegalStateException(" node " + node.getKey() + "could not be stabilized", e);
        }
    }
}