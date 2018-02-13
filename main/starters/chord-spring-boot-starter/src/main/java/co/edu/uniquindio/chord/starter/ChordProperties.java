package co.edu.uniquindio.chord.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:chord.yaml")
@ConfigurationProperties(prefix = "p2p.chord")
public class ChordProperties {
    private int stableRingTime;
    private int successorListAmount;

    public int getStableRingTime() {
        return stableRingTime;
    }

    public int getSuccessorListAmount() {
        return successorListAmount;
    }
}
