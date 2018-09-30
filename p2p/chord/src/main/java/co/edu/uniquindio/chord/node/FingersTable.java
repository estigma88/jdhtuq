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

import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Arrays;

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
 * @see ChordNode
 * @see StableRing
 * @since 1.0
 */
@Slf4j
public class FingersTable {

    /**
     * List of {@link Key} that represents the fingers table.
     */
    private ChordKey[] fingersTable;

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
    private KeyFactory keyFactory;

    /**
     * Constructor of the class. Receives a reference of the chord node and
     * initializes the size, pointer next and the fingers table.
     *
     * @param nodeChord The reference of the chord node.
     */
    FingersTable(ChordNode nodeChord, KeyFactory keyFactory) {
        this.size = keyFactory.getKeyLength();
        this.chordNode = nodeChord;
        this.next = 0;
        this.fingersTable = new ChordKey[size];
        this.keyFactory = keyFactory;
    }

    FingersTable(ChordKey[] fingersTable, ChordNode chordNode, int next, int size, KeyFactory keyFactory) {
        this.fingersTable = fingersTable;
        this.chordNode = chordNode;
        this.next = next;
        this.size = size;
        this.keyFactory = keyFactory;
    }

    /**
     * Find the closest key in its fingers list that is before a given key.
     *
     * @param key The key that will be tested.
     * @return {@link Key} The key that is closest and before the given key.
     */
    public ChordKey findClosestPresedingNode(ChordKey key) {
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
     * <p>
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

        log.debug("Node: " + chordNode.getKey().getValue() + " Next: " + next
                + " ChordKey: " + createNext(chordNode.getKey()));

        log.debug("Fingers: " + Arrays.asList(fingersTable));
    }

    /**
     * Sets the successor in the position 0 in the fingers table list.
     *
     * @param successor The key to be set as successor.
     */
    public void setSuccessor(ChordKey successor) {
        fingersTable[0] = successor;
    }

    /**
     * Called always for <code>FingersTable.fixFingers</code>
     * <p>
     * Create the <code>i-th</code> entry to be test in the fingers table. The
     * next entry is given by
     * <code>(node.key + 2^(next-1)) mod 2^(size), 1<=k<=m</code>
     *
     * @param key Node's key
     * @return {@link Key} The key to be test in the fingers table.
     */
    ChordKey createNext(ChordKey key) {
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

        nextKey = keyFactory.newKey(nextValue);

        return (ChordKey) nextKey;
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
     * @return ChordKey[]
     */
    public ChordKey[] getFingersTable() {
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