package co.edu.uniquindio.dht;

import co.edu.uniquindio.dht.gui.MainFrame;
import org.apache.log4j.Logger;
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

        EventQueue.invokeLater(() -> {
            MainFrame mainFrame = applicationContext.getBean(MainFrame.class);
            mainFrame.setVisible(true);
        });
    }

    @Bean
    public MainFrame mainFrame() {
        return new MainFrame();
    }
}

