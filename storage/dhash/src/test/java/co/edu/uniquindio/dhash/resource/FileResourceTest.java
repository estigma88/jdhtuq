package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.dhash.resource.checksum.ChecksumInputStreamCalculator;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileResourceTest {
    @Mock
    private InputStream inputStream;
    @Mock
    private ChecksumInputStreamCalculator checksumInputStreamCalculator;
    @Mock
    private ProgressStatus progressStatus;

    @Test
    public void build_withPath() throws IOException {
        FileResource expectedResource = FileResource.withInputStream()
                .id("id")
                .checkSum("checkSum")
                .size(10L)
                .inputStream(inputStream)
                .build();

        FileResource.WithPathBuilder builder = spy(FileResource.withPath());

        doReturn(inputStream).when(builder).newInputStream(Paths.get("path"));
        doReturn(checksumInputStreamCalculator).when(builder).getChecksumInputStreamCalculator();
        doReturn(10L).when(builder).getFileSize(Paths.get("path"));
        when(checksumInputStreamCalculator.calculate(inputStream, 10L, progressStatus)).thenReturn("checkSum");

        FileResource resource = builder
                .id("id")
                .path("path")
                .build(progressStatus);

        assertThat(resource).isEqualTo(expectedResource);

    }
}