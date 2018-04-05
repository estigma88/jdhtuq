package co.edu.uniquindio.dht.it.socket;

import co.edu.uniquindio.dht.it.socket.ring.Ring;
import lombok.Data;

@Data
public class World {
    private Ring ring;
    private String nodeGateway;
}

