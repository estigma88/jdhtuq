package co.edu.uniquindio.utils.communication.transfer.structure.starter;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "communication.data-structure")
@Getter
public class DataStructureCommunicationProperties {
}
