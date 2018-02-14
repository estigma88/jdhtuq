package co.edu.uniquindio.chord.starter;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:chord.yaml")
@ConfigurationProperties(prefix = "p2p.chord")
@Getter
public class ChordProperties {
    private int stableRingTime;
    private int successorListAmount;
    private CommunicationType communicationType;
}
