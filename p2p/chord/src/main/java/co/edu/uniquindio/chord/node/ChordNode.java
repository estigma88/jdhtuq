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

import co.edu.uniquindio.chord.Chord;
import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.chord.protocol.Protocol.LookupResponseParams;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.apache.log4j.Logger;

import java.util.Observable;

/**
 * The <code>ChordNode</code> class represents a node in the Chord network. It
 * implements the peer-to-peer algorithm described in paper <blockquote>Chord: A
 * Scalable Peer-to-peer Lookup Protocol for Internet Applications</blockquote>.
 * <p>
 * A Chord node is initialized from the method <code>boot</code> of the class
 * {@link BootStrap}. The node is added to the network when you know who is your
 * successor and is stable when it knows who is its successor and its
 * predecessor.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see Key
 * @see FingersTable
 * @see SuccessorList
 * @see BootStrap
 * @since 1.0
 */
public class ChordNode extends Observable implements Chord {

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(ChordNode.class);

    /**
     * Communication manager
     */
    private CommunicationManager communicationManager;

    /**
     * The next node on the identifier circle
     */
    private Key successor;

    /**
     * The previous node on the identifier circle
     */
    private Key predecessor;

    /**
     * Routing table
     */
    private FingersTable fingersTable;

    /**
     * Successor list
     */
    private SuccessorList successorList;

    /**
     * Identifier of the node
     */
    private Key key;

    private BootStrap bootStrap;
    private KeyFactory keyFactory;

    ChordNode(Key key, CommunicationManager communicationManager, int successorListAmount, BootStrap bootStrap, KeyFactory keyFactory) {
        this.keyFactory = keyFactory;
        this.fingersTable = newFingersTable();
        this.successorList = new SuccessorList(this, communicationManager, successorListAmount, keyFactory);
        this.key = key;
        this.communicationManager = communicationManager;
        this.bootStrap = bootStrap;

        logger.info("New ChordNode created = " + key);
    }

    ChordNode(CommunicationManager communicationManager, Key successor, Key predecessor, FingersTable fingersTable, SuccessorList successorList, Key key) {
        this.communicationManager = communicationManager;
        this.successor = successor;
        this.predecessor = predecessor;
        this.fingersTable = fingersTable;
        this.successorList = successorList;
        this.key = key;
    }

    /**
     * Makes a look up of the given key.
     *
     * @param id The key which will be found.
     * @return The key which is responsible for the given <code>id</code>.
     */
    public Key lookUp(Key id) {
        return findSuccessor(id, LookupType.LOOKUP);
    }

    /**
     * Find successor for <code>id</code>.
     * <p>
     * If <code>id</code> falls between <code>n</code> and its successor,
     * <code>findSuccessor</code> is finished and node <code>n</code> returns
     * its successor. Otherwise, <code>n</code> searches its finger table for
     * the node <code>next</code> whose ID most immediately precedes
     * <code>id</code>, and then invokes <code>findSuccessor</code> at
     * <code>next</code>. The reason behind this choice of <code>next</code> is
     * that the closer <code>next</code> is to <code>id</code>.
     *
     * @param id Identifier searched.
     * @return successor of the given <code>id</code>.
     */
    public Key findSuccessor(Key id, LookupType typeLookUp) {

        Key next;
        Message lookupMessage;

        if (id.isBetweenRightIncluded(key, successor)) {
            return successor;
        } else {
            next = fingersTable.findClosestPresedingNode(id);

            lookupMessage = new MessageXML(Protocol.LOOKUP, next.getValue(),
                    key.getValue());
            lookupMessage.addParam(Protocol.LookupParams.HASHING.name(), id
                    .getHashing().toString());
            lookupMessage.addParam(Protocol.LookupParams.TYPE.name(),
                    typeLookUp.name());

            return communicationManager.sendMessageUnicast(lookupMessage,
                    ChordKey.class, LookupResponseParams.NODE_FIND.name());
        }
    }

    /**
     * Create a new Chord ring.
     */
    public void createRing() {
        predecessor = null;
        successor = key;

        successorList.initializeSuccessors();
    }

    /**
     * Join a Chord ring using node <code>node</code>.
     * <p>
     * The <code>join</code> function asks <code>node</code> to find the
     * immediate successor of <code>n</code>.
     *
     * @param node Identifier of the known node.
     */
    public void join(Key node) {

        Message lookupMessage;

        predecessor = null;

        lookupMessage = new MessageXML(Protocol.LOOKUP, node.getValue(), key
                .getValue());
        lookupMessage.addParam(Protocol.LookupParams.HASHING.name(), key
                .getHashing().toString());
        lookupMessage.addParam(Protocol.LookupParams.TYPE.name(),
                LookupType.JOIN.name());

        successor = communicationManager.sendMessageUnicast(lookupMessage,
                ChordKey.class, LookupResponseParams.NODE_FIND.name());

        successorList.initializeSuccessors();
    }

    /**
     * Accept a new predecessor. Also notifies through by ChordNode.observable
     * the change predecessor
     *
     * @param node Identifier of potential predecessor.
     */
    public void notify(Key node) {
        if (predecessor == null || node.isBetween(predecessor, key)) {
            /*
             * Have to validate if the given key is equal of node's key, then
             * the node must not have a predecessor
             */
            if (node.equals(key)) {
                predecessor = null;
                return;
            }

            predecessor = node;

            String[] message = new String[2];
            message[0] = "REASSIGN";
            message[1] = predecessor.getValue();

            setChanged();
            notifyObservers(message);
            clearChanged();

            logger.debug("Node: '" + key.getValue()
                    + "' Predecessor changed for '" + predecessor.getValue());
        }

        logger.info("Notify to node '" + key.getValue() + "', predecessor is '"
                + predecessor.getValue() + "'");
    }

