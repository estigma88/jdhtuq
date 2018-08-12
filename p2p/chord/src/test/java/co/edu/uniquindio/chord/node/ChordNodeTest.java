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
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChordNodeTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private ChordKey successor;
    @Mock
    private ChordKey predecessor;
    @Mock
    private FingersTable fingersTable;
    @Mock
    private SuccessorList successorList;
    @Mock
    private ChordKey key;
    @Mock
    private Observable<Object> observable;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    @Mock
    private SequenceGenerator sequenceGenerator;
    @Mock
    private ScheduledFuture<?> stableRing;
    @Mock
    private Message message;

    private ChordNode chordNode;

    @Before
    public void before() {
        chordNode = spy(new ChordNode(communicationManager, successor, predecessor, fingersTable, successorList, key, sequenceGenerator, stableRing));
    }

    @Test
    public void lookUp_isBetweenRightIncluded_successor() {
        ChordKey id = mock(ChordKey.class);

        when(id.isBetweenRightIncluded(key, successor)).thenReturn(true);

        Key result = chordNode.lookUp(id);

        assertThat(result).isEqualTo(successor);
    }

    @Test
    public void lookUp_isNotBetweenRightIncluded_successor() {
        ChordKey id = mock(ChordKey.class);
        ChordKey next = mock(ChordKey.class);
        ChordKey lookUpKey = mock(ChordKey.class);

        when(id.isBetweenRightIncluded(key, successor)).thenReturn(false);
        when(fingersTable.findClosestPresedingNode(id)).thenReturn(next);
        when(communicationManager.sendMessageUnicast(any(),
                eq(ChordKey.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()))).thenReturn(lookUpKey);
        when(id.getHashing()).thenReturn(new BigInteger("123565"));

        Key result = chordNode.lookUp(id);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(ChordKey.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()));

        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo(next.getValue());
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.HASHING.name())).isEqualTo(id
                .getHashing().toString());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.TYPE.name())).isEqualTo(LookupType.LOOKUP.name());
        assertThat(lookUpKey).isEqualTo(result);
    }

    @Test
    public void lookUp_successorIsNull_successor() {
        chordNode = new ChordNode(communicationManager, null, predecessor, fingersTable, successorList, key, sequenceGenerator, stableRing);

        ChordKey id = mock(ChordKey.class);
        ChordKey next = mock(ChordKey.class);
        ChordKey lookUpKey = mock(ChordKey.class);

        when(fingersTable.findClosestPresedingNode(id)).thenReturn(next);
        when(communicationManager.sendMessageUnicast(any(),
                eq(ChordKey.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()))).thenReturn(lookUpKey);
        when(id.getHashing()).thenReturn(new BigInteger("123565"));

        Key result = chordNode.lookUp(id);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(ChordKey.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()));

        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo(next.getValue());
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.HASHING.name())).isEqualTo(id
                .getHashing().toString());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.TYPE.name())).isEqualTo(LookupType.LOOKUP.name());
        assertThat(lookUpKey).isEqualTo(result);
    }

    @Test
    public void createRing_create_initializeSuccessors() {
        chordNode.createRing();

        assertThat(chordNode.getPredecessor()).isNull();
        assertThat(chordNode.getSuccessor()).isEqualTo(chordNode.getKey());

        verify(successorList).initializeSuccessors();
    }

    @Test
    public void join_sendMessage_initializeSuccessors() {
        Key node = mock(Key.class);
        when(key.getHashing()).thenReturn(new BigInteger("123565"));

        ChordKey successorResult = mock(ChordKey.class);

        when(communicationManager.sendMessageUnicast(any(),
                eq(ChordKey.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()))).thenReturn(successorResult);

        chordNode.join(node);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(ChordKey.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()));

        assertThat(chordNode.getPredecessor()).isNull();
        assertThat(chordNode.getSuccessor()).isEqualTo(successorResult);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo(node.getValue());
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.HASHING.name())).isEqualTo(key
                .getHashing().toString());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.TYPE.name())).isEqualTo(LookupType.JOIN.name());

        verify(successorList).initializeSuccessors();
    }

    @Test
    public void notify_predecessorNotNullAndIsNotBetween_notNotify() {
        ChordKey node = mock(ChordKey.class);

        when(node.isBetween(predecessor, key)).thenReturn(false);

        chordNode.notify(node);

        verifyNoMoreInteractions(observable);
    }

    @Test
    public void notify_predecessorIsNullAndNodeEqualKey_predecessorNull() {
        ChordKey node = key;

        chordNode = new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, sequenceGenerator, stableRing);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void notify_predecessorIsNullIsBetweenAndNodeEqualKey_predecessorNull() {
        ChordKey node = key;

        when(node.isBetween(predecessor, key)).thenReturn(true);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void notify_predecessorIsNullAndNodeNotEqualKey_predecessorNull() {
        ChordKey node = mock(ChordKey.class);

        Message message = Message.builder()
                .messageType(Protocol.RE_ASSIGN)
                .param(Protocol.ReAssignParams.PREDECESSOR.name(), null)
                .build();

        chordNode = spy(new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, sequenceGenerator, stableRing));

        doNothing().when(chordNode).notifyObservers(message);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isEqualTo(node);

        verify(chordNode).notifyObservers(message);
    }

    @Test
    public void notify_predecessorIsNotNullAndNodeNotEqualKey_predecessorNull() {
        ChordKey node = mock(ChordKey.class);

        Message message = Message.builder()
                .messageType(Protocol.RE_ASSIGN)
                .param(Protocol.ReAssignParams.PREDECESSOR.name(), "hashPredecessor")
                .build();

        when(node.isBetween(predecessor, key)).thenReturn(true);
        when(node.getValue()).thenReturn("hashPredecessor");
        doNothing().when(chordNode).notifyObservers(message);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isEqualTo(node);

        verify(chordNode).notifyObservers(message);
    }

    @Test
    public void checkPredecessor_predecessorIsNotNull_doNothing() {
        chordNode = new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, sequenceGenerator, stableRing);

        chordNode.checkPredecessor();

        verifyZeroInteractions(communicationManager);
    }

    @Test
    public void checkPredecessor_pingSuccess_predecessorNotNull() {
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Boolean.class))).thenReturn(true);
        when(predecessor.getValue()).thenReturn("hashPredecessor");
        when(key.getValue()).thenReturn("hashKey");

        chordNode.checkPredecessor();

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));


        assertThat(chordNode.getPredecessor()).isEqualTo(predecessor);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("hashPredecessor");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("hashKey");
    }

    @Test
    public void checkPredecessor_pingNoSuccess_predecessorNull() {
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Boolean.class))).thenReturn(null);
        when(predecessor.getValue()).thenReturn("hashPredecessor");
        when(key.getValue()).thenReturn("hashKey");

        chordNode.checkPredecessor();

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));

        assertThat(chordNode.getPredecessor()).isNull();
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("hashPredecessor");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("hashKey");
    }

    @Test
    public void stabilize_pingSuccessorNotNullNotPredecessor_notifyChange() {
        ChordKey getPredecessor = null;

        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Boolean.class))).thenReturn(true);
        when(successor.getValue()).thenReturn("hashSuccessor");
        when(key.getValue()).thenReturn("hashKey");
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(ChordKey.class))).thenReturn(getPredecessor);

        chordNode.stabilize();

        assertThat(chordNode.getSuccessor()).isEqualTo(successor);

        verifyZeroInteractions(successorList);
        verifyZeroInteractions(fingersTable);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(ChordKey.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("hashKey");
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("hashKey");
        assertThat(messageCaptor.getAllValues().get(2).getMessageType()).isEqualTo(Protocol.NOTIFY);
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getSource()).isEqualTo("hashKey");
    }

    @Test
    public void stabilize_pingSuccessorNotNullGetPredecessorNotBetweenNotKey_notifyChange() {
        ChordKey getPredecessor = mock(ChordKey.class);

        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Boolean.class))).thenReturn(true);
        when(successor.getValue()).thenReturn("hashSuccessor");
        when(key.getValue()).thenReturn("hashKey");
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(ChordKey.class))).thenReturn(getPredecessor);

        chordNode.stabilize();

        assertThat(chordNode.getSuccessor()).isEqualTo(successor);

        verifyZeroInteractions(successorList);
        verifyZeroInteractions(fingersTable);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(ChordKey.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("hashKey");
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("hashKey");
        assertThat(messageCaptor.getAllValues().get(2).getMessageType()).isEqualTo(Protocol.NOTIFY);
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getSource()).isEqualTo("hashKey");
    }

    @Test
    public void stabilize_pingSuccessorNotNullGetPredecessorIsBetweenNotKey_notifyChange() {
        ChordKey getPredecessor = mock(ChordKey.class);

        when(getPredecessor.isBetween(key, successor)).thenReturn(true);
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Boolean.class))).thenReturn(true);
        when(successor.getValue()).thenReturn("hashSuccessor");
        when(getPredecessor.getValue()).thenReturn("hashGetPredecessor");
        when(key.getValue()).thenReturn("hashKey");
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(ChordKey.class))).thenReturn(getPredecessor);

        chordNode.stabilize();

        assertThat(chordNode.getSuccessor()).isEqualTo(getPredecessor);

        verify(successorList).setSuccessor(getPredecessor);
        verify(fingersTable).setSuccessor(getPredecessor);
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(ChordKey.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("hashKey");
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("hashKey");
        assertThat(messageCaptor.getAllValues().get(2).getMessageType()).isEqualTo(Protocol.NOTIFY);
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getDestination()).isEqualTo("hashGetPredecessor");
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getSource()).isEqualTo("hashKey");
    }

    @Test
    public void stabilize_pingSuccessorNotNullGetPredecessorNotBetweenKeyEqual_notifyChange() {
        ChordKey getPredecessor = mock(ChordKey.class);

        chordNode = new ChordNode(communicationManager, successor, predecessor, fingersTable, successorList, successor, sequenceGenerator, stableRing);

        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Boolean.class))).thenReturn(true);
        when(successor.getValue()).thenReturn("hashSuccessor");
        when(getPredecessor.getValue()).thenReturn("hashGetPredecessor");
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(ChordKey.class))).thenReturn(getPredecessor);

        chordNode.stabilize();

        assertThat(chordNode.getSuccessor()).isEqualTo(getPredecessor);

        verify(successorList).setSuccessor(getPredecessor);
        verify(fingersTable).setSuccessor(getPredecessor);
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(ChordKey.class));
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PING);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("hashSuccessor");
        assertThat(messageCaptor.getAllValues().get(2).getMessageType()).isEqualTo(Protocol.NOTIFY);
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getDestination()).isEqualTo("hashGetPredecessor");
        assertThat(messageCaptor.getAllValues().get(2).getAddress().getSource()).isEqualTo("hashSuccessor");
    }

    @Test
    public void stabilize_pingSuccessorNull_setNextSuccessor() {
        ChordKey successorNew = mock(ChordKey.class);

        when(successorList.getNextSuccessorAvailable()).thenReturn(successorNew);
        when(communicationManager.sendMessageUnicast(any(),
                eq(Boolean.class))).thenReturn(null);

        chordNode.stabilize();

        assertThat(chordNode.getSuccessor()).isEqualTo(successorNew);

        verify(successorList).setSuccessor(successorNew);
        verify(fingersTable).setSuccessor(successorNew);
    }

    @Test
    public void stabilize_pingSuccessorNull_bootUp() {
        ChordKey successorNew = null;
        FingersTable fingersTable = mock(FingersTable.class);

        when(successorList.getNextSuccessorAvailable()).thenReturn(successorNew);
        doReturn(fingersTable).when(chordNode).newFingersTable();
        doNothing().when(chordNode).bootUp();

        chordNode.stabilize();

        verify(chordNode).newFingersTable();
        verify(chordNode).bootUp();
    }

    @Test
    public void setSuccessor_set_newSuccessor() {
        ChordKey successorNew = mock(ChordKey.class);

        chordNode.setSuccessor(successorNew);

        verify(successorList).setSuccessor(successorNew);
        verify(fingersTable).setSuccessor(successorNew);
    }

    @Test
    public void setPredecessor_predecessorNotEqualKey_setNewPredecessor() {
        ChordKey predecessorNew = mock(ChordKey.class);

        chordNode.setPredecessor(predecessorNew);

        assertThat(chordNode.getPredecessor()).isEqualTo(predecessorNew);
    }

    @Test
    public void setPredecessor_predecessorEqualKey_setNull() {
        ChordKey predecessorNew = mock(ChordKey.class);

        chordNode = new ChordNode(communicationManager, successor, predecessor, fingersTable, successorList, predecessorNew, sequenceGenerator, stableRing);

        chordNode.setPredecessor(predecessorNew);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void leave_successorEqualsKey_onlyUnlinkNode() throws OverlayException {
        chordNode = spy(new ChordNode(communicationManager, key, predecessor, fingersTable, successorList, key, sequenceGenerator, stableRing));

        ChordKey[] keys = {mock(ChordKey.class), mock(ChordKey.class)};

        when(key.getValue()).thenReturn("hashKey");
        when(successorList.getKeyList()).thenReturn(keys);

        Key[] keysResult = chordNode.leave();

        verify(stableRing).cancel(true);
        verify(communicationManager).removeMessageProcessor(chordNode.getKey().getValue());
        assertThat(keysResult).isEqualTo(keys);
    }

    @Test
    public void leave_successorNotEqualsKeyAndPredecessorNull_notifyAndUnlinkNode() throws OverlayException {
        chordNode = spy(new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, sequenceGenerator, stableRing));

        ChordKey[] keys = {mock(ChordKey.class), mock(ChordKey.class)};

        when(key.getValue()).thenReturn("hashKey");
        when(successorList.getKeyList()).thenReturn(keys);

        Key[] keysResult = chordNode.leave();

        verify(stableRing).cancel(true);
        verify(communicationManager).removeMessageProcessor(chordNode.getKey().getValue());
        assertThat(keysResult).isEqualTo(keys);
    }


    @Test
    public void leave_successorNotEqualsKey_notifyAndUnlinkNode() throws OverlayException {
        ChordKey[] keys = {mock(ChordKey.class), mock(ChordKey.class)};

        when(successorList.getKeyList()).thenReturn(keys);
        when(predecessor.getValue()).thenReturn("predecessor");
        when(successor.getValue()).thenReturn("successor");
        when(key.getValue()).thenReturn("key");

        ChordKey[] keysResult = chordNode.leave();

        verify(stableRing).cancel(true);

        assertThat(keysResult).isEqualTo(keys);

        verify(communicationManager, times(2)).sendMessageUnicast(messageCaptor.capture());
        verify(communicationManager).removeMessageProcessor(chordNode.getKey().getValue());

        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.SET_SUCCESSOR);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("predecessor");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.SetSuccessorParams.SUCCESSOR.name())).isEqualTo("successor");

        assertThat(messageCaptor.getAllValues().get(1).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.SET_PREDECESSOR);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("successor");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(1).getParam(Protocol.SetPredecessorParams.PREDECESSOR.name())).isEqualTo("predecessor");
    }

    @Test
    public void stopStabilizing_stop(){
        chordNode.stopStabilizing();

        verify(stableRing).cancel(true);
    }

}