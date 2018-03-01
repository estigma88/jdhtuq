package co.edu.uniquindio.chord.starter;

import co.edu.uniquindio.chord.ChordKeyFactory;
import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.chord.hashing.HashingGeneratorImp;
import co.edu.uniquindio.chord.node.BootStrap;
import co.edu.uniquindio.chord.node.ChordNodeFactory;
import co.edu.uniquindio.chord.node.command.CheckPredecessorCommand;
import co.edu.uniquindio.chord.node.command.FixFingersCommand;
import co.edu.uniquindio.chord.node.command.FixSuccessorsCommand;
import co.edu.uniquindio.chord.node.command.StabilizeCommand;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ConditionalOnClass({OverlayNodeFactory.class, ChordNodeFactory.class})
@EnableConfigurationProperties(ChordProperties.class)
public class ChordAutoConfiguration {
    @Autowired
    private ChordProperties chordProperties;

    @Bean
    @ConditionalOnMissingBean
    public OverlayNodeFactory overlayNodeFactory(CommunicationManager communicationManagerChord, BootStrap bootStrap, ScheduledExecutorService scheduledStableRing, List<Observer> stableRingObservers, KeyFactory keyFactory) {
        return new ChordNodeFactory(communicationManagerChord, new HashSet<>(), chordProperties.getStableRingTime(), chordProperties.getSuccessorListAmount(), bootStrap, scheduledStableRing, stableRingObservers, keyFactory);
    }

    @Bean
    public CommunicationManager communicationManagerChord(CommunicationManagerFactory communicationManagerFactory) {
        return communicationManagerFactory.newCommunicationManager("chord");
    }

    @Bean
    public BootStrap bootStrap() {
        return new BootStrap();
    }

    @Bean
    public HashingGenerator hashingGenerator() {
        return new HashingGeneratorImp();
    }

    @Bean
    public KeyFactory keyFactory(HashingGenerator hashingGenerator) {
        return new ChordKeyFactory(hashingGenerator, chordProperties.getKeyLength());
    }

    @Bean
    public ScheduledExecutorService scheduledStableRing() {
        return Executors.newScheduledThreadPool(chordProperties.getStableRingThreadPool());
    }

    @Bean
    public List<Observer> stableRingObservers() {
        return Arrays.asList(new FixSuccessorsCommand(), new FixFingersCommand(), new CheckPredecessorCommand(), new StabilizeCommand());
    }
}
