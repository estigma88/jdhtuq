package co.edu.uniquindio.dht.it.socket.ring;

import lombok.Data;

@Data
public class Node {
    private final String name;
    private final String hashing;
    private final String successor;
}
