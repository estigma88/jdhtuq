/*
 *  LookupService project defined all services an lookup algorithm
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

package co.edu.uniquindio.overlay;

import java.math.BigInteger;

/**
 * The {@code Key} class is responsible for encapsulating an string value with
 * its respective hashing number.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
public interface Key {
    /**
     * This method is used for compare if the key is between two specific keys.
     * This comparison is made in a circular way
     *
     * @param key1 . The first key of the comparison
     * @param key2 . The second key of the comparison
     * @return Returns true if the key is between the keys that are received as
     * parameter
     */
    boolean isBetween(Key key1, Key key2);

    /**
     * This method is used for getting the string value of the key
     *
     * @return Returns the string value of the key
     */
    String getValue();

    /**
     * This method is used for getting the hashing number of the key
     *
     * @return Returns the hashing number of the key
     */
    BigInteger getHashing();
}