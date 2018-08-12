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

package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.utils.communication.message.Message;
import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

/**
 * The <code>ReAssignObserver</code> class is notify when overlay node leave and
 * reassign all resource in the network using
 * <code>DHashNode.relocateAllResources</code>
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public class ReAssignObserver implements Observer {
    /**
     * Logger
     */
    private static final Logger logger = Logger
            .getLogger(ReAssignObserver.class);
    public static final String RE_ASSIGN = "RE_ASSIGN";
    public static final String PREDECESSOR = "PREDECESSOR";

    private final DHashNode dHashNode;
    private final KeyFactory keyFactory;

    public ReAssignObserver(DHashNode dHashNode, KeyFactory keyFactory) {
        this.dHashNode = dHashNode;
        this.keyFactory = keyFactory;
    }

    @Override
    public void update(Observable observable, Object object) {
        if (object instanceof Message) {
            Message message = (Message) object;

            logger.info("Update: " + message);

            if (message.getMessageType().getName().equals(RE_ASSIGN)) {
                dHashNode.relocateAllResources(keyFactory.newKey(message.getParam(PREDECESSOR)));
            }
        }
    }
}
