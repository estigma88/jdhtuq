package co.edu.uniquindio.dht;


import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.network.HostNameUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainTest {
    @Mock
    private StorageNodeFactory storageNodeFactory;
    @Mock
    private StorageNode storageNode;
    @InjectMocks
    private Main main;

    @Test
    public void storageNode_nullNodeName_useNetworkUtility() throws StorageException, UnknownHostException {
        String nodeName = HostNameUtil.getLocalHostAddress();

        when(storageNodeFactory.createNode(nodeName)).thenReturn(storageNode);

        StorageNode result = main.storageNode(storageNodeFactory, null);

        assertThat(result).isEqualTo(storageNode);
    }

    @Test
    public void storageNode_nodeName_useNetworkUtility() throws StorageException, UnknownHostException {
        when(storageNodeFactory.createNode("nodeName")).thenReturn(storageNode);

        StorageNode result = main.storageNode(storageNodeFactory, "nodeName");

        assertThat(result).isEqualTo(storageNode);
    }

}
