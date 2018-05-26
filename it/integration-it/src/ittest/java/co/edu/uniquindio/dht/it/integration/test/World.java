package co.edu.uniquindio.dht.it.integration.test;

import co.edu.uniquindio.dht.it.integration.test.ring.Ring;
import lombok.Data;

@Data
public class World {
    private Ring ring;
    private String nodeGateway;
}

