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
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DHashEnvironmentTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private DHashNode dHashNode;
    @Mock
    private SerializationHandler serializationHandler;
    @Mock
    private ChecksumCalculator checksumeCalculator;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private Message bigMessage;
    @Mock
    private Message message;
    @Mock
    private Resource serializableResource;
    @InjectMocks
    private DHashEnvironment dHashEnvironment;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    @Captor
    private ArgumentCaptor<Message> bigMessageCaptor;

    @Test
    public void put_bigMessageNotReplicate_save() throws OverlayException, StorageException {
        when(bigMessage.getMessageType()).thenReturn(Protocol.PUT);
        //when(bigMessage.getData(Protocol.PutDatas.RESOURCE.name())).thenReturn(new byte[]{1, 2, 3, 5});
        when(bigMessage.getParam(Protocol.PutParams.REPLICATE.name())).thenReturn("false");
        when(serializationHandler.decode("", null)).thenReturn(serializableResource);

        dHashEnvironment.process(bigMessage);

        verify(dHashNode, times(0)).replicateData(any(), (name, current, size) -> {});
        verify(resourceManager).save(serializableResource);
    }

    @Test
    public void put_bigMessageReplicate_replicate() throws OverlayException, IOException, ClassNotFoundException, StorageException {
        when(bigMessage.getMessageType()).thenReturn(Protocol.PUT);
        //when(bigMessage.getData(Protocol.PutDatas.RESOURCE.name())).thenReturn(new byte[]{1, 2, 3, 5});
        when(bigMessage.getParam(Protocol.PutParams.REPLICATE.name())).thenReturn("true");
        when(serializationHandler.decode("", null)).thenReturn(serializableResource);

        dHashEnvironment.process(bigMessage);

        verify(dHashNode).replicateData(serializableResource.getId(), (name, current, size) -> {});
        verify(resourceManager).save(serializableResource);
    }

    @Test
    public void resourceCompare_notExist_returnFalse() throws OverlayException {
        when(message.getMessageType()).thenReturn(Protocol.RESOURCE_COMPARE);
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(false);

        Message response = dHashEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("dhash");
        assertThat(response.getParam(Protocol.ResourceCompareResponseParams.EXIST_RESOURCE.name())).isEqualTo("false");

    }

    @Test
    public void resourceCompare_existChecksumNotEqual_returnFalse() throws OverlayException {
        when(message.getMessageType()).thenReturn(Protocol.RESOURCE_COMPARE);
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getParam(Protocol.ResourceCompareParams.CHECK_SUM.name())).thenReturn("checksum");
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);
        when(resourceManager.find("resource")).thenReturn(serializableResource);
        when(checksumeCalculator.calculate(serializableResource)).thenReturn("checksumother");

        Message response = dHashEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("dhash");
        assertThat(response.getParam(Protocol.ResourceCompareResponseParams.EXIST_RESOURCE.name())).isEqualTo("false");

    }

    @Test
    public void resourceCompare_existChecksumEqual_returnTrue() throws OverlayException {
        when(message.getMessageType()).thenReturn(Protocol.RESOURCE_COMPARE);
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getParam(Protocol.ResourceCompareParams.CHECK_SUM.name())).thenReturn("checksum");
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);
        when(resourceManager.find("resource")).thenReturn(serializableResource);
        when(checksumeCalculator.calculate(serializableResource)).thenReturn("checksum");

        Message response = dHashEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("dhash");
        assertThat(response.getParam(Protocol.ResourceCompareResponseParams.EXIST_RESOURCE.name())).isEqualTo("true");

    }

    @Test
    public void get_hasResource_returnTrue() throws OverlayException {
        when(message.getMessageType()).thenReturn(Protocol.GET);
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);

        Message response = dHashEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("dhash");
        assertThat(response.getParam(Protocol.GetResponseParams.HAS_RESOURCE.name())).isEqualTo("true");

    }

    @Test
    public void get_notHasResource_returnTrue() throws OverlayException {
        when(message.getMessageType()).thenReturn(Protocol.GET);
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(false);

        Message response = dHashEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("dhash");
        assertThat(response.getParam(Protocol.GetResponseParams.HAS_RESOURCE.name())).isEqualTo("false");

    }

    @Test
    public void resourceTransfere_hasResource_returnTrue() throws OverlayException {
        when(message.getMessageType()).thenReturn(Protocol.RESOURCE_TRANSFER);
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);
        when(resourceManager.find("resource")).thenReturn(serializableResource);
        when(serializationHandler.encode(serializableResource)).thenReturn("");

        Message response = dHashEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.RESOURCE_TRANSFER_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("dhash");
        //assertThat(response.getData(Protocol.ResourceTransferResponseData.RESOURCE.name())).isEqualTo(new byte[]{1, 2, 3});

    }

    @Test
    public void resourceTransfere_notHasResource_returnTrue() throws OverlayException {
        when(message.getMessageType()).thenReturn(Protocol.RESOURCE_TRANSFER);
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getAddress()).thenReturn(Address.builder().source("source").build());
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(false);

        Message response = dHashEnvironment.process(message);

        assertThat(response.getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(response.getMessageType()).isEqualTo(Protocol.RESOURCE_TRANSFER_RESPONSE);
        assertThat(response.getAddress().getDestination()).isEqualTo("source");
        assertThat(response.getAddress().getSource()).isEqualTo("dhash");
        //assertThat(response.getData(Protocol.ResourceTransferResponseData.RESOURCE.name())).isEqualTo(new byte[0]);

    }
}