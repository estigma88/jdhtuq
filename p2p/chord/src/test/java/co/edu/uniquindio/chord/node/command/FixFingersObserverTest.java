package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.chord.node.FingersTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FixFingersObserverTest {
    @Mock
    private FingersTable fingersTable;
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private FixFingersObserver fixFingersObserver;

    @Test
    public void run_chordNode_checkPredeccesor(){
        when(chordNode.getFingersTable()).thenReturn(fingersTable);

        fixFingersObserver.update(null, chordNode);

        verify(fingersTable).fixFingers();
    }
}