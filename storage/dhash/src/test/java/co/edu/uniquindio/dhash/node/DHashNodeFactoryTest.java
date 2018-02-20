package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.hashing.Key;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DHashNodeFactoryTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private OverlayNodeFactory overlayNodeFactory;
    @Mock
    private OverlayNode overlayNode;
    @Mock
    private DHashNode dhashNode;
    @Mock
    private DHashEnvironment dHashEnviroment;
    @Mock
    private Key key;
    @Mock
    private InetAddress inetAddress;
    @InjectMocks
    @Spy
    private DHashNodeFactory dHashNodeFactory;

    @Test
    public void create_byName_nodeCreated() throws OverlayException, DHashFactoryException {
        when(overlayNodeFactory.createNode("node")).thenReturn(overlayNode);
        doReturn(dhashNode).when(dHashNodeFactory).getDhashNode("node", overlayNode, persistenceManager);
        doReturn(dHashEnviroment).when(dHashNodeFactory).getdHashEnviroment(dhashNode, persistenceManager);

        StorageNode node = dHashNodeFactory.createNode("node");

        assertThat(node).isEqualTo(dhashNode);

        verify(communicationManager).addObserver(dHashEnviroment);
    }

    @Test
    public void create_byDefault_nodeCreated() throws OverlayException, DHashFactoryException {
        when(overlayNodeFactory.createNode()).thenReturn(overlayNode);
        when(overlayNode.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("node");
        doReturn(dhashNode).when(dHashNodeFactory).getDhashNode("node", overlayNode, persistenceManager);
        doReturn(dHashEnviroment).when(dHashNodeFactory).getdHashEnviroment(dhashNode, persistenceManager);

        StorageNode node = dHashNodeFactory.createNode();

        assertThat(node).isEqualTo(dhashNode);

        verify(communicationManager).addObserver(dHashEnviroment);
    }

    @Test
    public void create_byInitAddress_nodeCreated() throws OverlayException, DHashFactoryException {
        when(overlayNodeFactory.createNode(inetAddress)).thenReturn(overlayNode);
        when(overlayNode.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("node");
        doReturn(dhashNode).when(dHashNodeFactory).getDhashNode("node", overlayNode, persistenceManager);
        doReturn(dHashEnviroment).when(dHashNodeFactory).getdHashEnviroment(dhashNode, persistenceManager);

        StorageNode node = dHashNodeFactory.createNode(inetAddress);

        assertThat(node).isEqualTo(dhashNode);

        verify(communicationManager).addObserver(dHashEnviroment);
    }

    @Test
    public void destroyNode_byName_nodeDestroyed() throws OverlayException, DHashFactoryException {
        dHashNodeFactory.destroyNode("node");

        verify(communicationManager).removeObserver("node");
    }

}