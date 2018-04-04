package co.edu.uniquindio.dht.it.datastructure;

import com.jayway.restassured.RestAssured;
import cucumber.runtime.arquillian.CukeSpace;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.HostPort;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CukeSpace.class)
public class PingPongTest {
    @HostIp
    private String ip;

    @HostPort(containerName = "planetstest", value = 8080)
    int planetsPort;

    @Test
    public void shouldReturnAverage() throws MalformedURLException {
        URL url = new URL("http://" + ip + ":" + planetsPort + "/starwars/");
        final String average = RestAssured.get(url.toExternalForm() + "rest/planet/orbital/average").asString();
        assertThat(average).isEqualTo("1699.42");
    }
}
