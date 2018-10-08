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
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.message.MessageStream;
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

import java.io.ByteArrayInputStream;
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
    private IdGenerator idGenerator;
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

        dHashNode = spy(new DHashNode(overlayNode, 3, "dhash", communicationManager, serializationHandler, resourceManager, keyFactory, idGenerator, executorService));
    }

    @Test
    public void get_keyNotFound_exception() throws StorageException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Impossible to do get to resource, lookup fails");

        dHashNode.getSync("resourceKey", (name, current, size) -> {});
    }

    @Test
    public void get_nodeNotHaveResource_null() throws StorageException {
        Message getMessage = Message.builder()
                .id("id")
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.CONTAIN)
                .address(Address.builder()
                        .destination("key")
                        .source("dhash")
                        .build())
                .param(Protocol.ContainParams.RESOURCE_KEY.name(), "resourceKey")
                .build();

        when(idGenerator.newId()).thenReturn("id");
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(communicationManager.send(getMessage, Boolean.class)).thenReturn(false);

        Resource result = dHashNode.getSync("resourceKey", (name, current, size) -> {
        });

        assertThat(result).isNull();

        verify(communicationManager).send(getMessage, Boolean.class);
        verify(communicationManager, times(0)).receive(any(), any());
    }

    @Test
    public void get_nodeHaveResource_resource() throws StorageException, IOException, ClassNotFoundException {
        Message getMessage = Message.builder()
                .id("id")
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.CONTAIN)
                .address(Address.builder()
                        .destination("key")
                        .source("dhash")
                        .build())
                .param(Protocol.ContainParams.RESOURCE_KEY.name(), "resourceKey")
                .build();

        Message resourceTransferMessage = Message.builder()
                .sendType(Message.SendType.REQUEST)
                .id("id")
                .messageType(Protocol.GET)
                .address(Address.builder()
                        .destination("key")
                        .source("dhash")
                        .build())
                .param(Protocol.GetParams.RESOURCE_KEY.name(), "resourceKey")
                .build();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[8]);
        MessageStream messageStream = MessageStream.builder()
                .message(Message.builder()
                        .param(Protocol.GetResponseParams.RESOURCE.name(), "resource")
                        .param(Protocol.GetResponseParams.TRANSFER_VALID.name(), "true")
                        .build())
                .inputStream(inputStream)
                .build();

        when(idGenerator.newId()).thenReturn("id");
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(communicationManager.send(getMessage, Boolean.class)).thenReturn(true);
        when(communicationManager.receive(eq(resourceTransferMessage), any())).thenReturn(messageStream);
        when(serializationHandler.decode("resource", inputStream)).thenReturn(resource1);

        Resource result = dHashNode.getSync("resourceKey", progressStatus);

        assertThat(result).isEqualTo(resource1);
    }

    @Test
    public void get_nodeHaveResourceErrorTransfering_exception() throws StorageException, IOException, ClassNotFoundException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("file access failed");

        Message getMessage = Message.builder()
                .id("id")
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.CONTAIN)
                .address(Address.builder()
                        .destination("key")
                        .source("dhash")
                        .build())
                .param(Protocol.ContainParams.RESOURCE_KEY.name(), "resourceKey")
                .build();

        Message resourceTransferMessage = Message.builder()
                .sendType(Message.SendType.REQUEST)
                .id("id")
                .messageType(Protocol.GET)
                .address(Address.builder()
                        .destination("key")
                        .source("dhash")
                        .build())
                .param(Protocol.GetParams.RESOURCE_KEY.name(), "resourceKey")
                .build();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[8]);
        MessageStream messageStream = MessageStream.builder()
                .message(Message.builder()
                        .param(Protocol.GetResponseParams.MESSAGE.name(), "file access failed")
                        .param(Protocol.GetResponseParams.TRANSFER_VALID.name(), "false")
                        .build())
                .inputStream(inputStream)
                .build();

        when(idGenerator.newId()).thenReturn("id");
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(communicationManager.send(getMessage, Boolean.class)).thenReturn(true);
        when(communicationManager.receive(eq(resourceTransferMessage), any())).thenReturn(messageStream);

        dHashNode.getSync("resourceKey", progressStatus);
    }

    @Test
    public void put_keyNotFound_exception() throws StorageException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Impossible to do put the resource: resourceKey in this moment");

        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(resource1.getId()).thenReturn("resourceKey");
        when(overlayNode.lookUp(key1)).thenReturn(null);

        dHashNode.putSync(resource1, (name, current, size) -> {});
    }

    @Test
    public void put_send_resource() throws StorageException, IOException, ClassNotFoundException {
        Message putMessage = Message.builder()
                .id("id")
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.PUT)
                .address(Address.builder()
                        .destination("key")
                        .source("dhash")
                        .build())
                .param(Protocol.PutParams.RESOURCE.name(), "resource")
                .param(Protocol.PutParams.REPLICATE.name(), "true")
                .build();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[8]);
        MessageStream messageStream = MessageStream.builder()
                .message(putMessage)
                .inputStream(inputStream)
                .size(8L)
                .build();

        Message putResponse = Message.builder()
                .param(Protocol.PutResponseParams.TRANSFER_VALID.name(), "true")
                .build();

        when(idGenerator.newId()).thenReturn("id");
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(resource1.getId()).thenReturn("resourceKey");
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(communicationManager.send(eq(messageStream), any())).thenReturn(putResponse);
        when(serializationHandler.encode(resource1)).thenReturn("resource");
        when(resource1.getInputStream()).thenReturn(inputStream);
        when(resource1.getSize()).thenReturn(8L);

        dHashNode.putSync(resource1, progressStatus);

        verify(communicationManager).send(eq(messageStream), any());
    }

    @Test
    public void put_sendTransferError_exception() throws StorageException, IOException, ClassNotFoundException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("file access failed");

        Message putMessage = Message.builder()
                .id("id")
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.PUT)
                .address(Address.builder()
                        .destination("key")
                        .source("dhash")
                        .build())
                .param(Protocol.PutParams.RESOURCE.name(), "resource")
                .param(Protocol.PutParams.REPLICATE.name(), "true")
                .build();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[8]);
        MessageStream messageStream = MessageStream.builder()
                .message(putMessage)
                .inputStream(inputStream)
                .size(8L)
                .build();

        Message putResponse = Message.builder()
                .param(Protocol.PutResponseParams.MESSAGE.name(), "file access failed")
                .param(Protocol.PutResponseParams.TRANSFER_VALID.name(), "false")
                .build();

        when(idGenerator.newId()).thenReturn("id");
        when(keyFactory.newKey("resourceKey")).thenReturn(key1);
        when(resource1.getId()).thenReturn("resourceKey");
        when(overlayNode.lookUp(key1)).thenReturn(key);
        when(communicationManager.send(eq(messageStream), any())).thenReturn(putResponse);
        when(serializationHandler.encode(resource1)).thenReturn("resource");
        when(resource1.getInputStream()).thenReturn(inputStream);
        when(resource1.getSize()).thenReturn(8L);

        dHashNode.putSync(resource1, progressStatus);
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
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), eq(progressStatus));

        dHashNode.relocateAllResources(relocateKey, progressStatus);

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
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), eq(progressStatus));

        dHashNode.leave(progressStatus);

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource2, key1, false, progressStatus);
        verify(dHashNode).put(resource3, key1, false, progressStatus);
        verify(resourceManager).deleteAll();
        verify(communicationManager).removeObserver("key");
    }

    @Test
    public void replicateData_neighborsListEqualReplicationFactor_replicateAll() throws OverlayException, StorageException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{key1, key2, key3});
        when(resourceManager.find(resource1.getId())).thenReturn(resource1);
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), eq(progressStatus));

        dHashNode.replicateData(resource1.getId(), progressStatus);

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource1, key2, false, progressStatus);
        verify(dHashNode).put(resource1, key3, false, progressStatus);
    }

    @Test
    public void replicateData_neighborsListLessThanReplicationFactor_replicate2() throws OverlayException, StorageException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{key1, key2});
        when(resourceManager.find(resource1.getId())).thenReturn(resource1);
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), eq(progressStatus));

        dHashNode.replicateData(resource1.getId(), progressStatus);

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource1, key2, false, progressStatus);
    }

    @Test
    public void replicateData_neighborsListGreaterThanReplicationFactor_replicate3() throws OverlayException, StorageException {
        when(overlayNode.getNeighborsList()).thenReturn(new Key[]{key1, key2, key3, key});
        when(resourceManager.find(resource1.getId())).thenReturn(resource1);
        doReturn(true).when(dHashNode).put(any(), any(), anyBoolean(), eq(progressStatus));

        dHashNode.replicateData(resource1.getId(), progressStatus);

        verify(dHashNode).put(resource1, key1, false, progressStatus);
        verify(dHashNode).put(resource1, key2, false, progressStatus);
        verify(dHashNode).put(resource1, key3, false, progressStatus);
        verify(dHashNode, times(0)).put(resource1, key, false, progressStatus);
    }
}