package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StabilizeCommandTest {
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private StabilizeCommand stabilizeCommand;

    @Test
    public void run_chordNode_checkPredeccesor(){
        stabilizeCommand.run();

        verify(chordNode).stabilize();
    }

}