package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.overlay.Key;
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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChordNodeTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private Key successor;
    @Mock
    private Key predecessor;
    @Mock
    private FingersTable fingersTable;
    @Mock
    private SuccessorList successorList;
    @Mock
    private Key key;
    @Mock
    private Observable<Object> observable;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    @Mock
    private SequenceGenerator sequenceGenerator;

    private ChordNode chordNode;

    @Before
    public void before() {
        chordNode = spy(new ChordNode(communicationManager, successor, predecessor, fingersTable, successorList, key, sequenceGenerator));
    }

    @Test
    public void lookUp_isBetweenRightIncluded_successor() {
        Key id = mock(Key.class);

        when(id.isBetweenRightIncluded(key, successor)).thenReturn(true);

        Key result = chordNode.lookUp(id);

        assertThat(result).isEqualTo(successor);
    }

    @Test
    public void lookUp_isNotBetweenRightIncluded_successor() {
        Key id = mock(Key.class);
        Key next = mock(Key.class);
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
        Key node = mock(Key.class);

        when(node.isBetween(predecessor, key)).thenReturn(false);

        chordNode.notify(node);

        verifyNoMoreInteractions(observable);
    }

    @Test
    public void notify_predecessorIsNullAndNodeEqualKey_predecessorNull() {
        Key node = key;

        chordNode = new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, sequenceGenerator);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void notify_predecessorIsNullIsBetweenAndNodeEqualKey_predecessorNull() {
        Key node = key;

        when(node.isBetween(predecessor, key)).thenReturn(true);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void notify_predecessorIsNullAndNodeNotEqualKey_predecessorNull() {
        Key node = mock(Key.class);

        Message message = Message.builder()
                .messageType(Protocol.RE_ASSIGN)
                .param(Protocol.ReAssignParams.PREDECESSOR.name(), null)
                .build();

        chordNode = spy(new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, sequenceGenerator));

        doNothing().when(chordNode).notifyObservers(message);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isEqualTo(node);

        verify(chordNode).notifyObservers(message);
    }

    @Test
    public void notify_predecessorIsNotNullAndNodeNotEqualKey_predecessorNull() {
        Key node = mock(Key.class);

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
        chordNode = new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, sequenceGenerator);

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

        chordNode = new ChordNode(communicationManager, successor, predecessor, fingersTable, successorList, successor, sequenceGenerator);

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
        Key successorNew = mock(Key.class);

        when(successorList.getNextSuccessorAvailable()).thenReturn(successorNew);
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Boolean.class))).thenReturn(null);

        chordNode.stabilize();

        assertThat(chordNode.getSuccessor()).isEqualTo(successorNew);

        verify(successorList).setSuccessor(successorNew);
        verify(fingersTable).setSuccessor(successorNew);
    }

    @Test
    public void stabilize_pingSuccessorNull_bootUp() {
        Key successorNew = null;
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
        Key successorNew = mock(Key.class);

        chordNode.setSuccessor(successorNew);

        verify(successorList).setSuccessor(successorNew);
        verify(fingersTable).setSuccessor(successorNew);
    }

    @Test
    public void setPredecessor_predecessorNotEqualKey_setNewPredecessor() {
        Key predecessorNew = mock(Key.class);

        chordNode.setPredecessor(predecessorNew);

        assertThat(chordNode.getPredecessor()).isEqualTo(predecessorNew);
    }

    @Test
    public void setPredecessor_predecessorEqualKey_setNull() {
        Key predecessorNew = mock(Key.class);

        chordNode = new ChordNode(communicationManager, successor, predecessor, fingersTable, successorList, predecessorNew, sequenceGenerator);

        chordNode.setPredecessor(predecessorNew);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void leave_predecessorNotEqualKey_setNewPredecessor() {
        Key[] keys = {mock(Key.class), mock(Key.class)};

        when(key.getValue()).thenReturn("hashKey");
        when(successorList.getKeyList()).thenReturn(keys);

        Key[] keysResult = chordNode.leave();

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LEAVE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("hashKey");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("hashKey");

        assertThat(keysResult).isEqualTo(keys);
    }
}