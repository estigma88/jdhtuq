package co.edu.uniquindio.chord;

import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.overlay.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChordKeyFactoryTest {
    @Mock
    private HashingGenerator hashingGenerator;
    private int keyLength = 160;
    private ChordKeyFactory chordKeyFactory;

    @Before
    public void before() {
        chordKeyFactory = new ChordKeyFactory(hashingGenerator, keyLength);
    }

    @Test
    public void newKeyByBigInteger() {
        Key key = chordKeyFactory.newKey(new BigInteger("132"));

        assertThat(key.getValue()).isNull();
        assertThat(key.getHashing()).isEqualTo(new BigInteger("132"));
    }

    @Test
    public void newKeyByString() {
        when(hashingGenerator.generateHashing("key", keyLength)).thenReturn(new BigInteger("132"));

        Key key = chordKeyFactory.newKey("key");

        assertThat(key.getValue()).isEqualTo("key");
        assertThat(key.getHashing()).isEqualTo(new BigInteger("132"));
    }
}