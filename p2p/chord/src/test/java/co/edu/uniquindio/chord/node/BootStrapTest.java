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
    private Key successor;
    @Mock
    private Key key;
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
        when(fingersTable.getFingersTable()).thenReturn(new Key[1]);
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
        when(fingersTable.getFingersTable()).thenReturn(new Key[1]);
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