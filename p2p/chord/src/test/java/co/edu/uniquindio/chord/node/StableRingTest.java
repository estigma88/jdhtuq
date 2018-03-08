package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.node.command.CheckPredecessorObserver;
import co.edu.uniquindio.chord.node.command.FixFingersObserver;
import co.edu.uniquindio.chord.node.command.FixSuccessorsObserver;
import co.edu.uniquindio.chord.node.command.StabilizeObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StableRingTest {
    @Mock
    private ChordNode node;
    @Mock
    private StabilizeObserver stabilizeCommand;
    @Mock
    private CheckPredecessorObserver checkPredecessorCommand;
    @Mock
    private FixFingersObserver fixFingersCommand;
    @Mock
    private FixSuccessorsObserver fixSuccessorsCommand;

    private StableRing stableRing;

    @Before
    public void before(){
        /*stableRing = spy(new StableRing(node, 5000, true));

        doReturn(stabilizeCommand).when(stableRing).getStabilizeCommand();
        doReturn(checkPredecessorCommand).when(stableRing).getCheckPredecessorCommand();
        doReturn(fixFingersCommand).when(stableRing).getFixFingersCommand();
        doReturn(fixSuccessorsCommand).when(stableRing).getFixSuccessorsCommand();*/
    }

    @Test
    public void runCommands_runTrue_callCommands(){
        /*stableRing.runCommands();

        verify(stabilizeCommand).execute();
        verify(checkPredecessorCommand).execute();
        verify(fixFingersCommand).execute();
        verify(fixSuccessorsCommand).execute();*/
    }

}