    /**
     * Called periodically in {@link StableRing }.
     * <p>
     * Clear the node's predecessor pointer if <code>predecessor</code> has
     * failed.
     */
    public void checkPredecessor() {
        if (predecessor == null) {
            return;
        }

        Message pingMessage;

        pingMessage = new MessageXML(Protocol.PING, predecessor.getValue(), key
                .getValue());

        Boolean success = communicationManager.sendMessageUnicast(pingMessage,
                Boolean.class);

        if (success == null) {
            predecessor = null;
        }
    }

    /**
     * Called periodically in {@link StableRing }.
     * <p>
     * Each time node <code>n</code> runs <code>stabilize</code>, it asks its
     * successor for the successor�s predecessor <code>x</code>, and decides
     * whether <code>x</code> should be n�s successor instead. This would be the
     * case if node <code>x</code> recently joined the system. In addition,
     * <code>stabilize</code> notifies node n�s successor of n�s existence,
     * giving the successor the chance to change its predecessor to n. The
     * successor does this only if it knows of no closer predecessor than n.
     */
    public void stabilize() {
        Key x;
        Boolean success;
        Message pingMessage;
        Message getPredecessorMessage;
        Message notifyMessage;

        pingMessage = new MessageXML(Protocol.PING, successor.getValue(), key
                .getValue());

        success = communicationManager.sendMessageUnicast(pingMessage,
                Boolean.class);

        if (success == null) {
            /* When node's successor fails, then node must find a new successor */
            Key successorNew = successorList.getNextSuccessorAvailable();

            logger.error("Node: " + key.getValue() + ", successor failed");

            if (successorNew != null) {
                successor = successorNew;

                fingersTable.setSuccessor(successor);
                successorList.setSuccessor(successor);
            } else {
                /*
                 * If all successors fail, the the node must boot again, that
                 * is, find a node to join the network.
                 */

                logger.error("Node: " + key.getValue()
                        + ", successor list failed... new bootstrap");

                fingersTable = newFingersTable();

                bootUp();
            }
        } else {
            /*
             * Stabilizes by finding a new successor with successor's
             * predecessor
             */
            getPredecessorMessage = new MessageXML(Protocol.GET_PREDECESSOR,
                    successor.getValue(), key.getValue());

            x = communicationManager.sendMessageUnicast(getPredecessorMessage,
                    Key.class);

            if (x != null) {
                if (x.isBetween(key, successor) || key.equals(successor)) {
                    successor = x;

                    fingersTable.setSuccessor(successor);
                    successorList.setSuccessor(successor);
                }
            }

            logger.info("Node '" + key.getValue()
                    + "' stabilized, its succesor is '" + successor.getValue()
                    + "'");

            logger.debug("Node '" + key.getValue() + "' Succesor list '"
                    + successorList + "'");

            notifyMessage = new MessageXML(Protocol.NOTIFY, successor
                    .getValue(), key.getValue());

            communicationManager.sendMessageUnicast(notifyMessage);
        }
    }

    void bootUp() {
        bootStrap.boot(this, communicationManager);
    }

    FingersTable newFingersTable() {
        return new FingersTable(this, keyFactory);
    }

    /**
     * Gets node's table of fingers.
     *
     * @return {@link FingersTable}
     */
    public FingersTable getFingersTable() {
        return fingersTable;
    }

    /**
     * Gets node's key.
     *
     * @return {@link Key}
     */
    public Key getKey() {
        return key;
    }

    /**
     * Gets node's successor.
     *
     * @return {@link Key}
     */
    public Key getSuccessor() {
        return successor;
    }

    /**
     * Sets node's successor in the node and its fingersTable and succesorList.
     *
     * @param successor
     */
    public void setSuccessor(Key successor) {
        this.successor = successor;
        this.fingersTable.setSuccessor(successor);
        this.successorList.setSuccessor(successor);
    }

    /**
     * Sets the predecessor of the node.
     *
     * @param predecessor
     */
    public void setPredecessor(Key predecessor) {
        /*
         * Have to validate if the given key is equal of node's key, then the
         * node must not have a predecessor
         */
        if (predecessor.equals(key)) {
            this.predecessor = null;
        } else {
            this.predecessor = predecessor;
        }
    }

    /**
     * Gets the predecessor of the node.
     *
     * @return {@link Key} the key of the predecessor
     */
    public Key getPredecessor() {
        return predecessor;
    }

    /**
     * Gets the observable of the node.
     *
     * @return Observable
     */
    public Observable getObservable() {
        return this;
    }

    /**
     * Gets the list of successors of the node.
     *
     * @return {@link SuccessorList}
     */
    public SuccessorList getSuccessorList() {
        return successorList;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param object the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the object.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof ChordNode) {
            ChordNode nodeChord = (ChordNode) object;

            return nodeChord.getKey().equals(key);
        }
        return false;
    }

    /**
     * Takes the node out of the network by sending a message of leave.
     *
     * @return ChordKey[] An array with node's successors
     */
    @Override
    public Key[] leave() {

        Message leaveMessage;

        leaveMessage = new MessageXML(Protocol.LEAVE, key.getValue(), key
                .getValue());

        communicationManager.sendMessageUnicast(leaveMessage);

        return successorList.getKeyList();
    }

    /**
     * Gets node's successor list.
     *
     * @return ChordKey[] An array with node's successors
     */
    public Key[] getNeighborsList() {
        return successorList.getKeyList();
    }

}