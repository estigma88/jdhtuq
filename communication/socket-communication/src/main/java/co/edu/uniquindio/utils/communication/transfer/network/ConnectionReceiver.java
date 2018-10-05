/*
 *  Communication project implement communication point to point and multicast
 *  Copyright (C) 2010  Daniel Pelaez, Daniel Lopez, Hector Hurtado
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

package co.edu.uniquindio.utils.communication.transfer.network;

import co.edu.uniquindio.utils.communication.transfer.ConnectionHandler;
import co.edu.uniquindio.utils.communication.transfer.ConnectionListener;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class ConnectionReceiver implements Closeable, Runnable {
    private boolean run;
    private final ConnectionListener connectionListener;
    private final ConnectionHandler connectionHandler;

    public ConnectionReceiver(ConnectionListener connectionListener, ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.run = true;
        this.connectionListener = connectionListener;
    }

    public void close() {
        run = false;
    }

    public void run() {
        while (run) {
            try {
                Socket socket = connectionListener.listen();

                connectionHandler.handle(socket);
            } catch (IOException e) {
                log.error("Error processing connection", e);
            }
        }
    }

}
