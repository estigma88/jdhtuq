package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.Resource;
import co.edu.uniquindio.storage.resource.ResourceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({SerializableResource.class})
@RunWith(PowerMockRunner.class)
public class ResourceManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private SerializableResource serializableResource;
    @Mock
    private SerializableResource newSerializableResource;
    private Map<String, Resource> resources;
    private ResourceManager resourceManager;

    @Before
    public void before() {
        resources = new HashMap<>();
        resources.put("resource", serializableResource);

        resourceManager = new ResourceManager(resources, "name");
    }

    @Test
    public void put_create_persist() throws IOException, ClassNotFoundException, ResourceException {
        mockStatic(SerializableResource.class);
        when(SerializableResource.valueOf(new byte[]{1, 2, 3})).thenReturn(newSerializableResource);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SerializableResource.ResourceParams.MANAGER_NAME.name(), "name");
        params.put(SerializableResource.ResourceParams.PERSIST_TYPE.name(), "put");

        resourceManager.put("newResource", new byte[]{1, 2, 3});

        assertThat(resourceManager.get("newResource")).isEqualTo(newSerializableResource);

        verify(newSerializableResource).persist(params);
    }

    @Test
    public void hasResource_notFound_returnFalse() throws IOException, ClassNotFoundException, ResourceException {
        boolean result = resourceManager.hasResource("resourceother");

        assertThat(result).isFalse();
    }

    @Test
    public void hasResource_found_returnTrue() throws IOException, ClassNotFoundException, ResourceException {
        boolean result = resourceManager.hasResource("resource");

        assertThat(result).isTrue();
    }

    @Test
    public void get_found_returnTrue() throws IOException, ClassNotFoundException, ResourceException {
        Resource result = resourceManager.get("resource");

        assertThat(result).isEqualTo(serializableResource);
    }

    @Test
    public void get_notFound_exception() throws IOException, ClassNotFoundException, ResourceException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The resource not found");

        resourceManager.get("resourceother");
    }

    @Test
    public void delete_notFound_returnTrue() throws IOException, ClassNotFoundException, ResourceException {
        boolean result = resourceManager.delete("resourceother");

        assertThat(result).isTrue();
    }

    @Test
    public void delete_found_returnTrue() throws IOException, ClassNotFoundException, ResourceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SerializableResource.ResourceParams.MANAGER_NAME.name(), "resource");

        boolean result = resourceManager.delete("resource");

        assertThat(result).isTrue();
        assertThat(resourceManager.getResourcesNames()).doesNotContain("resource");
        verify(serializableResource).delete(params);
    }

    @Test
    public void deleteResources_found_deleteAll() throws IOException, ClassNotFoundException, ResourceException {
        resources.put("otherresource", newSerializableResource);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SerializableResource.ResourceParams.MANAGER_NAME.name(), "name");

        resourceManager.deleteResources();

        assertThat(resourceManager.getResourcesNames()).isNotEmpty();
        verify(serializableResource).delete(params);
        verify(newSerializableResource).delete(params);
    }
}