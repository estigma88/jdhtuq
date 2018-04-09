package co.edu.uniquindio.dht.it.socket.test;

import java.net.MalformedURLException;
import java.net.URL;

//@RunWith(CukeSpace.class)
public class PingPongTest {
    //@HostIp
    private String ip;

    //@HostPort(containerName = "planetstest", value = 8080)
    int planetsPort;

    //@Test
    public void shouldReturnAverage() throws MalformedURLException {
        URL url = new URL("http://" + ip + ":" + planetsPort + "/starwars/");
        //final String average = RestAssured.get(url.toExternalForm() + "rest/planet/orbital/average").asString();
        //assertThat(average).isEqualTo("1699.42");
    }
}
