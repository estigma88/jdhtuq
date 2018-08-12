/*
 *  Communication project implement communication point to point and multicast
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

package co.edu.uniquindio.utils.communication.message;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Address {

    /**
     * Source name
     */
    private final String source;
    /**
     * Destination name
     */
    private final String destination;

    /**
     * This method is used for knowing if the message is the same source and
     * destination node
     *
     * @return Returns true if the message is the same source and destination
     * node
     */
    public boolean isMessageFromMySelf() {
        if (source != null && destination != null) {
            return source.equals(destination);
        } else {
            if (source != null) {
                return false;
            } else {
                if (destination != null) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }
}
