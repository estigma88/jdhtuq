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
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import lombok.extern.slf4j.Slf4j;

/**
 * The <code>SuccessorList</code> class represents a list of <code>m</code>
 * entries, where every entry is the next node's successor in the network.
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
public class SuccessorList {

    /**
     * Communication manager
     */
    private CommunicationManager communicationManager;

    /**
     * Is the separator used for listing the successors.
     */
    private static final String SEPARATOR = "->";

    /**
     * The array that contains the successors.
     */
    private ChordKey[] keyList;

    /**
     * The value of successor list size.
     */
    private int size;

    /**
     * The reference of the chord node.
     */
    private ChordNode chordNode;
    private KeyFactory keyFactory;
    private final IdGenerator sequenceGenerator;

    SuccessorList(ChordNode chordNode, CommunicationManager communicationManager, int successorListAmount, KeyFactory keyFactory, IdGenerator sequenceGenerator) {
        this.size = successorListAmount;
        this.sequenceGenerator = sequenceGenerator;
        this.keyList = new ChordKey[size];
        this.chordNode = chordNode;
        this.communicationManager = communicationManager;
        this.keyFactory = keyFactory;
    }

    SuccessorList(CommunicationManager communicationManager, ChordKey[] keyList, int size, ChordNode chordNode, IdGenerator sequenceGenerator, KeyFactory keyFactory) {
        this.communicationManager = communicationManager;
        this.keyList = keyList;
        this.size = size;
        this.chordNode = chordNode;
        this.sequenceGenerator = sequenceGenerator;
        this.keyFactory = keyFactory;
    }

    /**
     * Called periodically in {@link StableRing }.
     * <p>
     * Fixes the successor list by asking node's successor its successor list.
     */
    public void fixSuccessors() {
        String successorList;
        ChordKey successor = keyList[0];
        Message getSuccessorListMesssage;

        getSuccessorListMesssage = Message.builder()
                .id(sequenceGenerator.newId())
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.GET_SUCCESSOR_LIST)
                .address(Address.builder()
                        .destination(successor.getValue())
                        .source(chordNode.getKey().getValue())
                        .build())
                .build();

        successorList = communicationManager.send(
                getSuccessorListMesssage, String.class);

        if (successorList == null)
            return;

        String[] successors = successorList.split(SEPARATOR);

        for (int i = 1; i < Math.min(size, successors.length); i++) {
            keyList[i] = (ChordKey) keyFactory.newKey(successors[i - 1]);
        }

        log.debug("Node: " + chordNode.getKey().getValue()
                + " Successors: " + toString());
    }

    /**
     * Initializes the keyList with chord node's successor in all positions.
     */
    public void initializeSuccessors() {
        for (int i = 0; i < size; i++) {
            keyList[i] = chordNode.getSuccessor();
        }
    }

    /**
     * Sets the successor in the position [0] of the keyList
     *
     * @param successor The successor key that will be set.
     */
    public void setSuccessor(ChordKey successor) {
        keyList[0] = successor;

        log.debug("Node: " + chordNode.getKey().getValue()
                + " New successor: " + successor);
    }

    /**
     * Verifies and return the next successor that is available by checking
     * every one.
     *
     * @return A {@link Key} of the first successor that is available.
     */
    public ChordKey getNextSuccessorAvailable() {
        Boolean success;
        Message pingMessage;

        for (int i = 0; i < keyList.length; i++) {

            pingMessage = Message.builder()
                    .id(sequenceGenerator.newId())
                    .sendType(Message.SendType.REQUEST)
                    .messageType(Protocol.PING)
                    .address(Address.builder()
                            .destination(keyList[i].getValue())
                            .source(chordNode.getKey().getValue())
                            .build())
                    .build();

            success = communicationManager.send(pingMessage,
                    Boolean.class);

            if (success != null) {
                return keyList[i];
            }
        }

        return null;
    }

    /**
     * Gets the keyList.
     *
     * @return An array of {@link Key} with the successors of the node.
     */
    public ChordKey[] getKeyList() {
        return keyList;
    }

    /**
     * Constructs a representative {@link String} of the class, adding
     * information about the successors of the node separated by the SEPARATOR
     * of the class.
     *
     * @return A representative {@link String} of the class.
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < size - 1; i++) {
            stringBuilder.append(keyList[i].getValue());
            stringBuilder.append(SEPARATOR);
        }

        stringBuilder.append(keyList[size - 1].getValue());

        return stringBuilder.toString();
    }
}
