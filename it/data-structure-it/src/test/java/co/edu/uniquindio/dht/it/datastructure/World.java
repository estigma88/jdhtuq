package co.edu.uniquindio.dht.it.datastructure;

import co.edu.uniquindio.dht.it.datastructure.ring.Ring;
import lombok.Data;

@Data
public class World {
    private Ring ring;
    private String nodeGateway;
}

