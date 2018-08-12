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
import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({HashingGenerator.class})
@RunWith(PowerMockRunner.class)
public class SuccessorListTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private ChordKey key1;
    @Mock
    private ChordKey key2;
    @Mock
    private ChordKey key3;
    @Mock
    private ChordKey key;
    private int size = 3;
    @Mock
    private ChordNode chordNode;
    @Mock
    private KeyFactory keyFactory;
    @Mock
    private SequenceGenerator sequenceGenerator;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    private SuccessorList successorList;

    @Before
    public void before() {
        successorList = new SuccessorList(communicationManager, new ChordKey[]{key1, key2, key3}, size, chordNode, sequenceGenerator, keyFactory);
    }

    @Test
    public void fixSuccessors_successorListFromSuccessorNull_doNothing() {
        when(key1.getValue()).thenReturn("successor");
        when(key.getValue()).thenReturn("key");
        when(chordNode.getKey()).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(String.class))).thenReturn(null);

        successorList.fixSuccessors();

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(), eq(String.class));

        assertThat(new Key[]{key1, key2, key3}).isEqualTo(successorList.getKeyList());
        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.GET_SUCCESSOR_LIST);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("successor");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
    }

    @Test
    public void fixSuccessors_successorListFromSuccessorAndGreaterThan3_doNothing() {
        ChordKey newKey1 = mock(ChordKey.class);
        ChordKey newKey2 = mock(ChordKey.class);

        when(key.getValue()).thenReturn("key");
        when(key1.getValue()).thenReturn("successor");
        when(newKey1.getValue()).thenReturn("123");
        when(newKey2.getValue()).thenReturn("456");
        when(keyFactory.newKey("123")).thenReturn(newKey1);
        when(keyFactory.newKey("456")).thenReturn(newKey2);
        when(chordNode.getKey()).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(String.class))).thenReturn("123->456->798->753");

        successorList.fixSuccessors();

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(), eq(String.class));

        assertThat(key1).isEqualTo(successorList.getKeyList()[0]);
        assertThat("123").isEqualTo(successorList.getKeyList()[1].getValue());
        assertThat("456").isEqualTo(successorList.getKeyList()[2].getValue());
        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.GET_SUCCESSOR_LIST);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("successor");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
    }

    @Test
    public void fixSuccessors_successorListFromSuccessorAndLowerThan3_doNothing() {
        ChordKey newKey1 = mock(ChordKey.class);
        ChordKey newKey2 = mock(ChordKey.class);

        when(key1.getValue()).thenReturn("successor");
        when(key.getValue()).thenReturn("key");
        when(newKey1.getValue()).thenReturn("123");
        when(newKey2.getValue()).thenReturn("456");
        when(keyFactory.newKey("123")).thenReturn(newKey1);
        when(keyFactory.newKey("456")).thenReturn(newKey2);
        when(chordNode.getKey()).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(String.class))).thenReturn("123->456");

        successorList.fixSuccessors();

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(), eq(String.class));

        assertThat(key1).isEqualTo(successorList.getKeyList()[0]);
        assertThat("123").isEqualTo(successorList.getKeyList()[1].getValue());
        assertThat(key3).isEqualTo(successorList.getKeyList()[2]);
        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.GET_SUCCESSOR_LIST);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("successor");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
    }

    @Test
    public void initializeSuccessors_set_newList() {
        when(chordNode.getSuccessor()).thenReturn(key);

        successorList.initializeSuccessors();

        assertThat(successorList.getKeyList()).isEqualTo(new Key[]{key, key, key});
    }

    @Test
    public void setSuccessor_set_newSuccessor() {
        when(key.getValue()).thenReturn("key");
        when(chordNode.getKey()).thenReturn(key);

        successorList.setSuccessor(key);

        assertThat(successorList.getKeyList()[0]).isEqualTo(key);
    }

    @Test
    public void getNextSuccessorAvailable_notFound_returnNull() {
        mockStatic(HashingGenerator.class);
        //when(HashingGenerator.getInstance()).thenReturn(hashingGenerator);

        when(key1.getValue()).thenReturn("key1");
        when(key2.getValue()).thenReturn("key2");
        when(key3.getValue()).thenReturn("key3");
        when(key.getValue()).thenReturn("key");
        when(chordNode.getKey()).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(Boolean.class))).thenReturn(null);

        Key successor = successorList.getNextSuccessorAvailable();

        verify(communicationManager, times(3)).sendMessageUnicast(messageCaptor.capture(), eq(Boolean.class));

        assertThat(successor).isNull();
        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key1");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getAllValues().get(1).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("key2");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getAllValues().get(2).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(2).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getDestination()).isEqualTo("key3");
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getSource()).isEqualTo(key.getValue());
    }
    @Test
    public void getNextSuccessorAvailable_found_returnSuccessor() {
        mockStatic(HashingGenerator.class);
        //when(HashingGenerator.getInstance()).thenReturn(hashingGenerator);

        when(key1.getValue()).thenReturn("key1");
        when(key2.getValue()).thenReturn("key2");
        when(key3.getValue()).thenReturn("key3");
        when(key.getValue()).thenReturn("key");
        when(chordNode.getKey()).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(Boolean.class))).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                if (++count == 1)
                    return null;

                return true;
            }
        });

        Key successor = successorList.getNextSuccessorAvailable();

        verify(communicationManager, times(2)).sendMessageUnicast(messageCaptor.capture(), eq(Boolean.class));

        assertThat(successor).isEqualTo(key2);
        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key1");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getAllValues().get(1).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("key2");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo(key.getValue());
    }

}