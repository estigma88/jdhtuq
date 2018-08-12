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
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChordNodeFactoryTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private Chord chord;
    @Mock
    private ChordNode chordNode;
    @Mock
    private NodeEnvironment nodeEnviroment;
    @Mock
    private ChordKey key;
    @Mock
    private BootStrap bootStrap;
    @Mock
    private ScheduledExecutorService scheduledExecutorService;
    @Mock
    private KeyFactory keyFactory;
    @Mock
    private SequenceGenerator sequenceGenerator;
    @Mock
    private ScheduledFuture stableRingTask;
    @Mock
    private StableRing stableRing;
    private ChordNodeFactory chordNodeFactory;

    @Before
    public void before() {
        Set<String> names = new HashSet<>();
        names.add("0");
        names.add("1");

        chordNodeFactory = spy(new ChordNodeFactory(communicationManager, names, 2000, 3, bootStrap, scheduledExecutorService, Collections.emptyList(), keyFactory, sequenceGenerator));
    }

    @Test
    public void createNode_byName_nodeCreated() throws ChordNodeFactoryException {
        when(key.getValue()).thenReturn("key");
        when(chordNode.getKey()).thenReturn(key);
        doReturn(key).when(chordNodeFactory).getKey("2");
        doReturn(stableRing).when(chordNodeFactory).getStableRing(chordNode);
        when(scheduledExecutorService.scheduleAtFixedRate(stableRing, 5000, 2000, TimeUnit.MILLISECONDS)).thenReturn(stableRingTask);
        doReturn(chordNode).when(chordNodeFactory).getNodeChord(key);
        doReturn(nodeEnviroment).when(chordNodeFactory).getNodeEnviroment(chordNode);
        doNothing().when(chordNode).setStableRing(stableRingTask);

        Chord node = chordNodeFactory.createNode("2");

        assertThat(node).isEqualTo(chordNode);

        verify(chordNode).setStableRing(stableRingTask);
        verify(communicationManager).addMessageProcessor("2", nodeEnviroment);
    }

    @Test
    public void destroyNode_byName_nodeDestroyed() throws OverlayException {
        chordNodeFactory.destroyNode(chordNode);

        verify(chordNode).leave();
    }
}