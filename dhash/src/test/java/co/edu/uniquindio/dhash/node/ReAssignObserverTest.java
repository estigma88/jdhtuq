package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.dhash.resource.ResourceAlreadyExistException;
import co.edu.uniquindio.utils.hashing.HashingGenerator;
import co.edu.uniquindio.utils.hashing.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({HashingGenerator.class})
@RunWith(PowerMockRunner.class)
public class ReAssignObserverTest {
    @Mock
    private DHashNode dhashNode;
    @Mock
    private HashingGenerator hashingGenerator;
    @Captor
    private ArgumentCaptor<Key> keyCaptor;
    private ReAssignObserver reAssignObserver;

    @Before
    public void before(){
        reAssignObserver = new ReAssignObserver();
        reAssignObserver.setDHashNode(dhashNode);
    }

    @Test
    public void update_messageLength1_doNothing(){
        String[] message = new String[]{"REASSIGN"};

        reAssignObserver.update(message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageLength3_doNothing(){
        String[] message = new String[]{"REASSIGN", "REASSIGN", "REASSIGN"};

        reAssignObserver.update(message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageTypeWrong_doNothing(){
        String[] message = new String[]{"REASSIGNWRONG", "123"};

        reAssignObserver.update(message);

        verifyZeroInteractions(dhashNode);
    }

    @Test
    public void update_messageCorrect_relocateAllResources() throws ResourceAlreadyExistException {
        mockStatic(HashingGenerator.class);
        when(HashingGenerator.getInstance()).thenReturn(hashingGenerator);

        String[] message = new String[]{"REASSIGN", "123"};

        reAssignObserver.update(message);

        verify(dhashNode).relocateAllResources(keyCaptor.capture());

        assertThat(keyCaptor.getValue().getValue()).isEqualTo("123");
    }
}