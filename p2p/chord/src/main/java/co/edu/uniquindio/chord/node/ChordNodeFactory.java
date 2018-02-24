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
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.hashing.Key;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.List;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The <code>ChordNodeFactory</code> class creates nodes <code>ChordNode</code>.
 * Load properties file for communication called
 * chord_properties/communication.xml and initialized hashing class
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see Chord
 * @see ChordNode
 * @since 1.0
 */
public class ChordNodeFactory implements OverlayNodeFactory {

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(ChordNodeFactory.class);
    public static final int START_STABLE_RING = 5000;

    private int stableRingTime;
    private int successorListAmount;

    /**
     * Communication manager
     */
    private CommunicationManager communicationManager;

    /**
     * Allows to create a node with no specified name.
     */
    private int nodeKey;
    /**
     * List of names of the nodes created
     */
    private Set<String> names;
    private BootStrap bootStrap;
    private ScheduledExecutorService scheduledStableRing;
    private List<Observer> stableRingObservers;

    public ChordNodeFactory(CommunicationManager communicationManager, Set<String> names, int stableRingTime, int successorListAmount, BootStrap bootStrap, ScheduledExecutorService scheduledStableRing, List<Observer> stableRingObservers) {
        this.communicationManager = communicationManager;
        this.names = names;
        this.stableRingTime = stableRingTime;
        this.successorListAmount = successorListAmount;
        this.bootStrap = bootStrap;
        this.scheduledStableRing = scheduledStableRing;
        this.stableRingObservers = stableRingObservers;
    }

    /**
     * Creates a ChordNode with a default name and starts the node.
     *
     * @return {@link Chord}
     * @throws ChordNodeFactoryException
     */
    public Chord createNode() throws ChordNodeFactoryException {

        nodeKey++;
        while (names.contains(String.valueOf(nodeKey))) {
            nodeKey++;
        }

        return createNode(String.valueOf(nodeKey));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * co.edu.uniquindio.overlay.OverlayNodeFactory#createNode(java.lang.String)
     */
    public Chord createNode(String name) throws ChordNodeFactoryException {
        ChordNode nodeChord;
        NodeEnvironment nodeEnviroment;
        Key key;

        if (!names.contains(name)) {
            names.add(name);
        } else {
            NameAlreadyInUseException exception = new NameAlreadyInUseException(
                    "The specified name is already being used");

            logger.fatal("The specified name is already being used", exception);

            throw exception;
        }

        key = getKey(name);
        nodeChord = getNodeChord(key);

        logger.info("Created node with name '" + nodeChord.getKey().getValue()
                + "' and hashing '" + nodeChord.getKey().getStringHashing()
                + "'");

        StableRing stableRing = getStableRing(nodeChord);

        ScheduledFuture<?> stableRingTask = scheduledStableRing.scheduleAtFixedRate(stableRing, START_STABLE_RING, stableRingTime, TimeUnit.MILLISECONDS);

        nodeEnviroment = getNodeEnviroment(nodeChord, stableRingTask);

        communicationManager.addObserver(nodeEnviroment);

        bootStrap.boot(nodeChord, communicationManager);

        return nodeChord;
    }


    Key getKey(String name) {
        return new Key(name);
    }

    StableRing getStableRing(ChordNode nodeChord) {
        StableRing stableRing = new StableRing(nodeChord);

        for (Observer observer : stableRingObservers) {
            stableRing.addObserver(observer);
        }

        return stableRing;
    }

    NodeEnvironment getNodeEnviroment(ChordNode nodeChord, ScheduledFuture<?> stableRing) {
        return new NodeEnvironment(communicationManager, nodeChord, stableRing, this);
    }

    ChordNode getNodeChord(Key key) {
        return new ChordNode(key, communicationManager, successorListAmount, bootStrap);
    }

    /**
     * Removes the node form the ring.
     *
     * @param name The key of the node that will be destroyed.
     */
    public void destroyNode(String name) {
        names.remove(name);

        communicationManager.removeObserver(name);
    }

    /**
     * Creates node from Internet Address
     */
    public OverlayNode createNode(InetAddress inetAddress)
            throws ChordNodeFactoryException {
        return createNode(inetAddress.getHostAddress());
    }

    Set<String> getNames() {
        return names;
    }
}
