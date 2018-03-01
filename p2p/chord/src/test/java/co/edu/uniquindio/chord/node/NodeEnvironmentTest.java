package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.overlay.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigInteger;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({HashingGenerator.class, OverlayNodeFactory.class})
@RunWith(PowerMockRunner.class)
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
    private StableRing stableRing;

    private NodeEnvironment nodeEnvironment;
    @Captor
    private ArgumentCaptor<MessageXML> messageCaptor;
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

    @Before
    public void before() {
        when(key.getValue()).thenReturn("key");
        when(chordNode.getKey()).thenReturn(key);

        nodeEnvironment = spy(new NodeEnvironment(communicationManager, chordNode, stableRing));
    }

    @Test
    public void update_processFalse_doNothing() {
        nodeEnvironment.setProcess(false);

        nodeEnvironment.update(message);

        verify(nodeEnvironment).setProcess(false);
        verify(nodeEnvironment).update(message);
        verifyNoMoreInteractions(nodeEnvironment);
    }

    @Test
    public void update_LOOKUPMessageAndMessageFromMySelf_response() {
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.LOOKUP.getName());
        when(message.getParam(Protocol.LookupParams.TYPE.name())).thenReturn("type");
        when(message.isMessageFromMySelf()).thenReturn(true);

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.TYPE.name())).isEqualTo("type");
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupResponseParams.NODE_FIND.name())).isEqualTo("key");
    }

    @Test
    public void update_LOOKUPMessageAndNotFindSuccessor_response() {
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.LOOKUP.getName());
        when(message.getParam(Protocol.LookupParams.TYPE.name())).thenReturn("LOOKUP");
        when(message.getParam(Protocol.LookupParams.HASHING.name())).thenReturn("6887");
        when(chordNode.findSuccessor(any(), eq(LookupType.LOOKUP))).thenReturn(null);

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());
        verify(chordNode).findSuccessor(keyCaptor.capture(), eq(LookupType.LOOKUP));

        assertThat(keyCaptor.getValue().getHashing()).isEqualTo(new BigInteger("6887"));
        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo(key.getValue());
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.TYPE.name())).isEqualTo("LOOKUP");
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupResponseParams.NODE_FIND.name())).isEqualTo("key");
    }

    @Test
    public void update_LOOKUPMessageAndFindSuccessor_response() {
        when(found.getValue()).thenReturn("found");
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.LOOKUP.getName());
        when(message.getParam(Protocol.LookupParams.TYPE.name())).thenReturn("LOOKUP");
        when(message.getParam(Protocol.LookupParams.HASHING.name())).thenReturn("6887");
        when(chordNode.findSuccessor(any(), eq(LookupType.LOOKUP))).thenReturn(found);

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());
        verify(chordNode).findSuccessor(keyCaptor.capture(), eq(LookupType.LOOKUP));

        assertThat(keyCaptor.getValue().getHashing()).isEqualTo(new BigInteger("6887"));
        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.LOOKUP_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupParams.TYPE.name())).isEqualTo("LOOKUP");
        assertThat(messageCaptor.getValue().getParam(Protocol.LookupResponseParams.NODE_FIND.name())).isEqualTo("found");
    }

    @Test
    public void update_PINGMessage_responsePing() {
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.PING.getName());

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.PING_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getValue().getParam(Protocol.PingResponseParams.PING.name())).isEqualTo("true");
    }

    @Test
    public void update_NOTIFYMessage_chordNodeNotified() {
        mockStatic(HashingGenerator.class);
        when(HashingGenerator.getInstance()).thenReturn(hashingGenerator);

        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.NOTIFY.getName());

        nodeEnvironment.update(message);

        verify(chordNode).notify(keyCaptor.capture());

        assertThat(keyCaptor.getValue().getValue()).isEqualTo("source");
    }

    @Test
    public void update_GET_PREDECESSORMessageAndPredecesorNull_sendPredecesorNull() {
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.GET_PREDECESSOR.getName());
        when(chordNode.getPredecessor()).thenReturn(null);

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getValue().getParam(Protocol.GetPredecessorResponseParams.PREDECESSOR.name())).isNull();
    }

    @Test
    public void update_GET_PREDECESSORMessageAndPredecesorNotNull_sendPredecesor() {
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.GET_PREDECESSOR.getName());
        when(predecesor.getValue()).thenReturn("predecesor");
        when(chordNode.getPredecessor()).thenReturn(predecesor);

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.GET_PREDECESSOR_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getValue().getParam(Protocol.GetPredecessorResponseParams.PREDECESSOR.name())).isEqualTo("predecesor");
    }

    @Test
    public void update_BOOTSTRAPMessageAndSourceEqualKey_doNothing() {
        when(message.getMessageSource()).thenReturn("key");
        when(message.getType()).thenReturn(Protocol.BOOTSTRAP.getName());

        nodeEnvironment.update(message);

        verifyZeroInteractions(communicationManager);
    }

    @Test
    public void update_BOOTSTRAPMessageAndSourceNotEqualKey_responseBoostrap() {
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.BOOTSTRAP.getName());

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.BOOTSTRAP_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getValue().getParam(Protocol.BootStrapResponseParams.NODE_FIND.name())).isEqualTo("key");
    }

    @Test
    public void update_LEAVEMessageAndSuccessorEqualsKey_onlyDestroyNode() throws OverlayException {
        mockStatic(OverlayNodeFactory.class);
        when(OverlayNodeFactory.getInstance()).thenReturn(overlayNodeFactory);
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.LEAVE.getName());
        when(chordNode.getSuccessor()).thenReturn(key);
        when(chordNode.getKey()).thenReturn(key);

        nodeEnvironment.update(message);

        verify(stableRing).setRun(false);
        verify(overlayNodeFactory).destroyNode("key");
        verifyZeroInteractions(communicationManager);
    }

    @Test
    public void update_LEAVEMessageAndSuccessorNotEqualsKey_nnotifyAndDestroyNode() throws OverlayException {
        mockStatic(OverlayNodeFactory.class);
        when(OverlayNodeFactory.getInstance()).thenReturn(overlayNodeFactory);
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.LEAVE.getName());
        when(successor.getValue()).thenReturn("successor");
        when(chordNode.getSuccessor()).thenReturn(successor);
        when(predecesor.getValue()).thenReturn("predecesor");
        when(chordNode.getPredecessor()).thenReturn(predecesor);
        when(chordNode.getKey()).thenReturn(key);

        nodeEnvironment.update(message);

        verify(stableRing).setRun(false);
        verify(overlayNodeFactory).destroyNode("key");

        verify(communicationManager, times(2)).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.SET_SUCCESSOR);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("predecesor");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.SetSuccessorParams.SUCCESSOR.name())).isEqualTo("successor");

        assertThat(messageCaptor.getAllValues().get(1).getSendType()).isEqualTo(Message.SendType.REQUEST);
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.SET_PREDECESSOR);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("successor");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(1).getParam(Protocol.SetPredecessorParams.PREDECESSOR.name())).isEqualTo("predecesor");
    }

    @Test
    public void update_SET_PREDECESSORMessage_changePredecesor() {
        mockStatic(HashingGenerator.class);
        when(HashingGenerator.getInstance()).thenReturn(hashingGenerator);

        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.SET_PREDECESSOR.getName());
        when(message.getParam(Protocol.SetPredecessorParams.PREDECESSOR.name())).thenReturn("predecesor");

        nodeEnvironment.update(message);

        verify(chordNode).setPredecessor(keyCaptor.capture());

        assertThat(keyCaptor.getValue().getValue()).isEqualTo("predecesor");
    }

    @Test
    public void update_SET_SUCCESSORMessage_changeSuccessor() {
        mockStatic(HashingGenerator.class);
        when(HashingGenerator.getInstance()).thenReturn(hashingGenerator);

        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.SET_SUCCESSOR.getName());
        when(message.getParam(Protocol.SetSuccessorParams.SUCCESSOR.name())).thenReturn("successor");

        nodeEnvironment.update(message);

        verify(chordNode).setSuccessor(keyCaptor.capture());

        assertThat(keyCaptor.getValue().getValue()).isEqualTo("successor");
    }

    @Test
    public void update_GET_SUCCESSOR_LISTMessageAndSourceNotEqualKey_responseBoostrap() {
        when(message.getMessageSource()).thenReturn("source");
        when(message.getType()).thenReturn(Protocol.GET_SUCCESSOR_LIST.getName());
        when(successorList.toString()).thenReturn("successorList");
        when(chordNode.getSuccessorList()).thenReturn(successorList);

        nodeEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getValue().getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.GET_SUCCESSOR_LIST_RESPONSE);
        assertThat(messageCaptor.getValue().getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("key");
        assertThat(messageCaptor.getValue().getParam(Protocol.GetSuccessorListResponseParams.SUCCESSOR_LIST.name())).isEqualTo("successorList");
    }
}