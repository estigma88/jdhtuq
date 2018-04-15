package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StabilizeObserverTest {
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private StabilizeObserver stabilizeObserver;

    @Test
    public void run_chordNode_checkPredeccesor(){
        stabilizeObserver.update(null, chordNode);

        verify(chordNode).stabilize();
    }

}