package co.edu.uniquindio.dhash.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:dhash.yaml")
@ConfigurationProperties(prefix = "p2p.dhash")
public class DHashProperties {
    private int replicationAmount;

    public int getReplicationAmount() {
        return replicationAmount;
    }
}
