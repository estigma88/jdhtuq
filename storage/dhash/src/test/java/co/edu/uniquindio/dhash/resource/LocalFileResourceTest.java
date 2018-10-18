package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class LocalFileResourceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private ProgressStatus progressStatus;
    private Path source;
    private Path destination;

    @Before
    public void before() {
        source = Paths.get("src/test/resources/file.txt");
        destination = Paths.get("src/test/resources/");
    }

    @After
    public void after() throws IOException {
        Files.deleteIfExists(destination.resolve("resource.txt"));
    }

    @Test
    public void persist_transferOk_file() throws StorageException, IOException {
        FileResource resource = FileResource.withInputStream()
                .id("resource.txt")
                .inputStream(Files.newInputStream(source))
                .size(Files.size(source))
                .checkSum("F8A22FF48BD073675387E0C5F1A0C26B").build();

        LocalFileResource localFileResource = spy(LocalFileResource.builder()
                .resource(resource)
                .path(destination.toString())
                .bufferSize(1024)
                .build());

        File file = localFileResource.persist(progressStatus);

        assertThat(file).isNotNull();
        assertThat(file.exists()).isTrue();
        assertThat(Files.readAllLines(file.toPath())).isEqualTo(Files.readAllLines(source));
    }

    @Test
    public void persist_transferNotOk_exception() throws StorageException, IOException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Checksum is not valid, F8A22FF48BD073675387E0C5F1A0C26B != checkSum");

        FileResource resource = FileResource.withInputStream()
                .id("resource.txt")
                .inputStream(Files.newInputStream(source))
                .size(Files.size(source))
                .checkSum("checkSum").build();

        LocalFileResource localFileResource = spy(LocalFileResource.builder()
                .resource(resource)
                .path(destination.toString())
                .bufferSize(1024)
                .build());

        localFileResource.persist(progressStatus);
    }
}