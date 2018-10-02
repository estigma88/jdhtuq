/*
 *  DHash project implement a storage management
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.ResourceNotFoundException;
import co.edu.uniquindio.dhash.resource.checksum.ChecksumCalculator;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.dhash.resource.serialization.SerializationHandler;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.overlay.OverlayNode;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DHashNodeTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private OverlayNode overlayNode;
    @Mock
    private SerializationHandler serializationHandler;
    @Mock
    private ChecksumCalculator checksumeCalculator;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private KeyFactory keyFactory;
    @Mock
    private IdGenerator sequenceGenerator;
    @Mock
    private Key key;
    @Mock
    private Key key1;
    @Mock
    private Key key2;
    @Mock
    private Key key3;
    @Mock
    private Message bigMessage;
    @Mock
    private Resource resource1;
    @Mock
    private Resource resource2;
    @Mock
    private Resource resource3;
    @Mock
    private StorageNodeFactory dhashNodeFactory;
    @Mock
    private ProgressStatus progressStatus;
    @Mock
    private ExecutorService executorService;
    @Captor
    private ArgumentCaptor<Key> keyCaptor;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    @Captor
    private ArgumentCaptor<Message> bigMessageCaptor;
    private DHashNode dHashNode;

    @Before
    public void before() throws IOException, ClassNotFoundException {
        when(key.getValue()).thenReturn("key");

        dHashNode = spy(new DHashNode(overlayNode, 3, "dhash", communicationManager, serializationHandler, resourceManager, keyFactory, sequenceGenerator, executorService));
    }

    @Test
    public void get_keyNotFound_exception() throws StorageException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Imposible to do get to resource, lookup fails");

        dHashNode.getSync("resourceKey", (name, current, size) -> {});
    }

    @Test
    public void get_nodeNotHaveResource_exception() throws StorageException {
        thrown.expect(ResourceNotFoundException.class);
        thrown.expectMessage("Resource 'resourceKey' not found");

        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(communicationManager.send(any(), eq(Boolean.class))).thenReturn(false);

        dHashNode.getSync("resourceKey", (name, current, size) -> {});
    }

    @Test
    public void get_nodeHaveResource_exception() throws StorageException, IOException, ClassNotFoundException {
        when(serializationHandler.decode("", null)).thenReturn(resource1);
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(overlayNode.lookUp(key1)).thenReturn(key);
        //when(bigMessage.getData(Protocol.GetResponseData.RESOURCE.name())).thenReturn(new byte[10]);
        when(communicationManager.send(any(), eq(Boolean.class))).thenReturn(true);
        when(communicationManager.send(any(), eq(Message.class))).thenReturn(bigMessage);

        Resource resourceResult = dHashNode.getSync("resourceKey", (name, current, size) -> {});

        verify(overlayNode).lookUp(key1);
        verify(communicationManager).send(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).send(messageCaptor.capture(), eq(Message.class));

        assertThat(resourceResult).isEqualTo(resource1);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.CONTAIN);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.ContainParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
        assertThat(messageCaptor.getAllValues().get(1).getMessageType()).isEqualTo(Protocol.GET);
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getDestination()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(1).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(1).getParam(Protocol.ContainParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
    }

    @Test
    public void put_keyNotFound_exception() throws StorageException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Imposible to do put to resource: resourceKey in this moment");

        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(resource1.getId()).thenReturn("resourceKey");
        when(overlayNode.lookUp(key1)).thenReturn(null);

        dHashNode.putSync(resource1, (name, current, size) -> {});

        verify(overlayNode).lookUp(key1);
    }

    @Test
    public void put_existResource_exception() throws StorageException {
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(resource1.getId()).thenReturn("resourceKey");
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(checksumeCalculator.calculate(resource1)).thenReturn("checksum");
        when(communicationManager.send(any(), eq(Boolean.class))).thenReturn(true);

        boolean result = dHashNode.putSync(resource1, (name, current, size) -> {});

        verify(communicationManager).send(messageCaptor.capture(), eq(Boolean.class));
        verify(overlayNode).lookUp(key1);

        assertThat(result).isFalse();
        //assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        //assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.ResourceCompareParams.CHECK_SUM.name())).isEqualTo("checksum");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.PutParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");

    }

    @Test
    public void put_send_resource() throws StorageException, IOException, ClassNotFoundException {
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(resource1.getId()).thenReturn("resourceKey");
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(checksumeCalculator.calculate(resource1)).thenReturn("checksum");
        when(communicationManager.send(any(), eq(Boolean.class))).thenReturn(false);

        dHashNode.putSync(resource1, (name, current, size) -> {});

        verify(overlayNode).lookUp(key1);
        verify(communicationManager).send(messageCaptor.capture(),
                eq(Boolean.class));
        verify(communicationManager).send(bigMessageCaptor.capture());

        //assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        //assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.ResourceCompareParams.CHECK_SUM.name())).isEqualTo("checksum");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.PutParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
        assertThat(bigMessageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.PUT);
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("key");
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(bigMessageCaptor.getAllValues().get(0).getParam(Protocol.PutParams.RESOURCE_KEY.name())).isEqualTo("resourceKey");
        assertThat(bigMessageCaptor.getAllValues().get(0).getParam(Protocol.PutParams.REPLICATE.name())).isEqualTo("true");
    }

    @Test
    public void relocateAllResources_put_relocate2() throws StorageException {
        Set<String> resourcesNames = new HashSet<>();
        resourcesNames.add("resource1");
        resourcesNames.add("resource2");
        resourcesNames.add("resource3");

        Key relocateKey = mock(Key.class);

        when(resourceManager.getAllKeys()).thenReturn(resourcesNames);
        when(resourceManager.find("resource1")).thenReturn(resource1);
        when(resourceManager.find("resource2")).thenReturn(resource2);
        when(resourceManager.find("resource3")).thenReturn(resource3);
        when(overlayNode.getKey()).thenReturn(key);
        doReturn(key1).when(dHashNode).getFileKey("resource1");
        doReturn(key2).when(dHashNode).getFileKey("resource2");
        doReturn(key3).when(dHashNode).getFileKey("resource3");
        when(key1.isBetween(relocateKey, key)).thenReturn(false);
        when(key2.isBetween(relocateKey, key)).thenReturn(true);
        when(key3.isBetween(relocateKey, key)).thenReturn(false);
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), progressStatus);

        dHashNode.relocateAllResources(relocateKey, (name, current, size) -> {});

        verify(dHashNode).put(resource1, relocateKey, false, progressStatus);
        verify(dHashNode).put(resource3, relocateKey, false, progressStatus);
        verify(dHashNode, times(0)).put(resource2, relocateKey, false, progressStatus);
    }

    @Test
    public void leave_keyEqualsSuccessor_notRelocate() throws StorageException, OverlayException {
        Set<String> resourcesNames = new HashSet<>();
        resourcesNames.add("resource1");
        resourcesNames.add("resource2");
        resourcesNames.add("resource3");

        when(overlayNode.leave()).thenReturn(new Key[]{key, key1, key2});
        when(overlayNode.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("key");
        when(resourceManager.getAllKeys()).thenReturn(resourcesNames);

        dHashNode.leave((name, current, size) -> {});

        verify(resourceManager).deleteAll();
        verify(communicationManager).removeObserver("key");
    }

    @Test
    public void leave_put_relocate2() throws StorageException, OverlayException {
        Set<String> resourcesNames = new HashSet<>();
        resourcesNames.add("resource1");
        resourcesNames.add("resource2");
        resourcesNames.add("resource3");

        when(overlayNode.leave()).thenReturn(new Key[]{key1, key2, key3});
        when(overlayNode.getKey()).thenReturn(key);
        when(key.getValue()).thenReturn("key");
        when(resourceManager.getAllKeys()).thenReturn(resourcesNames);
        when(resourceManager.find("resource1")).thenReturn(resource1);
        when(resourceManager.find("resource2")).thenReturn(resource2);
        when(resourceManager.find("resource3")).thenReturn(resource3);
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), progressStatus);

        dHashNode.leave((name, current, size) -> {});

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource2, key1, false, progressStatus);
        verify(dHashNode).put(resource3, key1, false, progressStatus);
        verify(resourceManager).deleteAll();
        verify(communicationManager).removeObserver("key");
    }

    @Test
    public void replicateData_neighborsListEqualReplicationFactor_replicateAll() throws OverlayException, StorageException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{key1, key2, key3});
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), progressStatus);

        dHashNode.replicateData(resource1.getId(), (name, current, size) -> {});

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource1, key2, false, progressStatus);
        verify(dHashNode).put(resource1, key3, false, progressStatus);
    }

    @Test
    public void replicateData_neighborsListLessThanReplicationFactor_replicate2() throws OverlayException, StorageException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{key1, key2});
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), progressStatus);

        dHashNode.replicateData(resource1.getId(), (name, current, size) -> {});

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource1, key2, false, progressStatus);
    }

    @Test
    public void replicateData_neighborsListGreaterThanReplicationFactor_replicate3() throws OverlayException, StorageException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{key1, key2, key3, key});
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), progressStatus);

        dHashNode.replicateData(resource1.getId(), (name, current, size) -> {});

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource1, key2, false, progressStatus);
        verify(dHashNode).put(resource1, key3, false, progressStatus);
        verify(dHashNode, times(0)).put(resource1, key, false, progressStatus);
    }
}