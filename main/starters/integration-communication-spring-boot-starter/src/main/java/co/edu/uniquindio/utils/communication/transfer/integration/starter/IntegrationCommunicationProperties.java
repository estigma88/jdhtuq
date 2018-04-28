package co.edu.uniquindio.utils.communication.transfer.network.starter;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "communication.integration")
@Data
public class IntegrationCommunicationProperties {
    private String baseURL;
    private String requestPath = "{communicationName}/messages/";
    private Map<String, Map<String, String>> instances = new HashMap<>();
}
