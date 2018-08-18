package co.edu.uniquindio.chord.starter;

import co.edu.uniquindio.chord.ChordKeyFactory;
import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.chord.hashing.HashingGeneratorImp;
import co.edu.uniquindio.chord.node.BootStrap;
import co.edu.uniquindio.chord.node.ChordNodeFactory;
import co.edu.uniquindio.chord.node.command.CheckPredecessorObserver;
import co.edu.uniquindio.chord.node.command.FixFingersObserver;
import co.edu.uniquindio.chord.node.command.FixSuccessorsObserver;
import co.edu.uniquindio.chord.node.command.StabilizeObserver;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.overlay.OverlayNodeFactory;
import co.edu.uniquindio.utils.communication.message.SequenceGenerator;
import co.edu.uniquindio.utils.communication.message.SequenceGeneratorImpl;
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
    public OverlayNodeFactory overlayNodeFactory(CommunicationManager communicationManagerChord, BootStrap bootStrap, ScheduledExecutorService scheduledStableRing, List<Observer> stableRingObservers, KeyFactory keyFactory, SequenceGenerator chordSequenceGenerator) {
        return new ChordNodeFactory(communicationManagerChord, new HashSet<>(), chordProperties.getStableRingTime(), chordProperties.getSuccessorListAmount(), bootStrap, scheduledStableRing, stableRingObservers, keyFactory, chordSequenceGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommunicationManager communicationManagerChord(CommunicationManagerFactory communicationManagerFactory) {
        return communicationManagerFactory.newCommunicationManager("chord");
    }

    @Bean
    @ConditionalOnMissingBean
    public BootStrap bootStrap() {
        return new BootStrap();
    }

    @Bean
    @ConditionalOnMissingBean
    public HashingGenerator hashingGenerator() {
        return new HashingGeneratorImp();
    }

    @Bean
    @ConditionalOnMissingBean
    public SequenceGenerator chordSequenceGenerator() {
        return new SequenceGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public KeyFactory keyFactory(HashingGenerator hashingGenerator) {
        return new ChordKeyFactory(hashingGenerator, chordProperties.getKeyLength());
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService scheduledStableRing() {
        return Executors.newScheduledThreadPool(chordProperties.getStableRingThreadPool());
    }

    @Bean
    @ConditionalOnMissingBean
    public List<Observer> stableRingObservers() {
        return Arrays.asList(new FixSuccessorsObserver(), new FixFingersObserver(), new CheckPredecessorObserver(), new StabilizeObserver());
    }
}
