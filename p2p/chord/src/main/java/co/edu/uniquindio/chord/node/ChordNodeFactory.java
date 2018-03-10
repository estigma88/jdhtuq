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
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.apache.log4j.Logger;

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

    private final int stableRingTime;
    private final int successorListAmount;

    /**
     * Communication manager
     */
    private final CommunicationManager communicationManager;
    private final BootStrap bootStrap;
    private final ScheduledExecutorService scheduledStableRing;
    private final List<Observer> stableRingObservers;
    private final KeyFactory keyFactory;
    private final SequenceGenerator sequenceGenerator;

    public ChordNodeFactory(CommunicationManager communicationManager, Set<String> names, int stableRingTime, int successorListAmount, BootStrap bootStrap, ScheduledExecutorService scheduledStableRing, List<Observer> stableRingObservers, KeyFactory keyFactory, SequenceGenerator sequenceGenerator) {
        this.communicationManager = communicationManager;
        this.stableRingTime = stableRingTime;
        this.successorListAmount = successorListAmount;
        this.bootStrap = bootStrap;
        this.scheduledStableRing = scheduledStableRing;
        this.stableRingObservers = stableRingObservers;
        this.keyFactory = keyFactory;
        this.sequenceGenerator = sequenceGenerator;
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

        key = getKey(name);
        nodeChord = getNodeChord(key);

        logger.info("Created node with name '" + nodeChord.getKey().getValue()
                + "' and hashing '" + nodeChord.getKey().getHashing()
                + "'");

        StableRing stableRing = getStableRing(nodeChord);

        ScheduledFuture<?> stableRingTask = scheduledStableRing.scheduleAtFixedRate(stableRing, START_STABLE_RING, stableRingTime, TimeUnit.MILLISECONDS);

        nodeEnviroment = getNodeEnviroment(nodeChord, stableRingTask);

        communicationManager.addMessageProcessor(name, nodeEnviroment);

        bootStrap.boot(nodeChord, communicationManager, sequenceGenerator);

        return nodeChord;
    }


    Key getKey(String name) {
        return keyFactory.newKey(name);
    }

    StableRing getStableRing(ChordNode nodeChord) {
        StableRing stableRing = new StableRing(nodeChord);

        for (Observer observer : stableRingObservers) {
            stableRing.addObserver(observer);
        }

        return stableRing;
    }

    NodeEnvironment getNodeEnviroment(ChordNode nodeChord, ScheduledFuture<?> stableRing) {
        return new NodeEnvironment(communicationManager, nodeChord, stableRing, this, keyFactory, sequenceGenerator);
    }

    ChordNode getNodeChord(Key key) {
        return new ChordNode(key, communicationManager, successorListAmount, bootStrap, keyFactory, sequenceGenerator);
    }

    /**
     * Removes the node form the ring.
     *
     * @param name The key of the node that will be destroyed.
     */
    public void destroyNode(String name) {
        communicationManager.removeObserver(name);
    }
}
