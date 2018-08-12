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

import org.apache.log4j.Logger;

import java.util.concurrent.Semaphore;

/**
 * The {@code WaitingResult} class is responsible for waiting for a response to
 * a message with a specific sequence number. When a response for a given
 * message does not arrive in a specific amount of time this class return
 * {@code null} as the response for that message
 *
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
class WaitingResult<T> extends Thread {

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(WaitingResult.class);

    /**
     * The sequence number of the message
     */
    private long sequence;

    /**
     * The response for the message with the specific sequence number
     */
    private T result;

    /**
     * This semaphore is blocked when asked for the response to a certain
     * message, and is released when the response to the message arrives
     */
    private Semaphore semaphore;

    /**
     * This variable is used for Knowing if a response for a message has arrived
     */
    private boolean stop;

    /**
     * This variable is the specific amount of time that the class waits for a
     * response of a given message
     */
    private long timeOut = -1;

    /**
     * Reference to a ReturnsManager instance
     */
    private ReturnsManager<T> returnsManager;

    /**
     * The constructor of the class. Constructs a WaitingResult instance that
     * wait for the response of the message with the sequence number specified
     *
     * @param sequence                    . The sequence number of the message
     */
    WaitingResult(long sequence, ReturnsManager<T> returnsManager) {
        this.sequence = sequence;
        this.returnsManager = returnsManager;
        this.stop = false;
        this.semaphore = new Semaphore(0);
    }

    /**
     * The constructor of the class. Constructs a WaitingResult instance that
     * wait for the response of the message with the sequence number specified
     *
     * @param sequence                    . The sequence number of the message
     */
    WaitingResult(long sequence, ReturnsManager<T> returnsManager, long timeOut) {
        this(sequence, returnsManager);
        this.timeOut = timeOut;
    }

    /**
     * Executes the thread while <code>stop==false</code> and
     * <code>timeOut!=0</code>. This is, while the response for the message has
     * not arrived or while the specific amount of time has not finished. If
     * timeOut is -1, waiting until that the response arribe
     */
    public void run() {

        if (timeOut != -1) {

            while (!stop && timeOut != 0) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeOut--;
            }

        } else {

            while (!stop) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        if (!stop) {

            logger
                    .debug("Timeout waiting for a response for number sequence= '"
                            + sequence + "'");

            logger.debug("Timeout waiting for a response");

            returnsManager.releaseWaitingResult(sequence, null);
        }
    }

    /**
     * This method is used for getting the response of the message
     *
     * @return Returns the response for the message
     */
    public T getResult() {
        start();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.error("Error to stop semaphore", e);
        }

        return result;
    }

    /**
     * This method is used for setting the response of the message
     *
     * @param result . This is the response of the message
     */
    public void setResult(T result) {
        this.result = result;

        semaphore.release();

        stop = true;
    }

}
