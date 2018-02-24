package co.edu.uniquindio.dht;

import co.edu.uniquindio.dhash.starter.CommunicationType;
import co.edu.uniquindio.dht.gui.network.NetworkWindow;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.hashing.HashingGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(Main.class)
                .headless(false);

        EventQueue.invokeLater(() -> {
            Object[] options = {"Network",
                    "Structure"};

            int answer = JOptionPane.showOptionDialog(null,
                    "Which mode do you like to run?",
                    "Run mode",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            CommunicationType runMode;
            if (answer == JOptionPane.YES_OPTION) {
                runMode = CommunicationType.NETWORK;
            } else {
                runMode = CommunicationType.DATA_STRUCTURE;
            }

            ConfigurableApplicationContext applicationContext = springApplicationBuilder
                    .properties("p2p.dhash.communication_type=" + runMode.name(), "p2p.chord.communication_type=" + runMode.name())
                    .run(args);

            JFrame window;
            if (runMode == CommunicationType.NETWORK) {
                window = applicationContext.getBean(NetworkWindow.class);
            } else {
                window = applicationContext.getBean(StructureWindow.class);
            }

            window.setVisible(true);
        });
    }

    @Bean
    @Lazy
    public StructureWindow structureWindow(StorageNodeFactory storageNodeFactory, @Qualifier("communicationManagerChord") CommunicationManager communicationManager, HashingGenerator hashingGenerator) {
        Controller controller = new Controller(storageNodeFactory, communicationManager, hashingGenerator);
        StructureWindow structureWindow = new StructureWindow(hashingGenerator);

        controller.setStructureWindow(structureWindow);
        structureWindow.setController(controller);

        return structureWindow;
    }

    @Bean
    @Lazy
    public NetworkWindow networkWindow(StorageNodeFactory storageNodeFactory) {
        return new NetworkWindow(storageNodeFactory);
    }
}

