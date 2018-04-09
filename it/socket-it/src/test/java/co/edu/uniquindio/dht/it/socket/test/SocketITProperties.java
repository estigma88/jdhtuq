package co.edu.uniquindio.dht.it.socket.test;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "socket-it")
@Data
public class SocketITProperties {
    private Map<String, Integer> portMapping = new HashMap<>();
}
