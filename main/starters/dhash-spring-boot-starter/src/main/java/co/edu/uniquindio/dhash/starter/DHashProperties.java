package co.edu.uniquindio.dhash.starter;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "p2p.dhash")
@Data
public class DHashProperties {
    private int replicationAmount = 1;
    private String resourceDirectory = "dhash/";
}
