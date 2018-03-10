package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.node.command.CheckPredecessorObserver;
import co.edu.uniquindio.chord.node.command.FixFingersObserver;
import co.edu.uniquindio.chord.node.command.FixSuccessorsObserver;
import co.edu.uniquindio.chord.node.command.StabilizeObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StableRingTest {
    @Mock
    private ChordNode node;
    @InjectMocks
    @Spy
    private StableRing stableRing;

    @Test
    public void run_notify(){
        doNothing().when(stableRing).notifyObservers(node);

        stableRing.run();

        verify(stableRing).notifyObservers(node);
    }

}