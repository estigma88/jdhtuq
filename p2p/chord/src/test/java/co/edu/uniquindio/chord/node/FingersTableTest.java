package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FingersTableTest {
    private ChordKey[] fingersTableArray;
    @Mock
    private ChordKey finger1;
    @Mock
    private ChordKey finger2;
    @Mock
    private ChordKey finger3;
    @Mock
    private ChordKey key;
    @Mock
    private ChordKey successor;
    @Mock
    private KeyFactory keyFactory;
    @Mock
    private ChordNode chordNode;
    private FingersTable fingersTable;

    @Before
    public void before() {
        when(chordNode.getKey()).thenReturn(key);

        fingersTableArray = new ChordKey[]{finger1, finger2, finger3};
        fingersTable = spy(new FingersTable(fingersTableArray, chordNode, 0, 3, keyFactory));
    }

    @Test
    public void findClosestPresedingNode_notFound_returnNodeKey() {
        ChordKey search = mock(ChordKey.class);

        ChordKey result = fingersTable.findClosestPresedingNode(search);

        assertThat(result).isEqualTo(key);
    }

    @Test
    public void findClosestPresedingNode_notFoundNullFingers_returnNodeKey() {
        fingersTable = spy(new FingersTable(new ChordKey[]{null, null, null}, chordNode, 0, 3, keyFactory));

        ChordKey search = mock(ChordKey.class);

        ChordKey result = fingersTable.findClosestPresedingNode(search);

        assertThat(result).isEqualTo(key);
    }

    @Test
    public void findClosestPresedingNode_found_returnFingerKey() {
        ChordKey search = mock(ChordKey.class);

        when(finger2.isBetween(key, search)).thenReturn(true);

        ChordKey result = fingersTable.findClosestPresedingNode(search);

        assertThat(result).isEqualTo(finger2);
    }

    @Test
    public void fixFingers_successorNotFound_setNodeSuccessor() {
        ChordKey next = mock(ChordKey.class);

        doReturn(next).when(fingersTable).createNext(key);

        when(chordNode.getSuccessor()).thenReturn(successor);
        when(chordNode.findSuccessor(next, LookupType.FINGERS_TABLE)).thenReturn(null);

        fingersTable.fixFingers();

        assertThat(fingersTable.getFingersTable()[1]).isEqualTo(successor);
    }

    @Test
    public void fixFingers_successorFound_setKeyFound() {
        ChordKey next = mock(ChordKey.class);
        ChordKey found = mock(ChordKey.class);

        doReturn(next).when(fingersTable).createNext(key);

        when(chordNode.findSuccessor(next, LookupType.FINGERS_TABLE)).thenReturn(found);

        fingersTable.fixFingers();

        assertThat(fingersTable.getFingersTable()[1]).isEqualTo(found);
    }

    @Test
    public void fixFingers_turnFingers_setNodeSuccessor() {
        fingersTable = spy(new FingersTable(fingersTableArray, chordNode, 3, 3, keyFactory));

        ChordKey next = mock(ChordKey.class);

        doReturn(next).when(fingersTable).createNext(key);

        when(chordNode.getSuccessor()).thenReturn(successor);
        when(chordNode.findSuccessor(next, LookupType.FINGERS_TABLE)).thenReturn(null);

        fingersTable.fixFingers();

        assertThat(fingersTable.getFingersTable()[0]).isEqualTo(successor);
    }

    @Test
    public void setSuccessor_set_newFirstFinger() {
        ChordKey newSuccessor = mock(ChordKey.class);

        fingersTable.setSuccessor(newSuccessor);

        assertThat(fingersTable.getFingersTable()[0]).isEqualTo(newSuccessor);
    }

    @Test
    public void createNext_next0_newKey() {
        ChordKey key = mock(ChordKey.class);
        ChordKey newKey = mock(ChordKey.class);

        when(key.getHashing()).thenReturn(new BigInteger("10"));
        when(newKey.getHashing()).thenReturn(new BigInteger("3"));
        when(keyFactory.newKey(new BigInteger("3"))).thenReturn(newKey);

        ChordKey next = fingersTable.createNext(key);

        assertThat(next.getHashing()).isEqualTo(new BigInteger("3"));
    }

    @Test
    public void createNext_next2_newKey() {
        fingersTable = spy(new FingersTable(fingersTableArray, chordNode, 2, 3, keyFactory));

        ChordKey key = mock(ChordKey.class);
        ChordKey newKey = mock(ChordKey.class);

        when(key.getHashing()).thenReturn(new BigInteger("10"));
        when(newKey.getHashing()).thenReturn(new BigInteger("6"));
        when(keyFactory.newKey(new BigInteger("6"))).thenReturn(newKey);

        ChordKey next = fingersTable.createNext(key);

        assertThat(next.getHashing()).isEqualTo(new BigInteger("6"));
    }
}