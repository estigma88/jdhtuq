package co.edu.uniquindio.chord.hashing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class HashingGeneratorImpTest {

    @InjectMocks
    private HashingGeneratorImp hashingGeneratorImp;

    @Test
    public void generateHashing_value_hashing(){
        BigInteger hashing = hashingGeneratorImp.generateHashing("value", 16);

        assertThat(hashing).isEqualTo(new BigInteger("1"));
    }
}