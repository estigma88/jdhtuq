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

import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
public class WaitingResult<T>{

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
     * This countDownLatch is blocked when asked for the response to a certain
     * message, and is released when the response to the message arrives
     */
    private CountDownLatch countDownLatch;

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
    private WaitingResult(long sequence, ReturnsManager<T> returnsManager) {
        this.sequence = sequence;
        this.returnsManager = returnsManager;
        this.countDownLatch = new CountDownLatch(1);
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
     * This method is used for getting the response of the message
     *
     * @return Returns the response for the message
     */
    public T getResult() {
        try {
            if(countDownLatch.await(timeOut, TimeUnit.MILLISECONDS)){
                logger
                        .debug("Response arrives for number sequence= '"
                                + sequence + "'");
            }else{
                logger
                        .debug("Timeout waiting for a response for number sequence= '"
                                + sequence + "'");

                returnsManager.releaseWaitingResult(sequence, null);
            }
        } catch (InterruptedException e) {
            logger.error("Error to close countDownLatch", e);
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

        countDownLatch.countDown();
    }

}
