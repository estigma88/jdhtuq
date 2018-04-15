package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.chord.node.SuccessorList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FixSuccessorsObserverTest {
    @Mock
    private SuccessorList successorList;
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private FixSuccessorsObserver fixSuccessorsObserver;

    @Test
    public void run_chordNode_checkPredeccesor() {
        when(chordNode.getSuccessorList()).thenReturn(successorList);

        fixSuccessorsObserver.update(null, chordNode);

        verify(successorList).fixSuccessors();
    }
}