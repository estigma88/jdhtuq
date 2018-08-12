/*
 *  desktop-structure-gui  is a example of the peer to peer application with a desktop ui
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


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

