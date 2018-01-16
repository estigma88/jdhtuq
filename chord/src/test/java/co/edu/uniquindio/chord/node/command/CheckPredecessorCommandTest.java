package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CheckPredecessorCommandTest {
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private CheckPredecessorCommand checkPredecessorCommand;

    @Test
    public void run_chordNode_checkPredeccesor(){
        checkPredecessorCommand.run();

        verify(chordNode).checkPredeccesor();
    }


}