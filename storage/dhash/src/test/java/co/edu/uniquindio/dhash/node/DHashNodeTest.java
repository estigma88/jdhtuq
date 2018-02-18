package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.ResourceAlreadyExistException;
import co.edu.uniquindio.dhash.resource.ResourceManager;
import co.edu.uniquindio.dhash.resource.ResourceNotFoundException;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.dhash.resource.file.SerializableResource;
import co.edu.uniquindio.utils.communication.message.BigMessage;
import co.edu.uniquindio.utils.communication.message.BigMessageXML;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.hashing.HashingGenerator;
import co.edu.uniquindio.utils.hashing.Key;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({HashingGenerator.class, SerializableResource.class, DHashNodeFactory.class})
@RunWith(PowerMockRunner.class)
public class DHashNodeTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private OverlayNode overlayNode;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private OverlayObserver overlayObserver;
    @Mock
    private HashingGenerator hashingGenerator;
    @Mock
    private SerializableResource serializableResource;
    @Mock
    private Key key;
    @Mock
    private Key fileKey1;
    @Mock
    private Key fileKey2;
    @Mock
    private Key fileKey3;
    @Mock
    private BigMessage bigMessage;
    @Mock
    private Resource resource1;
    @Mock
    private Resource resource2;
    @Mock
    private Resource resource3;
    @Mock
    private StorageNodeFactory dhashNodeFactory;
    @Captor
    private ArgumentCaptor<Key> keyCaptor;
    @Captor
    private ArgumentCaptor<MessageXML> messageCaptor;
    @Captor
    private ArgumentCaptor<BigMessageXML> bigMessageCaptor;
    private DHashNode dHashNode;

    @Before
    public void before() throws IOException, ClassNotFoundException {
        mockStatic(HashingGenerator.class);
        when(HashingGenerator.getInstance()).thenReturn(hashingGenerator);

        when(key.getValue()).thenReturn("key");
        when(key.getStringHashing()).thenReturn("key");

        dHashNode = spy(new DHashNode(communicationManager, overlayNode, resourceManager, 3, "dhash", overlayObserver));
    }

    @Test
    public void get_keyNotFound_exception() throws StorageException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Imposible to do get to resource, lookup fails");

        dHashNode.get("resourceKey");
    }

    @Test
    public void get_nodeNotHaveResource_exception() throws StorageException {
        thrown.expect(ResourceNotFoundException.class);
        thrown.expectMessage("Resource 'resourceKey' not found");

        when(overlayNode.lookUp(any())).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(Boolean.class))).thenReturn(false);

        dHashNode.get("resourceKey");
    }

    @Test
    public void get_nodeHaveResource_exception() throws StorageException, IOException, ClassNotFoundException {
        mockStatic(SerializableResource.class);
        when(SerializableResource.valueOf(new byte[10])).thenReturn(serializableResource);
        when(overlayNode.lookUp(any())).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(Boolean.class))).thenReturn(true);
        when(communicationManager.recieverBigMessage(any())).thenReturn(bigMessage);
        when(bigMessage.getData(Protocol.ResourceTransferResponseData.RESOURCE.name())).thenReturn(new byte[10]);

        Resource resourceResult = dHashNode.get("resourceKey");

        verify(overlayNode).lookUp(keyCaptor.capture());
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).recieverBigMessage(messageCaptor.capture());

        assertThat(resourceResult).isEqualTo(serializableResource);
        assertThat(keyCaptor.getValue().getValue()).isEqualTo("resourceKey");
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.GET);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.GetParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.RESOURCE_TRANSFER);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(1).getParam(Protocol.GetParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
    }

    @Test
    public void put_keyNotFound_exception() throws StorageException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Imposible to do put to resource: resourceKey in this moment");

        when(serializableResource.getKey()).thenReturn("resourceKey");
        when(hashingGenerator.generateHashing(any(), anyInt())).thenReturn(new BigInteger("465456"));

        dHashNode.put(serializableResource);
    }

    @Test
    public void put_existResource_exception() throws StorageException {
        thrown.expect(ResourceAlreadyExistException.class);
        thrown.expectMessage("Resource existe yet");

        when(overlayNode.lookUp(any())).thenReturn(key);
        when(serializableResource.getKey()).thenReturn("resourceKey");
        when(hashingGenerator.generateHashing(any(), anyInt())).thenReturn(new BigInteger("465456"));
        when(communicationManager.sendMessageUnicast(any(), eq(Boolean.class))).thenReturn(true);

        dHashNode.put(serializableResource);
    }

    @Test
    public void put_send_resource() throws StorageException, IOException, ClassNotFoundException {
        when(overlayNode.lookUp(any())).thenReturn(key);
        when(communicationManager.sendMessageUnicast(any(), eq(Boolean.class))).thenReturn(false);
        when(serializableResource.getKey()).thenReturn("resourceKey");
        when(serializableResource.getCheckSum()).thenReturn("checksum");
        when(hashingGenerator.generateHashing(any(), anyInt())).thenReturn(new BigInteger("465456"));

        dHashNode.put(serializableResource);

        verify(overlayNode).lookUp(keyCaptor.capture());
        verify(communicationManager).sendMessageUnicast(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).sendBigMessage(bigMessageCaptor.capture());

        assertThat(keyCaptor.getValue().getValue()).isEqualTo("resourceKey");
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.ResourceCompareParams.CHECK_SUM.name())).isEqualTo("checksum");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.PutParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
        assertThat(bigMessageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PUT);
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key");
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(bigMessageCaptor.getAllValues().get(0).getParam(Protocol.PutParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
        assertThat(bigMessageCaptor.getAllValues().get(0).getParam(Protocol.PutParams.REPLICATE.name())).isEqualTo("true");
    }

    @Test
    public void relocateAllResources_put_relocate2() throws ResourceAlreadyExistException {
        Set<String> resourcesNames = new HashSet<>();
        resourcesNames.add("resource1");
        resourcesNames.add("resource2");
        resourcesNames.add("resource3");

        Key relocateKey = mock(Key.class);

        when(resourceManager.getResourcesNames()).thenReturn(resourcesNames);
        when(resourceManager.get("resource1")).thenReturn(resource1);
        when(resourceManager.get("resource2")).thenReturn(resource2);
        when(resourceManager.get("resource3")).thenReturn(resource3);
        when(overlayNode.getKey()).thenReturn(key);
        doReturn(fileKey1).when(dHashNode).getFileKey("resource1");
        doReturn(fileKey2).when(dHashNode).getFileKey("resource2");
        doReturn(fileKey3).when(dHashNode).getFileKey("resource3");
        when(fileKey1.isBetween(relocateKey, key)).thenReturn(false);
        when(fileKey2.isBetween(relocateKey, key)).thenReturn(true);
        when(fileKey3.isBetween(relocateKey, key)).thenReturn(false);
        doNothing().when(dHashNode).put(any(), any(), anyBoolean());

        dHashNode.relocateAllResources(relocateKey);

        verify(dHashNode).put(resource1, relocateKey, false);
        verify(dHashNode).put(resource3, relocateKey, false);
        verify(dHashNode, times(0)).put(resource2, relocateKey, false);
    }

    @Test
    public void leave_put_relocate2() throws StorageException, OverlayException {
        mockStatic(StorageNodeFactory.class);
        when(StorageNodeFactory.getInstance()).thenReturn(dhashNodeFactory);

        Set<String> resourcesNames = new HashSet<>();
        resourcesNames.add("resource1");
        resourcesNames.add("resource2");
        resourcesNames.add("resource3");

        when(overlayNode.leave()).thenReturn(new Key[]{key, fileKey1, fileKey2});
        when(overlayNode.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("key");
        when(resourceManager.getResourcesNames()).thenReturn(resourcesNames);
        when(resourceManager.get("resource1")).thenReturn(resource1);
        when(resourceManager.get("resource2")).thenReturn(resource2);
        when(resourceManager.get("resource3")).thenReturn(resource3);
        doNothing().when(dHashNode).put(any(), any(), anyBoolean());

        dHashNode.leave();

        verify(dHashNode).put(resource1, key, false);
        verify(dHashNode).put(resource2, key, false);
        verify(dHashNode).put(resource3, key, false);
        verify(resourceManager).deleteResources();
        verify(dhashNodeFactory).destroyNode("key");
    }

    @Test
    public void replicateData_neighborsListEqualReplicationFactor_replicateAll() throws ResourceAlreadyExistException, OverlayException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{fileKey1, fileKey2, fileKey3});
        doNothing().when(dHashNode).put(any(), any(), anyBoolean());

        dHashNode.replicateData(serializableResource);

        verify(dHashNode).put(serializableResource, fileKey1, false);
        verify(dHashNode).put(serializableResource, fileKey2, false);
        verify(dHashNode).put(serializableResource, fileKey3, false);
    }

    @Test
    public void replicateData_neighborsListLessThanReplicationFactor_replicate2() throws ResourceAlreadyExistException, OverlayException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{fileKey1, fileKey2});
        doNothing().when(dHashNode).put(any(), any(), anyBoolean());

        dHashNode.replicateData(serializableResource);

        verify(dHashNode).put(serializableResource, fileKey1, false);
        verify(dHashNode).put(serializableResource, fileKey2, false);
    }

    @Test
    public void replicateData_neighborsListGreaterThanReplicationFactor_replicate3() throws ResourceAlreadyExistException, OverlayException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{fileKey1, fileKey2, fileKey3, key});
        doNothing().when(dHashNode).put(any(), any(), anyBoolean());

        dHashNode.replicateData(serializableResource);

        verify(dHashNode).put(serializableResource, fileKey1, false);
        verify(dHashNode).put(serializableResource, fileKey2, false);
        verify(dHashNode).put(serializableResource, fileKey3, false);
        verify(dHashNode, times(0)).put(serializableResource, key, false);
    }
}