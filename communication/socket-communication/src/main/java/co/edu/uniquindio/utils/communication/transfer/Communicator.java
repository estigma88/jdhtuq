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

package co.edu.uniquindio.utils.communication.transfer;

import co.edu.uniquindio.utils.communication.message.Message;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * The <code>Communicator</code> interface hava all services for to send and to
 * receiver messages
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public interface Communicator extends Closeable {

    /**
     * Send message
     *
     * @param message Message to send
     */
    void send(Message message);

    /**
     * Send message plus and input stream
     *
     * @param message Message to send
     * @param inputStream inputStream to send
     */
    Message receive(Message message);
    void send(Message message, InputStream source);
    void send(Message message, OutputStream destination);
    void send(InputStream source, OutputStream destination) throws IOException;

    /**
     * Reciever message
     *
     * @return Message
     */
    Message receiver();

    void start(Map<String, String> properties);
}
