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

package co.edu.uniquindio.dht.gui.network;


import co.edu.uniquindio.dht.gui.PanelDhash;
import co.edu.uniquindio.storage.StorageNode;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

//TODO Documentar
@SuppressWarnings("serial")
@Slf4j
public class NetworkWindow extends JFrame implements WindowListener {
    //TODO Documentar
    private PanelDhash panelDhash;

    //TODO Documentar
    public NetworkWindow(StorageNode storageNode, String resourceDirectory) {
        this(storageNode, "Network Window", resourceDirectory);
    }

    //TODO Documentar
    public NetworkWindow(StorageNode storageNode, String title, String resourceDirectory) {
        setTitle(title);
        setLayout(new BorderLayout());
        addWindowListener(this);

        panelDhash = new PanelDhash(PanelDhash.NETWORK, this, resourceDirectory);

        add(panelDhash, BorderLayout.CENTER);

        pack();

        setLocationRelativeTo(getParent());

        panelDhash.setDHashNode(storageNode);
    }

    //TODO Documentar
    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    //TODO Documentar
    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    //TODO Documentar
    @Override
    public void windowClosing(WindowEvent arg0) {
        panelDhash.exit();
        System.exit(0);
    }

    //TODO Documentar
    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    //TODO Documentar
    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    //TODO Documentar
    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    //TODO Documentar
    @Override
    public void windowOpened(WindowEvent arg0) {
    }
}
