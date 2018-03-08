package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CheckPredecessorObserverTest {
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private CheckPredecessorObserver checkPredecessorObserver;

    @Test
    public void run_chordNode_checkPredeccesor(){
        checkPredecessorObserver.update(null, chordNode);

        verify(chordNode).checkPredecessor();
    }


}