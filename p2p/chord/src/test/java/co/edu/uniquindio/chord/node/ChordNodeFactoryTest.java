package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.Chord;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.overlay.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({BootStrap.class})
@RunWith(PowerMockRunner.class)
public class ChordNodeFactoryTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private Chord chord;
    @Mock
    private ChordNode nodeChord;
    @Mock
    private NodeEnvironment nodeEnviroment;
    @Mock
    private Key key;
    @Mock
    private InetAddress inetAddress;
    private ChordNodeFactory chordNodeFactory;

    @Before
    public void before() {
        Set<String> names = new HashSet<>();
        names.add("0");
        names.add("1");

        chordNodeFactory = spy(new ChordNodeFactory(communicationManager, names));
    }

    @Test
    public void createNode_byCount_nodeCreated() throws ChordNodeFactoryException {
        doReturn(chord).when(chordNodeFactory).createNode("2");

        Chord node = chordNodeFactory.createNode();

        assertThat(node).isEqualTo(chord);
    }

    @Test
    public void createNode_byName_nodeCreated() throws ChordNodeFactoryException {
        mockStatic(BootStrap.class);
        when(key.getValue()).thenReturn("key");
        when(key.getStringHashing()).thenReturn("key");
        when(nodeChord.getKey()).thenReturn(key);
        doReturn(key).when(chordNodeFactory).getKey("2");
        doReturn(nodeChord).when(chordNodeFactory).getNodeChord(key);
        doReturn(nodeEnviroment).when(chordNodeFactory).getNodeEnviroment(nodeChord);

        Chord node = chordNodeFactory.createNode("2");

        assertThat(node).isEqualTo(nodeChord);

        PowerMockito.verifyStatic(BootStrap.class);
        BootStrap.boot(nodeChord, communicationManager);

        verify(communicationManager).addObserver(nodeEnviroment);
        verify(nodeEnviroment).startStableRing();
    }

    @Test
    public void createNode_byInetAddress_nodeCreated() throws ChordNodeFactoryException {
        when(inetAddress.getHostAddress()).thenReturn("host");
        doReturn(chord).when(chordNodeFactory).createNode("host");

        OverlayNode node = chordNodeFactory.createNode(inetAddress);

        assertThat(node).isEqualTo(chord);

        verify(chordNodeFactory).createNode("host");
    }

    @Test
    public void destroyNode_byName_nodeDestroyed() throws ChordNodeFactoryException {
        chordNodeFactory.destroyNode("1");

        assertThat(chordNodeFactory.getNames()).doesNotContain("1");

        verify(communicationManager).removeObserver("1");
    }
}