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

package co.edu.uniquindio.chord.node;

import java.math.BigInteger;
import java.util.Arrays;

import co.edu.uniquindio.utils.hashing.Key;
import org.apache.log4j.Logger;

/**
 * The <code>FingersTable</code> class represents a routing table with up to
 * <code>m</code> entries. The <code>i-th</code> entry in the table at node
 * <code>n</code> contains the identity of the first node <code>s</code> that
 * succeeds <code>n</code> by at least <code>2^(i-1)</code> on the identifier
 * circle, i.e., <code>s = successor(n+2^(i-1))</code>, where
 * <code>1<=i<=m</code> (and all arithmetic is modulo <code>2^m</code>). We call
 * node <code>s</code> the <code>i-th</code> finger of node <code>n</code>, and
 * denote it by <code>n.finger[i]</code>.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see ChordNode
 * @see StableRing
 */
public class FingersTable {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger
			.getLogger(FingersTable.class);

	/**
	 * List of {@link Key} that represents the fingers table.
	 */
	private Key[] fingersTable;

	/**
	 * References the chord node that has this FingersTable.
	 */
	private ChordNode chordNode;

	/**
	 * Stores the index of the next finger to fix.
	 */
	private int next;

	/**
	 * Size of the fingers table.
	 */
	private int size;

	/**
	 * Constructor of the class. Receives a reference of the chord node and
	 * initializes the size, pointer next and the fingers table.
	 * 
	 * @param nodeChord
	 *            The reference of the chord node.
	 */
	FingersTable(ChordNode nodeChord) {
		this.size = Key.getKeyLength();
		this.chordNode = nodeChord;
		this.next = 0;
		this.fingersTable = new Key[size];
	}

	FingersTable(Key[] fingersTable, ChordNode chordNode, int next, int size) {
		this.fingersTable = fingersTable;
		this.chordNode = chordNode;
		this.next = next;
		this.size = size;
	}

	/**
	 * Find the closest key in its fingers list that is before a given key.
	 * 
	 * @param key
	 *            The key that will be tested.
	 * @return {@link Key} The key that is closest and before the given key.
	 */
	public Key findClosestPresedingNode(Key key) {
		for (int i = size - 1; i >= 0; i--) {
			if (fingersTable[i] != null) {
				if (fingersTable[i].isBetween(chordNode.getKey(), key)) {
					return fingersTable[i];
				}
			}
		}
		return chordNode.getKey();
	}

	/**
	 * Called periodically in {@link StableRing }.
	 * 
	 * Fixes the position <code>next</code> of the fingers list every time the
	 * method <code>FingersTable.fixFingers</code> is called.
	 */
	public void fixFingers() {
		next++;

		if (next > size - 1) {
			next = 0;
		}

		fingersTable[next] = chordNode.findSuccessor(createNext(chordNode
				.getKey()), LookupType.FINGERS_TABLE);

		if (fingersTable[next] == null) {
			fingersTable[next] = chordNode.getSuccessor();
		}

		logger.debug("Node: " + chordNode.getKey().getValue() + " Next: " + next
				+ " Key: " + createNext(chordNode.getKey()));

		logger.debug("Fingers: " + Arrays.asList(fingersTable));
	}

	/**
	 * Sets the successor in the position 0 in the fingers table list.
	 * 
	 * @param successor
	 *            The key to be set as successor.
	 */
	public void setSuccessor(Key successor) {
		fingersTable[0] = successor;
	}

	/**
	 * Called always for <code>FingersTable.fixFingers</code>
	 * 
	 * Create the <code>i-th</code> entry to be test in the fingers table. The
	 * next entry is given by
	 * <code>(node.key + 2^(next-1)) mod 2^(size), 1<=k<=m</code>
	 * 
	 * @param key
	 *            Node's key
	 * @return {@link Key} The key to be test in the fingers table.
	 */
	Key createNext(Key key) {
		Key nextKey;
		BigInteger nextValue;
		BigInteger twoPow;
		BigInteger maxValue;

		twoPow = new BigInteger("2");
		twoPow = twoPow.pow(next);

		nextValue = new BigInteger(key.getHashing().toByteArray());
		nextValue = nextValue.add(twoPow);

		maxValue = new BigInteger("2");
		maxValue = maxValue.pow(size);

		nextValue = nextValue.mod(maxValue);

		nextKey = new Key(nextValue);

		return nextKey;
	}

	/**
	 * Gets the next position to fix.
	 * 
	 * @return value of the position to fix.
	 */
	public int getNext() {
		return next;
	}

	/**
	 * Gets the size of the fingers table
	 * 
	 * @return The size of the fingers table.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Gets the fingers table array.
	 * 
	 * @return Key[]
	 */
	public Key[] getFingersTable() {
		return fingersTable;
	}

	/**
	 * Gets the reference of the chord node.
	 * 
	 * @return {@link ChordNode}
	 */
	public ChordNode getChordNode() {
		return chordNode;
	}
}