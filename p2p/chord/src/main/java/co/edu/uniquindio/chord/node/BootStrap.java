/*
 *  Chord project implement of lookup algorithm Chord
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

package co.edu.uniquindio.chord.node;

import co.edu.uniquindio.chord.ChordKey;
import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.utils.communication.message.Address;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.IdGenerator;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import lombok.extern.slf4j.Slf4j;

/**
 * The <code>BootStrap</code> class is responsible for initializing a node
 * created by linking the existing network or creating a new one.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see CommunicationManager
 * @see ChordNode
 * @since 1.0
 */

@Slf4j
public class BootStrap {

    /**
     * A Chord uses this method when is created to initializes. If there is an
     * existing node in the network, calls the method
     * <code>ChordNode.join</code> of the created node giving as parameter the
     * node found, which is responsible for finding the successor of the node,
     * if no other node was found, the method <code>ChordNode.createRing</code>
     * is called, plus initializes the first position of the fingers table with
     * the successor of the node.
     *
     * @param nodeChord            node who will be added to the network.
     * @param communicationManager Communication manager
     * @param idGenerator    Sequence generator
     */
    public void boot(ChordNode nodeChord, CommunicationManager communicationManager, IdGenerator idGenerator) {

        log.info("Search node...");

        Key findNode;
        Message bootStrapMessage;

        bootStrapMessage = Message.builder()
                .id(idGenerator.newId())
                .sendType(Message.SendType.REQUEST)
                .messageType(Protocol.BOOTSTRAP)
                .address(Address.builder()
                        .source(nodeChord.getKey().getValue())
                        .build())
                .build();

        findNode = communicationManager.sendMultiCast(bootStrapMessage,
                ChordKey.class);

        log.info("Finish search node");

        if (findNode == null) {
            /* When no other node is found */
            log.debug(
                    "Node '" + nodeChord.getKey().getValue() + "' found '"
                            + null + "'");
            nodeChord.createRing();
        } else {
            log.debug("Node '" + nodeChord.getKey().getValue() + "' found '"
                    + findNode.getValue() + "'");
            nodeChord.join(findNode);

            if (nodeChord.getSuccessor() == null) {
                /* If the join fails the boot is done again */
                boot(nodeChord, communicationManager, idGenerator);
            }
        }

        nodeChord.getFingersTable().getFingersTable()[0] = nodeChord
                .getSuccessor();

        log.info("Node '" + nodeChord.getKey().getValue()
                + "' have by successor '" + nodeChord.getSuccessor().getValue()
                + "'");
    }
}
