package co.edu.uniquindio.dhash.starter;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:dhash.yaml")
@ConfigurationProperties(prefix = "p2p.dhash")
@Getter
public class DHashProperties {
    private int replicationAmount = 1;
    private CommunicationType communicationType = CommunicationType.DATA_STRUCTURE;
    private String resourceDirectory = "dhash/";
}
