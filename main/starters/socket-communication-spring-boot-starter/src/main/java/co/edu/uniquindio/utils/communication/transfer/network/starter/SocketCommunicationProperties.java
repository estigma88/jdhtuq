package co.edu.uniquindio.utils.communication.transfer.network.starter;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "communication.socket")
@Getter
public class SocketCommunicationProperties {
}
