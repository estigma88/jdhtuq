/*
 *  LookupService project defined all services an lookup algorithm
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

package co.edu.uniquindio.overlay;

import java.util.Observable;

/**
 * The {@code OverlayNode} interface defines the basic methods for routing
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public interface OverlayNode {
    /**
     * Finds the node on the overlay that is the responsible of the given key.
     *
     * @param id The key to find
     * @return A {@link Key} that is the responsible for the id.
     */
    Key lookUp(Key id);

    /**
     * Gets the {@link Key} of the Overlay node.
     *
     * @return The {@link Key} of the Overlay node.
     */
    Key getKey();

    /**
     * Takes the node out of the network.
     *
     * @return Key[] An array with node's neighbors.
     */
    Key[] leave() throws OverlayException;

    /**
     * Gets the observable object responsible for notifying information.
     *
     * @return An {@link Observable} object.
     */
    Observable getObservable();

    /**
     * Gets the list of node's neighbors.
     *
     * @return Key[] An array with node's neighbors.
     */
    Key[] getNeighborsList() throws OverlayException;
}
