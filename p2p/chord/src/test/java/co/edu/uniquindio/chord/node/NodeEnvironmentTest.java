package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.message.Address;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NodeEnvironmentTest {
    @Mock
    private Key found;
    @Mock
    private Key key;
    @Mock
    private Key successor;
    @Mock
    private Message message;
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private ChordNode chordNode;
    @Mock
    private ScheduledFuture stableRing;

    private NodeEnvironment nodeEnvironment;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    @Captor
    private ArgumentCaptor<Key> keyCaptor;
    @Mock
    private HashingGenerator hashingGenerator;
    @Mock
    private Key predecesor;
    @Mock
    private OverlayNodeFactory overlayNodeFactory;
    @Mock
    private SuccessorList successorList;
    @Mock
    private ChordNodeFactory chordNodeFactory;
    @Mock
    private KeyFactory keyFactory;
    @Mock
    private SequenceGenerator sequenceGenerator;

    @Before
    public void before() {
        when(key.getValue()).thenReturn("key");
        when(chordNode.getKey()).thenReturn(key);

        nodeEnvironment = spy(new NodeEnvironment(communicationManager, chordNode, keyFactory, sequenceGenerator));
    }

    @Test
    public void update_processFalse_doNothing() {
        nodeEnvironment.setProcess(false);

        nodeEnvironment.process(message);

        verify(nodeEnvironment).setProcess(false);
        verify(nodeEnvironment).process(message);
        verifyNoMoreInteractions(nodeEnvironment);
    }

    @Test
    public void process_LOOKUPMessageAndMessageFromMySelf_response() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.LOOKUP);
        when(message.getParam(Protocol.LookupParams.TYPE.name())).thenReturn("type");
        when(message.isMessageFromMySelf()).thenReturn(true);

        Message response = nodeEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.LOOKUP_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(response.getParam(Protocol.LookupParams.TYPE.name())).isEqualTo("type");
        assertThat(response.getParam(Protocol.LookupResponseParams.NODE_FIND.name())).isEqualTo("key");
    }

    @Test
    public void process_LOOKUPMessageAndNotFindSuccessor_response() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.LOOKUP);
        when(message.getParam(Protocol.LookupParams.TYPE.name())).thenReturn("LOOKUP");
        when(message.getParam(Protocol.LookupParams.HASHING.name())).thenReturn("6887");
        when(chordNode.findSuccessor(successor, LookupType.LOOKUP)).thenReturn(null);
        when(keyFactory.newKey(new BigInteger("6887"))).thenReturn(successor);

        Message response = nodeEnvironment.process(message);

        verify(chordNode).findSuccessor(successor, LookupType.LOOKUP);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.LOOKUP_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(response.getParam(Protocol.LookupParams.TYPE.name())).isEqualTo("LOOKUP");
        assertThat(response.getParam(Protocol.LookupResponseParams.NODE_FIND.name())).isEqualTo("key");
    }

    @Test
    public void process_LOOKUPMessageAndFindSuccessor_response() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.LOOKUP);
        when(message.getParam(Protocol.LookupParams.TYPE.name())).thenReturn("LOOKUP");
        when(message.getParam(Protocol.LookupParams.HASHING.name())).thenReturn("6887");
        when(keyFactory.newKey(new BigInteger("6887"))).thenReturn(successor);
        when(chordNode.findSuccessor(successor, LookupType.LOOKUP)).thenReturn(found);

        Message response = nodeEnvironment.process(message);

        verify(chordNode).findSuccessor(successor, LookupType.LOOKUP);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.LOOKUP_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("key");
        assertThat(response.getParam(Protocol.LookupParams.TYPE.name())).isEqualTo("LOOKUP");
        assertThat(response.getParam(Protocol.LookupResponseParams.NODE_FIND.name())).isEqualTo("found");
    }

    @Test
    public void process_PINGMessage_responsePing() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.PING);

        Message response = nodeEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.PING_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("key");
        assertThat(response.getParam(Protocol.PingResponseParams.PING.name())).isEqualTo("true");
    }

    @Test
    public void process_NOTIFYMessage_chordNodeNotified() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.NOTIFY);
        when(keyFactory.newKey("source")).thenReturn(key);
        doNothing().when(chordNode).notify(key);

        Message response = nodeEnvironment.process(message);

        verify(chordNode).notify(key);

        assertThat(response).isNull();
    }

    @Test
    public void process_GET_PREDECESSORMessageAndPredecesorNull_sendPredecesorNull() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.GET_PREDECESSOR);
        when(chordNode.getPredecessor()).thenReturn(null);

        Message response = nodeEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("key");
        assertThat(response.getParam(Protocol.GetPredecessorResponseParams.PREDECESSOR.name())).isNull();
    }

    @Test
    public void process_GET_PREDECESSORMessageAndPredecesorNotNull_sendPredecesor() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.GET_PREDECESSOR);
        when(chordNode.getPredecessor()).thenReturn(predecesor);

        Message response = nodeEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("key");
        assertThat(response.getParam(Protocol.GetPredecessorResponseParams.PREDECESSOR.name())).isEqualTo("predecesor");
    }

    @Test
    public void process_BOOTSTRAPMessageAndSourceEqualKey_doNothing() {
        when(message.getAddress()).thenReturn(Address.builder().source("key").build());
        when(message.getMessageType()).thenReturn(Protocol.BOOTSTRAP);

        nodeEnvironment.process(message);

        verifyZeroInteractions(communicationManager);
    }

    @Test
    public void process_BOOTSTRAPMessageAndSourceNotEqualKey_responseBoostrap() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.BOOTSTRAP);

        Message response = nodeEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.BOOTSTRAP_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("key");
        assertThat(response.getParam(Protocol.BootStrapResponseParams.NODE_FIND.name())).isEqualTo("key");
    }

    @Test
    public void process_SET_PREDECESSORMessage_changePredecesor() {
        when(message.getMessageType()).thenReturn(Protocol.SET_PREDECESSOR);
        when(message.getParam(Protocol.SetPredecessorParams.PREDECESSOR.name())).thenReturn("predecesor");
        when(keyFactory.newKey("predecesor")).thenReturn(predecesor);

        nodeEnvironment.process(message);

        verify(chordNode).setPredecessor(predecesor);
    }

    @Test
    public void process_SET_SUCCESSORMessage_changeSuccessor() {
        when(message.getMessageType()).thenReturn(Protocol.SET_SUCCESSOR);
        when(message.getParam(Protocol.SetSuccessorParams.SUCCESSOR.name())).thenReturn("successor");
        when(keyFactory.newKey("successor")).thenReturn(successor);

        nodeEnvironment.process(message);

        verify(chordNode).setSuccessor(successor);
    }

    @Test
    public void process_GET_SUCCESSOR_LISTMessageAndSourceNotEqualKey_responseBoostrap() {
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(message.getMessageType()).thenReturn(Protocol.GET_SUCCESSOR_LIST);
        when(successorList.toString()).thenReturn("successorList");
        when(chordNode.getSuccessorList()).thenReturn(successorList);

        Message response = nodeEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.GET_SUCCESSOR_LIST_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("key");
        assertThat(response.getParam(Protocol.GetSuccessorListResponseParams.SUCCESSOR_LIST.name())).isEqualTo("successorList");
    }
}