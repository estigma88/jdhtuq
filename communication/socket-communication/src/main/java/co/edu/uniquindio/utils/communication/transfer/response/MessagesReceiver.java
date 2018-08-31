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

package co.edu.uniquindio.utils.communication.transfer.response;

import co.edu.uniquindio.utils.communication.transfer.Communicator;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;

import java.io.Closeable;

public class MessagesReceiver implements Closeable, Runnable {
    private boolean run;
    private Communicator communicator;
    private MessageProcessor messageProcessor;

    public MessagesReceiver(Communicator communicator,
                            MessageProcessor messageProcessor) {
        this.run = true;
        this.communicator = communicator;
        this.messageProcessor = messageProcessor;
    }

    public void close() {
        run = false;
    }

    public void run() {
        while (run) {
            messageProcessor.process(communicator.receiver());
        }
    }

}