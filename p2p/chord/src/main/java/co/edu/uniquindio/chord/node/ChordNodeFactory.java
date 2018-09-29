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

import co.edu.uniquindio.chord.Chord;
import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.overlay.*;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
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
    private final IdGenerator sequenceGenerator;

    public ChordNodeFactory(CommunicationManager communicationManager, Set<String> names, int stableRingTime, int successorListAmount, BootStrap bootStrap, ScheduledExecutorService scheduledStableRing, List<Observer> stableRingObservers, KeyFactory keyFactory, IdGenerator sequenceGenerator) {
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
        ChordKey key = getKey(name);
        ChordNode chordNode = getNodeChord(key);

        logger.info("Created node with name '" + chordNode.getKey().getValue()
                + "' and hashing '" + chordNode.getKey().getHashing()
                + "'");

        StableRing stableRing = getStableRing(chordNode);

        ScheduledFuture<?> stableRingTask = scheduledStableRing.scheduleAtFixedRate(stableRing, START_STABLE_RING, stableRingTime, TimeUnit.MILLISECONDS);

        chordNode.setStableRing(stableRingTask);

        NodeEnvironment nodeEnviroment = getNodeEnviroment(chordNode);

        communicationManager.addMessageProcessor(name, nodeEnviroment);

        bootStrap.boot(chordNode, communicationManager, sequenceGenerator);

        return chordNode;
    }


    ChordKey getKey(String name) {
        return (ChordKey) keyFactory.newKey(name);
    }

    StableRing getStableRing(ChordNode nodeChord) {
        StableRing stableRing = new StableRing(nodeChord);

        for (Observer observer : stableRingObservers) {
            stableRing.addObserver(observer);
        }

        return stableRing;
    }

    NodeEnvironment getNodeEnviroment(ChordNode nodeChord) {
        return new NodeEnvironment(communicationManager, nodeChord, keyFactory, sequenceGenerator);
    }

    ChordNode getNodeChord(ChordKey key) {
        return new ChordNode(key, communicationManager, successorListAmount, bootStrap, keyFactory, sequenceGenerator);
    }

    @Override
    public void destroyNode(OverlayNode overlayNode) throws OverlayException {
        overlayNode.leave();
    }
}
