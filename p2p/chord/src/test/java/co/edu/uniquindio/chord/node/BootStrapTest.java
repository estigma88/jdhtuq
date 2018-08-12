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
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BootStrapTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private ChordNode nodeChord;
    @Mock
    private ChordKey successor;
    @Mock
    private ChordKey key;
    @Mock
    private ChordKey foundNode;
    @Mock
    private FingersTable fingersTable;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    @Mock
    private SequenceGenerator sequenceGenerator;
    @InjectMocks
    private BootStrap bootStrap;

    @Test
    public void boot_notOtherNodeFound_createRing() {
        when(fingersTable.getFingersTable()).thenReturn(new ChordKey[1]);
        when(nodeChord.getFingersTable()).thenReturn(fingersTable);
        when(nodeChord.getSuccessor()).thenReturn(successor);
        when(nodeChord.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("keyHash");

        bootStrap.boot(nodeChord, communicationManager, sequenceGenerator);

        verify(nodeChord).createRing();
        verify(communicationManager).sendMessageMultiCast(messageCaptor.capture(),
                eq(ChordKey.class));

        assertThat(fingersTable.getFingersTable()[0]).isEqualTo(successor);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.BOOTSTRAP);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isNull();
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("keyHash");
    }

    @Test
    public void boot_otherNodeFound_join() {
        when(fingersTable.getFingersTable()).thenReturn(new ChordKey[1]);
        when(nodeChord.getFingersTable()).thenReturn(fingersTable);
        when(nodeChord.getSuccessor()).thenReturn(successor);
        when(nodeChord.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("keyHash");
        when(communicationManager.sendMessageMultiCast(any(), eq(ChordKey.class))).thenReturn(foundNode);

        bootStrap.boot(nodeChord, communicationManager, sequenceGenerator);

        verify(nodeChord).join(foundNode);
        verify(communicationManager).sendMessageMultiCast(messageCaptor.capture(),
                eq(ChordKey.class));

        assertThat(fingersTable.getFingersTable()[0]).isEqualTo(successor);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.BOOTSTRAP);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isNull();
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("keyHash");
    }

}