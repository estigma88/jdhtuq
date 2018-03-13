package co.edu.uniquindio.dhash.resource.checksum;

import co.edu.uniquindio.dhash.resource.BytesResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BytesChecksumCalculatorTest {
    @InjectMocks
    private BytesChecksumCalculator bytesChecksumCalculator;

    @Test
    public void calculate() {
        BytesResource bytesResource = new BytesResource("resource", new byte[]{1, 2, 3});

        String checksum = bytesChecksumCalculator.calculate(bytesResource);

        assertThat(checksum).isEqualTo("5289df737df57326fcdd22597afb1fac");
    }
}