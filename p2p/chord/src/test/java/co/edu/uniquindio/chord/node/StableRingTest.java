package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.node.command.CheckPredecessorCommand;
import co.edu.uniquindio.chord.node.command.FixFingersCommand;
import co.edu.uniquindio.chord.node.command.FixSuccessorsCommand;
import co.edu.uniquindio.chord.node.command.StabilizeCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StableRingTest {
    @Mock
    private ChordNode node;
    @Mock
    private StabilizeCommand stabilizeCommand;
    @Mock
    private CheckPredecessorCommand checkPredecessorCommand;
    @Mock
    private FixFingersCommand fixFingersCommand;
    @Mock
    private FixSuccessorsCommand fixSuccessorsCommand;

    private StableRing stableRing;

    @Before
    public void before(){
        stableRing = spy(new StableRing(node, 5000, true));

        doReturn(stabilizeCommand).when(stableRing).getStabilizeCommand();
        doReturn(checkPredecessorCommand).when(stableRing).getCheckPredecessorCommand();
        doReturn(fixFingersCommand).when(stableRing).getFixFingersCommand();
        doReturn(fixSuccessorsCommand).when(stableRing).getFixSuccessorsCommand();
    }

    @Test
    public void runCommands_runTrue_callCommands(){
        stableRing.runCommands();

        verify(stabilizeCommand).execute();
        verify(checkPredecessorCommand).execute();
        verify(fixFingersCommand).execute();
        verify(fixSuccessorsCommand).execute();
    }

}