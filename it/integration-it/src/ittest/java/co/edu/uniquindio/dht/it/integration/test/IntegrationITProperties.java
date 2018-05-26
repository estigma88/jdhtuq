package co.edu.uniquindio.dht.it.integration.test;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@ConfigurationProperties(prefix = "socket-it")
@Data
@Component("socketITProperties")
public class IntegrationITProperties {
    private Map<String, Integer> portMapping = new HashMap<>();
    private DHash dhash;

    @Data
    public static class DHash {
        private String resourceDirectory;
    }

    public Integer getPortBy(String ip){
        return Optional.ofNullable(portMapping.get(ip)).orElse(portMapping.get(convertIp(ip)));
    }

    private String convertIp(String ip) {
        return ip.replaceAll("\\.", "-");
    }
}
