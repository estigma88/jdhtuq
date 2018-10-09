package co.edu.uniquindio.dhash.resource.manager;

import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class FileResourceManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private FileResourceManager fileResourceManager;
    @Mock
    private ProgressStatus progressStatus;

    @Before
    public void before() {
        fileResourceManager = new FileResourceManager("src/test/resources", "10.20.10.10", 1024);
    }

    @After
    public void after() throws IOException {
        Files.deleteIfExists(Paths.get("src/test/resources/10.20.10.10/resource2.txt/resource2.txt"));
        Files.deleteIfExists(Paths.get("src/test/resources/10.20.10.10/resource2.txt/resource2.txt.MD5"));
        Files.deleteIfExists(Paths.get("src/test/resources/10.20.10.10/resource2.txt/"));
    }

    @Test
    public void getAllKeys() throws StorageException {
        HashSet<String> keys = new HashSet<>();
        keys.add("resource1.txt");

        assertThat(fileResourceManager.getAllKeys()).isEqualTo(keys);
    }

    @Test
    public void hasResource_has_true() {
        assertThat(fileResourceManager.hasResource("resource1.txt")).isTrue();
    }

    @Test
    public void hasResource_notHas_false() {
        assertThat(fileResourceManager.hasResource("resource3.txt")).isFalse();
    }

    @Test
    public void find_notHas_null() throws StorageException {
        Resource resource = fileResourceManager.find("resource3.txt");

        assertThat(resource).isNull();
    }

    @Test
    public void find_has_resource() throws StorageException, IOException {
        Resource resource = fileResourceManager.find("resource1.txt");

        assertThat(resource).isNotNull();
        assertThat(resource.getId()).isEqualTo("resource1.txt");
        assertThat(resource.getSize()).isEqualTo(Files.size(Paths.get("src/test/resources/10.20.10.10/resource1.txt/resource1.txt")));
        assertThat(resource.getCheckSum()).isEqualTo(Files.readAllLines(Paths.get("src/test/resources/10.20.10.10/resource1.txt/resource1.txt.MD5")).get(0));
        assertThat(IOUtils.toByteArray(resource.getInputStream())).isEqualTo(Files.readAllBytes(Paths.get("src/test/resources/10.20.10.10/resource1.txt/resource1.txt")));
    }

    @Test
    public void save_transferOk_saved() throws IOException, StorageException {
        FileResource resource = FileResource.withInputStream()
                .id("resource2.txt")
                .inputStream(Files.newInputStream(Paths.get("src/test/resources/file.txt")))
                .size(Files.size(Paths.get("src/test/resources/file.txt")))
                .checkSum("F8A22FF48BD073675387E0C5F1A0C26B").build();

        fileResourceManager.save(resource, progressStatus);

        assertThat(Files.exists(Paths.get("src/test/resources/10.20.10.10/resource2.txt/resource2.txt"))).isTrue();
        assertThat(Files.exists(Paths.get("src/test/resources/10.20.10.10/resource2.txt/resource2.txt.MD5"))).isTrue();
        assertThat(Files.readAllLines(Paths.get("src/test/resources/10.20.10.10/resource2.txt/resource2.txt"))).isEqualTo(Files.readAllLines(Paths.get("src/test/resources/file.txt")));
        assertThat(Files.readAllLines(Paths.get("src/test/resources/10.20.10.10/resource2.txt/resource2.txt.MD5")).get(0)).isEqualTo("F8A22FF48BD073675387E0C5F1A0C26B");
    }

    @Test
    public void save_transferNotOk_exception() throws IOException, StorageException {
        thrown.expect(StorageException.class);
        thrown.expectMessage("Checksum is not valid, F8A22FF48BD073675387E0C5F1A0C26B != checkSum");

        FileResource resource = FileResource.withInputStream()
                .id("resource2.txt")
                .inputStream(Files.newInputStream(Paths.get("src/test/resources/file.txt")))
                .size(Files.size(Paths.get("src/test/resources/file.txt")))
                .checkSum("checkSum").build();

        fileResourceManager.save(resource, progressStatus);
    }
}