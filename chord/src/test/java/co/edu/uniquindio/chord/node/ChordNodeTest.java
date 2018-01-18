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
        Key next = mock(Key.class);

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
}