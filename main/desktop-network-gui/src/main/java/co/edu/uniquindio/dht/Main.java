package co.edu.uniquindio.dht;

import co.edu.uniquindio.dht.gui.network.NetworkWindow;
import co.edu.uniquindio.storage.StorageNodeFactory;
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
            NetworkWindow window = applicationContext.getBean(NetworkWindow.class);

            window.setVisible(true);
        });
    }

    @Bean
    public NetworkWindow networkWindow(StorageNodeFactory storageNodeFactory, @Value("${ui-network.resource-directory}") String resourceDirectory) {
        return new NetworkWindow(storageNodeFactory, resourceDirectory);
    }
}

