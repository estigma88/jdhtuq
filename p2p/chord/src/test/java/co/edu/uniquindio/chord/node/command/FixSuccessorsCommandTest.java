package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.chord.node.SuccessorList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FixSuccessorsCommandTest {
    @Mock
    private SuccessorList successorList;
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private FixSuccessorsCommand fixSuccessorsCommand;

    @Test
    public void run_chordNode_checkPredeccesor() {
        when(chordNode.getSuccessorList()).thenReturn(successorList);

        fixSuccessorsCommand.run();

        verify(successorList).fixSuccessors();
    }
}