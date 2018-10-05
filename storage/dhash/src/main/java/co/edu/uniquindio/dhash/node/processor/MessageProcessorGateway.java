/*
 *  DHash project implement a storage management
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

package co.edu.uniquindio.dhash.node.processor;

import co.edu.uniquindio.dhash.node.DHashNode;
import co.edu.uniquindio.dhash.protocol.Protocol;
import co.edu.uniquindio.dhash.resource.manager.ResourceManager;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageType;
import co.edu.uniquindio.utils.communication.transfer.MessageProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The <code>DHashEnviroment</code> class is the node responsible for handling
 * with the messages. This class is notified when a message arrives and decides
 * what the dhash node must do.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @see DHashNode
 * @since 1.0
 */
@Slf4j
public class MessageProcessorGateway implements MessageProcessor {
    private final DHashNode dHashNode;
    private final Map<MessageType, MessageProcessor> messageProcessorMap;

    public MessageProcessorGateway(DHashNode dHashNode, ResourceManager resourceManager) {
        this.dHashNode = dHashNode;

        this.messageProcessorMap = new HashMap<>();
        this.messageProcessorMap.put(Protocol.CONTAIN, createContainProcessor(dHashNode, resourceManager));
    }

    @Override
    public Message process(Message message) {

        log.debug("Message to: " + dHashNode.getName() + " Message:["
                + message.toString() + "]");
        log.debug("Node " + dHashNode.getName() + ", arrived message of "
                + message.getMessageType());

        return Optional.ofNullable(messageProcessorMap.get(message.getMessageType()))
                .orElseThrow(() -> new IllegalStateException("Message " + message.getMessageType() + " was not found"))
                .process(message);
    }

    ContainProcessor createContainProcessor(DHashNode dHashNode, ResourceManager resourceManager) {
        return new ContainProcessor(dHashNode, resourceManager);
    }

    Map<MessageType, MessageProcessor> getMessageProcessorMap() {
        return messageProcessorMap;
    }
}
