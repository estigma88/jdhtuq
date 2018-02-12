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

import co.edu.uniquindio.chord.configurations.ChordProperties;
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
 * @since 1.0
 * @see ChordNode
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
	 * Is the time it takes to run again
	 */
	private long executeTime;

	/**
	 * Determines the state of execution
	 */
	private volatile boolean run;

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

	/**
	 * The constructor of the class. Gets the execute time from the properties
	 * and sets the node and the run mode.
	 * 
	 * @param node
	 */
	StableRing(ChordNode node) {
		this.node = node;
		this.run = true;
		this.executeTime = ChordProperties.getInstance().getTime()
				.getStableRing();
	}

	StableRing(ChordNode node, long executeTime, boolean run) {
		this.node = node;
		this.executeTime = executeTime;
		this.run = run;
	}

	/**
	 * Executes all the commands periodically while <code>run==true</code>.
	 */
	public void run() {
		while (run) {
			runCommands();
		}
	}

	void runCommands() {
		stabilizeCommand = getStabilizeCommand();
		checkPredecessorCommand = getCheckPredecessorCommand();
		fixFingersCommand = getFixFingersCommand();
		fixSuccessorsCommand = getFixSuccessorsCommand();

		stabilizeCommand.execute();
		checkPredecessorCommand.execute();
		fixFingersCommand.execute();
		fixSuccessorsCommand.execute();

		try {
            Thread.sleep(executeTime);
        } catch (InterruptedException e) {
            logger.fatal(e.getMessage(), e);
        }
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
	 * Starts stabilization
	 */
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * Sets the run mode.
	 * 
	 * @param run
	 *            <code>true</code> if the StableRing will be executing,
	 *            <code>false</code> if the StableRing will not be executing
	 *            anymore.
	 */
	public void setRun(boolean run) {
		this.run = run;
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