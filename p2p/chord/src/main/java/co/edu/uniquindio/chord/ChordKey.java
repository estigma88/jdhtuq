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

package co.edu.uniquindio.chord;

import co.edu.uniquindio.overlay.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Optional;

/**
 * The {@code ChordKey} class is responsible for encapsulating an string value with
 * its respective hashing number.
 *
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 */
@Getter
@EqualsAndHashCode
public class ChordKey implements Key {
    public static final String TOSTRING_SEPARATOR = ",";
    public static final String DEFAULT_NAME = "-";
    /**
     * The string value of the key
     */
    private final String value;

    /**
     * The hashing number generated for the string value of the key
     */
    private final BigInteger hashing;

    /**
     * This constructor creates a key given a hashing number
     *
     * @param hashing . The hashing number of the key
     */
    public ChordKey(BigInteger hashing) {
        this.value = null;
        this.hashing = hashing;
    }

    /**
     * This constructor creates a key with the specified string value and
     * generate the respective hashing number to the value
     *
     * @param value   . The string that represents the key
     * @param hashing . The hashing number of the key
     */
    public ChordKey(String value, BigInteger hashing) {
        this.value = value;
        this.hashing = hashing;
    }

    /**
     * This method is used for compare if the key is between two specific keys
     * right included. This comparison is made in a circular way.
     *
     * @param key1 . The first key of the comparison
     * @param key2 . The second key of the comparison
     * @return Returns true if the key is between the keys that are received as
     * parameter
     */
    public boolean isBetweenRightIncluded(Key key1, Key key2) {
        if (key1.getHashing().compareTo(key2.getHashing()) == -1) {
            if (hashing.compareTo(key1.getHashing()) == 1
                    && (hashing.compareTo(key2.getHashing()) == -1 || hashing
                    .compareTo(key2.getHashing()) == 0)) {
                return true;
            }
        } else {
            if (key1.getHashing().compareTo(key2.getHashing()) == 1) {
                if ((hashing.compareTo(key1.getHashing()) == 1 && hashing
                        .compareTo(key2.getHashing()) == 1)
                        || hashing.compareTo(key2.getHashing()) == 0) {
                    return true;
                } else {
                    if (hashing.compareTo(key1.getHashing()) == -1
                            && hashing.compareTo(key2.getHashing()) == -1) {
                        return true;
                    }
                }
            } else {
                if (hashing.compareTo(key2.getHashing()) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method is used for compare if the key is between two specific keys.
     * This comparison is made in a circular way
     *
     * @param key1 . The first key of the comparison
     * @param key2 . The second key of the comparison
     * @return Returns true if the key is between the keys that are received as
     * parameter
     */
    public boolean isBetween(Key key1, Key key2) {
        if (key1.getHashing().compareTo(key2.getHashing()) == -1) {
            if (hashing.compareTo(key1.getHashing()) == 1
                    && (hashing.compareTo(key2.getHashing()) == -1)) {
                return true;
            }
        } else {
            if (key1.getHashing().compareTo(key2.getHashing()) == 1) {
                if (hashing.compareTo(key1.getHashing()) == 1
                        && hashing.compareTo(key2.getHashing()) == 1) {
                    return true;
                } else {
                    if (hashing.compareTo(key1.getHashing()) == -1
                            && hashing.compareTo(key2.getHashing()) == -1) {
                        return true;
                    }
                }
            } else {
                if (hashing.compareTo(key2.getHashing()) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(value).orElse(DEFAULT_NAME) +
                TOSTRING_SEPARATOR +
                Optional.ofNullable(hashing).orElse(BigInteger.ZERO);
    }

    public static ChordKey valueOf(String value) {
        String[] parts = value.split(TOSTRING_SEPARATOR);

        return new ChordKey(parts[0].equals(DEFAULT_NAME) ? null : parts[0], new BigInteger(parts[1]));
    }
}