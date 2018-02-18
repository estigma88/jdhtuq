package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.ResourceAlreadyExistException;
import co.edu.uniquindio.dhash.resource.ResourceManager;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.dhash.resource.file.SerializableResource;
import co.edu.uniquindio.utils.communication.message.BigMessage;
import co.edu.uniquindio.utils.communication.message.BigMessageXML;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({SerializableResource.class})
@RunWith(PowerMockRunner.class)
public class DHashEnvironmentTest {
    @Mock
    private CommunicationManager communicationManager;
    @Mock
    private DHashNode dHashNode;
    @Mock
    private BigMessage bigMessage;
    @Mock
    private Message message;
    @Mock
    private ResourceManager resourceManager;
    @Mock
    private SerializableResource serializableResource;
    @InjectMocks
    private DHashEnvironment dHashEnvironment;
    @Captor
    private ArgumentCaptor<MessageXML> messageCaptor;
    @Captor
    private ArgumentCaptor<BigMessageXML> bigMessageCaptor;

    @Test
    public void put_notBigMessage_doNothing() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.PUT.getName());

        dHashEnvironment.update(message);

        verify(dHashNode, times(0)).getObjectManager();
        verify(dHashNode, times(0)).replicateData(any());
    }

    @Test
    public void put_bigMessageNotReplicate_save() throws OverlayException, ResourceAlreadyExistException {
        when(bigMessage.getType()).thenReturn(Protocol.PUT.getName());
        when(bigMessage.getData(Protocol.PutDatas.RESOURCE.name())).thenReturn(new byte[]{1, 2, 3, 5});
        ;
        when(bigMessage.getParam(Protocol.PutParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(bigMessage.getParam(Protocol.PutParams.REPLICATE.name())).thenReturn("false");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);

        dHashEnvironment.update(bigMessage);

        verify(dHashNode, times(0)).replicateData(any());
        verify(resourceManager).put("resource", new byte[]{1, 2, 3, 5});
    }

    @Test
    public void put_bigMessageReplicate_replicate() throws OverlayException, ResourceAlreadyExistException, IOException, ClassNotFoundException {
        mockStatic(SerializableResource.class);
        when(SerializableResource.valueOf(new byte[]{1, 2, 3, 5})).thenReturn(serializableResource);

        when(bigMessage.getType()).thenReturn(Protocol.PUT.getName());
        when(bigMessage.getData(Protocol.PutDatas.RESOURCE.name())).thenReturn(new byte[]{1, 2, 3, 5});
        ;
        when(bigMessage.getParam(Protocol.PutParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(bigMessage.getParam(Protocol.PutParams.REPLICATE.name())).thenReturn("true");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);

        dHashEnvironment.update(bigMessage);

        verify(dHashNode).replicateData(serializableResource);
        verify(resourceManager).put("resource", new byte[]{1, 2, 3, 5});
    }

    @Test
    public void resourceCompare_notExist_returnFalse() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.RESOURCE_COMPARE.getName());
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getMessageSource()).thenReturn("source");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(false);

        dHashEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.ResourceCompareResponseParams.EXIST_RESOURCE.name())).isEqualTo("false");

    }

    @Test
    public void resourceCompare_existChecksumNotEqual_returnFalse() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.RESOURCE_COMPARE.getName());
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getParam(Protocol.ResourceCompareParams.CHECK_SUM.name())).thenReturn("checksum");
        when(message.getMessageSource()).thenReturn("source");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);
        when(resourceManager.get("resource")).thenReturn(serializableResource);
        when(serializableResource.getCheckSum()).thenReturn("checksumother");

        dHashEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.ResourceCompareResponseParams.EXIST_RESOURCE.name())).isEqualTo("false");

    }

    @Test
    public void resourceCompare_existChecksumEqual_returnTrue() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.RESOURCE_COMPARE.getName());
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getParam(Protocol.ResourceCompareParams.CHECK_SUM.name())).thenReturn("checksum");
        when(message.getMessageSource()).thenReturn("source");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);
        when(resourceManager.get("resource")).thenReturn(serializableResource);
        when(serializableResource.getCheckSum()).thenReturn("checksum");

        dHashEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_COMPARE_RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.ResourceCompareResponseParams.EXIST_RESOURCE.name())).isEqualTo("true");

    }

    @Test
    public void get_hasResource_returnTrue() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.GET.getName());
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getMessageSource()).thenReturn("source");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);

        dHashEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.GET_RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.GetResponseParams.HAS_RESOURCE.name())).isEqualTo("true");

    }

    @Test
    public void get_notHasResource_returnTrue() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.GET.getName());
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getMessageSource()).thenReturn("source");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(false);

        dHashEnvironment.update(message);

        verify(communicationManager).sendMessageUnicast(messageCaptor.capture());

        assertThat(messageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.GET_RESPONSE);
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("source");
        assertThat(messageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(messageCaptor.getAllValues().get(0).getParam(Protocol.GetResponseParams.HAS_RESOURCE.name())).isEqualTo("false");

    }

    @Test
    public void resourceTransfere_hasResource_returnTrue() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.RESOURCE_TRANSFER.getName());
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getMessageSource()).thenReturn("source");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(true);
        when(resourceManager.get("resource")).thenReturn(serializableResource);
        when(serializableResource.getSerializable()).thenReturn(new byte[]{1, 2, 3});

        dHashEnvironment.update(message);

        verify(communicationManager).sendBigMessage(bigMessageCaptor.capture());

        assertThat(bigMessageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(bigMessageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_TRANSFER_RESPONSE);
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("source");
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(bigMessageCaptor.getAllValues().get(0).getData(Protocol.ResourceTransferResponseData.RESOURCE.name())).isEqualTo(new byte[]{1, 2, 3});

    }

    @Test
    public void resourceTransfere_notHasResource_returnTrue() throws OverlayException, ResourceAlreadyExistException {
        when(message.getType()).thenReturn(Protocol.RESOURCE_TRANSFER.getName());
        when(message.getParam(Protocol.ResourceCompareParams.RESOURCE_KEY.name())).thenReturn("resource");
        when(message.getMessageSource()).thenReturn("source");
        when(dHashNode.getObjectManager()).thenReturn(resourceManager);
        when(dHashNode.getName()).thenReturn("dhash");
        when(resourceManager.hasResource("resource")).thenReturn(false);

        dHashEnvironment.update(message);

        verify(communicationManager).sendBigMessage(bigMessageCaptor.capture());

        assertThat(bigMessageCaptor.getAllValues().get(0).getSendType()).isEqualTo(Message.SendType.RESPONSE);
        assertThat(bigMessageCaptor.getAllValues().get(0).getMessageType()).isEqualTo(Protocol.RESOURCE_TRANSFER_RESPONSE);
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getDestination()).isEqualTo("source");
        assertThat(bigMessageCaptor.getAllValues().get(0).getAddress().getSource()).isEqualTo("dhash");
        assertThat(bigMessageCaptor.getAllValues().get(0).getData(Protocol.ResourceTransferResponseData.RESOURCE.name())).isEqualTo(new byte[0]);

    }
}