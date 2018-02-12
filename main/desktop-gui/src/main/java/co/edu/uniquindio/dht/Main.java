package co.edu.uniquindio.dht;

import co.edu.uniquindio.dht.gui.MainFrame;
import org.apache.log4j.xml.DOMConfigurator;

import javax.swing.*;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

public class Main {
    /**
     * Path for properties fiel
     */
    private static final String PROPERTIES = "resources/logger.xml";

    public static void main(String[] args) {
        try {
            loadLoggerProperties();

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {

        } catch (ClassNotFoundException e) {

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }

        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    private static void loadLoggerProperties() throws FactoryConfigurationError {
        try {
            CodeSource codeSource = Main.class.getProtectionDomain()
                    .getCodeSource();
            File jarFile;

            jarFile = new File(codeSource.getLocation().toURI().getPath());

            File jarDir = jarFile.getParentFile();

            File propFile = null;
            if (jarDir != null && jarDir.isDirectory()) {
                propFile = new File(jarDir, PROPERTIES);
            }

            DOMConfigurator.configure(propFile.getPath());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

