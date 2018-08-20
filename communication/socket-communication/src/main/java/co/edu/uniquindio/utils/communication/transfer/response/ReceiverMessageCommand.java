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

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;

/**
 * The {@code ReceiverMessageCommand} class, is a Command implementation, that is
 * used for receiving a message and notify to the observer the arrive the
 * message
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class ReceiverMessageCommand implements Runnable {
    /**
     * The message that has arrived
     */
    private Message message;

    /**
     * The observer that is notified for the arrival the message
     */
    private Observable<Message> observable;

    private final CommunicationManager communicationManager;

    /**
     * The constructor of the class. This creates a {@code
     * ReceiverMessageCommand} instance with the message that arrives and the
     * observer that has to be notified
     *
     * @param message
     * @param communicationManager
     */
    public ReceiverMessageCommand(Message message,
                                  Observable<Message> observable, CommunicationManager communicationManager) {
        this.message = message;
        this.observable = observable;
        this.communicationManager = communicationManager;
    }

    /**
     * Notifies to the observer the arrived message
     */
    public void run() {
        Message response = null;
        if (communicationManager.getMessageProcessor() != null) {
            response = communicationManager.getMessageProcessor().process(message);
        }
        if (response != null) {
            communicationManager.sendMessageUnicast(response);
            observable.notifyMessage(message);
        }
    }
}
