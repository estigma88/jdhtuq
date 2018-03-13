package co.edu.uniquindio.dhash.resource.serialization;

import co.edu.uniquindio.dhash.resource.BytesResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class ObjectSerializationHandlerTest {
    @InjectMocks
    private ObjectSerializationHandler objectSerializationHandler;

    @Test
    public void encode() {
        BytesResource bytesResource = new BytesResource("resource", new byte[]{1, 2, 3});

        byte[] result = objectSerializationHandler.encode(bytesResource);

        assertThat(result).isEqualTo(new byte[]{-84, -19, 0, 5, 115, 114, 0, 46, 99, 111, 46, 101, 100, 117, 46, 117, 110, 105, 113, 117, 105, 110, 100, 105, 111, 46, 100, 104, 97, 115, 104, 46, 114, 101, 115, 111, 117, 114, 99, 101, 46, 66, 121, 116, 101, 115, 82, 101, 115, 111, 117, 114, 99, 101, -116, 103, 98, 44, 108, 42, 75, -122, 2, 0, 2, 91, 0, 5, 98, 121, 116, 101, 115, 116, 0, 2, 91, 66, 76, 0, 2, 105, 100, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 117, 114, 0, 2, 91, 66, -84, -13, 23, -8, 6, 8, 84, -32, 2, 0, 0, 120, 112, 0, 0, 0, 3, 1, 2, 3, 116, 0, 8, 114, 101, 115, 111, 117, 114, 99, 101});
    }

    @Test
    public void decode() {
        BytesResource bytesResource = new BytesResource("resource", new byte[]{1, 2, 3});

        BytesResource result = (BytesResource) objectSerializationHandler.decode(new byte[]{-84, -19, 0, 5, 115, 114, 0, 46, 99, 111, 46, 101, 100, 117, 46, 117, 110, 105, 113, 117, 105, 110, 100, 105, 111, 46, 100, 104, 97, 115, 104, 46, 114, 101, 115, 111, 117, 114, 99, 101, 46, 66, 121, 116, 101, 115, 82, 101, 115, 111, 117, 114, 99, 101, -116, 103, 98, 44, 108, 42, 75, -122, 2, 0, 2, 91, 0, 5, 98, 121, 116, 101, 115, 116, 0, 2, 91, 66, 76, 0, 2, 105, 100, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 117, 114, 0, 2, 91, 66, -84, -13, 23, -8, 6, 8, 84, -32, 2, 0, 0, 120, 112, 0, 0, 0, 3, 1, 2, 3, 116, 0, 8, 114, 101, 115, 111, 117, 114, 99, 101});

        assertThat(result.getId()).isEqualTo(bytesResource.getId());
        assertThat(result.getBytes()).isEqualTo(bytesResource.getBytes());
    }

}