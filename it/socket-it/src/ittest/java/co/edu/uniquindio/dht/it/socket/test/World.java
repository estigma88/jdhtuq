package co.edu.uniquindio.dht.it.socket.test;

import co.edu.uniquindio.dht.it.socket.test.ring.Ring;
import lombok.Data;

@Data
public class World {
    private Ring ring;
    private String nodeGateway;
}

