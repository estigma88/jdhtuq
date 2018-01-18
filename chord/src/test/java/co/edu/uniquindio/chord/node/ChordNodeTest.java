package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.hashing.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private ArgumentCaptor<MessageXML> messageCaptor;

    private ChordNode chordNode;

    @Before
    public void before() {
        chordNode = new ChordNode(communicationManager, successor, predecessor, fingersTable, successorList, key, observable);
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
        Key lookUpKey = mock(Key.class);

        when(id.isBetweenRightIncluded(key, successor)).thenReturn(false);
        when(fingersTable.findClosestPresedingNode(id)).thenReturn(next);
        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Key.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()))).thenReturn(lookUpKey);

        Key result = chordNode.lookUp(id);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Key.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()));

        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo(next.getValue());
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.HASHING.name())).isEqualTo(id
                .getStringHashing());
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

        Key successorResult = mock(Key.class);

        when(communicationManager.sendMessageUnicast(anyObject(),
                eq(Key.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()))).thenReturn(successorResult);

        chordNode.join(node);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Key.class), eq(Protocol.LookupResponseParams.NODE_FIND.name()));

        assertThat(chordNode.getPredecessor()).isNull();
        assertThat(chordNode.getSuccessor()).isEqualTo(successorResult);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo(node.getValue());
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.HASHING.name())).isEqualTo(key
                .getStringHashing());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.TYPE.name())).isEqualTo(LookupType.JOIN.name());

        verify(successorList).initializeSuccessors();
    }

    @Test
    public void notify_predecessorNotNullAndIsNotBetween_notNotify(){
        Key node = mock(Key.class);

        when(node.isBetween(predecessor, key)).thenReturn(false);

        chordNode.notify(node);

        verifyNoMoreInteractions(observable);
    }

    @Test
    public void notify_predecessorIsNullAndNodeEqualKey_predecessorNull(){
        Key node = key;

        chordNode = new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, observable);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void notify_predecessorIsNullIsBetweenAndNodeEqualKey_predecessorNull(){
        Key node = key;

        when(node.isBetween(predecessor, key)).thenReturn(true);

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isNull();
    }

    @Test
    public void notify_predecessorIsNullAndNodeNotEqualKey_predecessorNull(){
        Key node = mock(Key.class);

        chordNode = new ChordNode(communicationManager, successor, null, fingersTable, successorList, key, observable);

        String[] message = new String[2];
        message[0] = "REASSIGN";
        message[1] = null;

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isEqualTo(node);

        verify(observable).notifyMessage(message);
    }

    @Test
    public void notify_predecessorIsNotNullAndNodeNotEqualKey_predecessorNull(){
        Key node = mock(Key.class);

        String[] message = new String[2];
        message[0] = "REASSIGN";
        message[1] = "hashPredecessor";

        when(node.isBetween(predecessor, key)).thenReturn(true);
        when(node.getValue()).thenReturn("hashPredecessor");

        chordNode.notify(node);

        assertThat(chordNode.getPredecessor()).isEqualTo(node);

        verify(observable).notifyMessage(message);
    }
}