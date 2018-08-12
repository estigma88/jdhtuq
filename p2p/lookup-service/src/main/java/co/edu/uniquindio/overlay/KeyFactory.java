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

import java.math.BigInteger;

/**
 * Factory of keys
 */
public interface KeyFactory {
    /**
     * Create a new key
     *
     * @param value key value
     * @return new key
     */
    Key newKey(String value);

    /**
     * Create a new key with the hashing value
     *
     * @param hashing key hashing
     * @return new key
     */
    Key newKey(BigInteger hashing);

    /**
     * Key length
     *
     * @return key length
     */
    int getKeyLength();

    /**
     * Update the key length
     *
     * @param keyLength new key length
     */
    void updateKeyLength(int keyLength);
}
