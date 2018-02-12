package co.edu.uniquindio.chord.node.command;

import co.edu.uniquindio.chord.node.ChordNode;
import co.edu.uniquindio.chord.node.FingersTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FixFingersCommandTest {
    @Mock
    private FingersTable fingersTable;
    @Mock
    private ChordNode chordNode;
    @InjectMocks
    private FixFingersCommand fixFingersCommand;

    @Test
    public void run_chordNode_checkPredeccesor(){
        when(chordNode.getFingersTable()).thenReturn(fingersTable);

        fixFingersCommand.run();

        verify(fingersTable).fixFingers();
    }
}