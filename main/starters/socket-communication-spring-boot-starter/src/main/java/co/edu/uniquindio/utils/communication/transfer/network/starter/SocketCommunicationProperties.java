package co.edu.uniquindio.utils.communication.transfer.network.starter;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "communication.socket")
@Data
public class SocketCommunicationProperties {
    private Map<String, Map<String, String>> instances = new HashMap<>();
}
