package co.edu.uniquindio.dht.it.socket.test.put;

import lombok.Data;

@Data
public class Content {
    private final String name;
    private final String dockerPath;
    private final String localPath;
    private final String node;
}
