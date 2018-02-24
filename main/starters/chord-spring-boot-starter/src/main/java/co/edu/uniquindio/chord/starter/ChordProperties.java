package co.edu.uniquindio.chord.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "p2p.chord")
@Data
public class ChordProperties {
    private int stableRingTime = 2000;
    private int successorListAmount = 3;
    private CommunicationType communicationType = CommunicationType.NETWORK;
    private int stableRingThreadPool = 3;
}
