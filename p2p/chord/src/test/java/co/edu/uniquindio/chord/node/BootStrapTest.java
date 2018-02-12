package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerCache;
import co.edu.uniquindio.utils.hashing.Key;
import org.assertj.core.api.Java6Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest(CommunicationManagerCache.class)
@RunWith(PowerMockRunner.class)
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
    private Key foundNode;
    @Mock
    private FingersTable fingersTable;
    @Captor
    private ArgumentCaptor<MessageXML> messageCaptor;

    @Before
    public void before() {
        mockStatic(CommunicationManagerCache.class);
        when(CommunicationManagerCache.getCommunicationManager(ChordNodeFactory.CHORD)).thenReturn(communicationManager);
    }

    @Test
    public void boot_notOtherNodeFound_createRing() {
        when(fingersTable.getFingersTable()).thenReturn(new Key[1]);
        when(nodeChord.getFingersTable()).thenReturn(fingersTable);
        when(nodeChord.getSuccessor()).thenReturn(successor);
        when(nodeChord.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("keyHash");
        when(communicationManager.sendMessageMultiCast(any(), eq(Key.class))).thenReturn(null);

        BootStrap.boot(nodeChord);

        verify(nodeChord).createRing();
        verify(communicationManager).sendMessageMultiCast(messageCaptor.capture(),
                eq(Key.class));

        assertThat(fingersTable.getFingersTable()[0]).isEqualTo(successor);
        Java6Assertions.assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.BOOTSTRAP);
        Java6Assertions.assertThat(messageCaptor.getValue().getAddress().getDestination()).isNull();
        Java6Assertions.assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("keyHash");
    }

    @Test
    public void boot_otherNodeFound_join() {
        when(fingersTable.getFingersTable()).thenReturn(new Key[1]);
        when(nodeChord.getFingersTable()).thenReturn(fingersTable);
        when(nodeChord.getSuccessor()).thenReturn(successor);
        when(nodeChord.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("keyHash");
        when(communicationManager.sendMessageMultiCast(any(), eq(Key.class))).thenReturn(foundNode);

        BootStrap.boot(nodeChord);

        verify(nodeChord).join(foundNode);
        verify(communicationManager).sendMessageMultiCast(messageCaptor.capture(),
                eq(Key.class));

        assertThat(fingersTable.getFingersTable()[0]).isEqualTo(successor);
        Java6Assertions.assertThat(messageCaptor.getValue().getMessageType()).isEqualTo(Protocol.BOOTSTRAP);
        Java6Assertions.assertThat(messageCaptor.getValue().getAddress().getDestination()).isNull();
        Java6Assertions.assertThat(messageCaptor.getValue().getAddress().getSource()).isEqualTo("keyHash");
    }

}