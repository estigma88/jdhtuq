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

import co.edu.uniquindio.chord.node.command.CheckPredecessorCommand;
import co.edu.uniquindio.chord.node.command.FixFingersCommand;
import co.edu.uniquindio.chord.node.command.FixSuccessorsCommand;
import co.edu.uniquindio.chord.node.command.StabilizeCommand;
import org.apache.log4j.Logger;

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
public class StableRing implements Runnable {

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(StableRing.class);

    /**
     * The reference of the chord node
     */
    private ChordNode node;

    /**
     * Is the command responsible for executing the method
     * <code>ChordNode.stabilize</code> on the chord node.
     */
    private StabilizeCommand stabilizeCommand;

    /**
     * Is the command responsible for executing the method
     * <code>ChordNode.checkPredecessor</code> on the chord node.
     */
    private CheckPredecessorCommand checkPredecessorCommand;

    /**
     * Is the command responsible for executing the method
     * <code>FingersTable.fixFingers</code> on the fingers table of the chord
     * node.
     */
    private FixFingersCommand fixFingersCommand;

    /**
     * Is the command responsible for executing the method
     * <code>SuccessorList.fixSuccessors</code> on the successor list of the
     * chord node.
     */
    private FixSuccessorsCommand fixSuccessorsCommand;

    StableRing(ChordNode node) {
        this.node = node;
    }

    /**
     * Executes all the commands periodically while <code>run==true</code>.
     */
    public void run() {
        stabilizeCommand = getStabilizeCommand();
        checkPredecessorCommand = getCheckPredecessorCommand();
        fixFingersCommand = getFixFingersCommand();
        fixSuccessorsCommand = getFixSuccessorsCommand();

        stabilizeCommand.execute();
        checkPredecessorCommand.execute();
        fixFingersCommand.execute();
        fixSuccessorsCommand.execute();
    }

    FixSuccessorsCommand getFixSuccessorsCommand() {
        return new FixSuccessorsCommand(node);
    }

    FixFingersCommand getFixFingersCommand() {
        return new FixFingersCommand(node);
    }

    CheckPredecessorCommand getCheckPredecessorCommand() {
        return new CheckPredecessorCommand(node);
    }

    StabilizeCommand getStabilizeCommand() {
        return new StabilizeCommand(node);
    }

    /**
     * Gets the reference of the chord node.
     *
     * @return {@link ChordNode} The reference of the chord node.
     */
    public ChordNode getNode() {
        return node;
    }
}