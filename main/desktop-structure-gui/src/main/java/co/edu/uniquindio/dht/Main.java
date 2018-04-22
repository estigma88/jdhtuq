package co.edu.uniquindio.dht;

import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.dht.gui.structure.StructureWindow;
import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(Main.class)
                .headless(false).run(args);

        SwingUtilities.invokeLater(() -> {
            StructureWindow window = applicationContext.getBean(StructureWindow.class);

            window.setVisible(true);
        });
    }

    @Bean
    public StructureWindow structureWindow(StorageNodeFactory storageNodeFactory, @Qualifier("communicationManagerChord") CommunicationManager communicationManager, HashingGenerator hashingGenerator, KeyFactory keyFactory, @Value("${ui-structure.resource-directory}") String resourceDirectory) {
        Controller controller = new Controller(storageNodeFactory, communicationManager, hashingGenerator, keyFactory);
        StructureWindow structureWindow = new StructureWindow(hashingGenerator, keyFactory, resourceDirectory);

        controller.setStructureWindow(structureWindow);
        structureWindow.setController(controller);

        return structureWindow;
    }
}

