/*
 *  desktop-network-gui is a example of the peer to peer application with a desktop ui
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
 */

package co.edu.uniquindio.dht;

import co.edu.uniquindio.dht.gui.network.NetworkWindow;
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.StorageNodeFactory;
import co.edu.uniquindio.utils.communication.transfer.network.HostNameUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;
import java.util.Optional;

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
    public StorageNode storageNode(StorageNodeFactory storageNodeFactory, @Value("${ui-network.node-name:#{null}}") String nodeName) throws UnknownHostException, StorageException {
        nodeName = Optional.ofNullable(nodeName)
                .orElse(HostNameUtil.getLocalHostAddress());

        return storageNodeFactory.createNode(nodeName);
    }

    @Bean
    public NetworkWindow networkWindow(StorageNode storageNode, @Value("${ui-network.resource-directory}") String resourceDirectory) {
        return new NetworkWindow(storageNode, resourceDirectory);
    }
}

