package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.utils.hashing.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FingersTableTest {
    private Key[] fingersTableArray;
    @Mock
    private Key finger1;
    @Mock
    private Key finger2;
    @Mock
    private Key finger3;
    @Mock
    private Key key;
    @Mock
    private Key successor;
    @Mock
    private ChordNode chordNode;
    private FingersTable fingersTable;

    @Before
    public void before() {
        when(chordNode.getKey()).thenReturn(key);

        fingersTableArray = new Key[]{finger1, finger2, finger3};
        fingersTable = spy(new FingersTable(fingersTableArray, chordNode, 0, 3));
    }

    @Test
    public void findClosestPresedingNode_notFound_returnNodeKey() {
        Key search = mock(Key.class);

        Key result = fingersTable.findClosestPresedingNode(search);

        assertThat(result).isEqualTo(key);
    }

    @Test
    public void findClosestPresedingNode_found_returnFingerKey() {
        Key search = mock(Key.class);

        when(finger2.isBetween(key, search)).thenReturn(true);

        Key result = fingersTable.findClosestPresedingNode(search);

        assertThat(result).isEqualTo(finger2);
    }

    @Test
    public void fixFingers_successorNotFound_setNodeSuccessor() {
        Key next = mock(Key.class);

        doReturn(next).when(fingersTable).createNext(key);

        when(chordNode.getSuccessor()).thenReturn(successor);
        when(chordNode.findSuccessor(next, LookupType.FINGERS_TABLE)).thenReturn(null);

        fingersTable.fixFingers();

        assertThat(fingersTable.getFingersTable()[1]).isEqualTo(successor);
    }

    @Test
    public void fixFingers_successorFound_setKeyFound() {
        Key next = mock(Key.class);
        Key found = mock(Key.class);

        doReturn(next).when(fingersTable).createNext(key);

        when(chordNode.findSuccessor(next, LookupType.FINGERS_TABLE)).thenReturn(found);

        fingersTable.fixFingers();

        assertThat(fingersTable.getFingersTable()[1]).isEqualTo(found);
    }

    @Test
    public void fixFingers_turnFingers_setNodeSuccessor() {
        fingersTable = spy(new FingersTable(fingersTableArray, chordNode, 3, 3));

        Key next = mock(Key.class);

        doReturn(next).when(fingersTable).createNext(key);

        when(chordNode.getSuccessor()).thenReturn(successor);
        when(chordNode.findSuccessor(next, LookupType.FINGERS_TABLE)).thenReturn(null);

        fingersTable.fixFingers();

        assertThat(fingersTable.getFingersTable()[0]).isEqualTo(successor);
    }
}