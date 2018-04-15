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

import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerWaitingResult;
import co.edu.uniquindio.utils.communication.transfer.Communicator;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The <code>CommunicationManagerNetworkLAN</code> abstract class management
 * send message multicast and recieves all messages from all sources. Required
 * params: BUFFER_SIZE_MULTICAST, IP_MULTICAST and PORT_MULTICAST
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public abstract class CommunicationManagerNetworkLAN extends
        CommunicationManagerWaitingResult {
    protected final MessageSerialization messageSerialization;

    protected CommunicationManagerNetworkLAN(MessageSerialization messageSerialization) {
        this.messageSerialization = messageSerialization;
    }

    /**
     * Properties for configuration CommunicationManagerNetworkLAN
     *
     * @author Daniel Pelaez
     * @author Hector Hurtado
     * @author Daniel Lopez
     * @version 1.0, 17/06/2010
     * @since 1.0
     */
    public enum CommunicationManagerNetworkLANProperties {
        BUFFER_SIZE_MULTICAST, IP_MULTICAST, PORT_MULTICAST
    }

    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(CommunicationManagerNetworkLAN.class);

    /**
     * Reciever all messages
     */
    private MessagesReciever messagesReciever;

    /*
     * (non-Javadoc)
     *
     * @seeco.edu.uniquindio.utils.communication.transfer.
     * CommunicationManagerWaitingResult#init ()
     */
    public void init() {
        super.init();
        this.messagesReciever = new MessagesReciever(multicastManager,
                unicastManager, unicastBigManager, this, this);
        this.observableCommunication = messagesReciever;
    }

    /**
     * Creates a Communicator for multicast manager. Are required of params in
     * communication properties: PORT_MULTICAST, IP_MULTICAST and
     * BUFFER_SIZE_MULTICAST
     */
    protected Communicator createMulticastManager() {

        int portMulticast;
        String ipMulticast;
        long bufferSize;

        if (communicationProperties
                .containsKey(CommunicationManagerNetworkLANProperties.PORT_MULTICAST
                        .name())) {
            portMulticast = Integer
                    .parseInt(communicationProperties
                            .get(CommunicationManagerNetworkLANProperties.PORT_MULTICAST
                                    .name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property PORT_MULTICAST not found");

            logger.error("Property PORT_MULTICAST no found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }

        if (communicationProperties
                .containsKey(CommunicationManagerNetworkLANProperties.IP_MULTICAST
                        .name())) {
            ipMulticast = communicationProperties
                    .get(CommunicationManagerNetworkLANProperties.IP_MULTICAST
                            .name());
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property IP_MULTICAST not found");

            logger.error("Property IP_MULTICAST no found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }

        if (communicationProperties
                .containsKey(CommunicationManagerNetworkLANProperties.BUFFER_SIZE_MULTICAST
                        .name())) {
            bufferSize = Long
                    .parseLong(communicationProperties
                            .get(CommunicationManagerNetworkLANProperties.BUFFER_SIZE_MULTICAST
                                    .name()));
        } else {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Property BUFFER_SIZE_MULTICAST not found");

            logger.error("Property BUFFER_SIZE_MULTICAST no found",
                    illegalArgumentException);

            throw illegalArgumentException;
        }

        try {
            multicastManager = new MulticastManagerNetworkLAN(portMulticast,
                    InetAddress.getByName(ipMulticast), bufferSize, messageSerialization);
        } catch (UnknownHostException e) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    "Error of ipmulticast", e);

            logger.error("Error of ipmulticast", illegalArgumentException);

            throw illegalArgumentException;
        }

        return multicastManager;
    }

    /**
     * Stop messages reciever and multicast manager
     */
    protected void stop() {
        messagesReciever.stop();
        multicastManager.stop();
    }



}
