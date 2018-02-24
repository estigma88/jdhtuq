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

package co.edu.uniquindio.utils.hashing;

import java.math.BigInteger;

/**
 * The {@code Key} class is responsible for encapsulating an string value with
 * its respective hashing number. The hashing is generated using the
 * {@link HashingGenerator} class.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class Key {

	/**
	 * Key length
	 */
	private static int keyLength = 160;
	/**
	 * The string value of the key
	 */
	private String value;

	/**
	 * The hashing number generated for the string value of the key
	 */
	private BigInteger hashing;

	/**
	 * This constructor creates a key given a hashing number
	 * 
	 * @param hashing
	 *            . The hashing number of the key
	 */
	public Key(BigInteger hashing) {
		this.hashing = hashing;
	}

	/**
	 * This constructor creates a key with the specified string value and
	 * generate the respective hashing number to the value
	 * 
	 * @param value
	 *            . The string that represents the key
	 */
	public Key(String value) {
		this.value = value;

		this.hashing = new HashingGeneratorImp().generateHashing(value,
				keyLength);
	}

	/**
	 * This method is used for compare if the key is between two specific keys
	 * right included. This comparison is made in a circular way.
	 * 
	 * @param key1
	 *            . The first key of the comparison
	 * @param key2
	 *            . The second key of the comparison
	 * @return Returns true if the key is between the keys that are received as
	 *         parameter
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
	 * @param key1
	 *            . The first key of the comparison
	 * @param key2
	 *            . The second key of the comparison
	 * @return Returns true if the key is between the keys that are received as
	 *         parameter
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

	/**
	 * This method is used for getting the string value of the key
	 * 
	 * @return Returns the string value of the key
	 */
	public String getValue() {
		return value;
	}

	/**
	 * This method is used for setting the string value of the key
	 * 
	 * @param value
	 *            . The string value of the key
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * This method is used for getting the hashing number of the key
	 * 
	 * @return Returns the hashing number of the key
	 */
	public BigInteger getHashing() {
		return hashing;
	}

	/**
	 * This method is used for getting the hashing number of the key
	 * 
	 * @return Returns the string representation of the hashing number of the
	 *         key
	 */
	public String getStringHashing() {
		return hashing.toString();
	}

	/**
	 * This method is used for setting the hashing number of the key
	 * 
	 * @param hashing
	 *            . The hashing number of the key
	 */
	public void setHashing(BigInteger hashing) {
		this.hashing = hashing;
	}

	/**
	 * Returns true if the string value and the value that is received as
	 * parameter are equals
	 */
	public boolean equals(Object object) {
		if (object instanceof Key) {
			Key key = (Key) object;

			return key.getValue().equals(value);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		return value + " : " + hashing.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Key clone() {
		Key key = new Key(value);
		return key;
	}

	public static int getKeyLength() {
		return keyLength;
	}

	public static void setKeyLength(int keyLength) {
		Key.keyLength = keyLength;
	}
}