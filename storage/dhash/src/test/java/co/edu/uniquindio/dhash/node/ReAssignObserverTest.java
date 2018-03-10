package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.resource.ResourceAlreadyExistException;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReAssignObserverTest {
    @Mock
    private DHashNode dhashNode;
    @Mock
    private KeyFactory keyFactory;
    @Captor
    private ArgumentCaptor<Key> keyCaptor;
    @InjectMocks
    private ReAssignObserver reAssignObserver;
    @Mock
    private Key key;

    @Test
    public void update_messageLength1_doNothing() {
        String[] message = new String[]{"REASSIGN"};

        reAssignObserver.update(null, message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageLength3_doNothing() {
        String[] message = new String[]{"REASSIGN", "REASSIGN", "REASSIGN"};

        reAssignObserver.update(null, message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageTypeWrong_doNothing() {
        String[] message = new String[]{"REASSIGNWRONG", "123"};

        reAssignObserver.update(null, message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageCorrect_relocateAllResources() throws ResourceAlreadyExistException {
        String[] message = new String[]{"REASSIGN", "123"};

        when(keyFactory.newKey("123")).thenReturn(key);

        reAssignObserver.update(null, message);

        verify(dhashNode).relocateAllResources(key);
    }
}