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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import co.edu.uniquindio.utils.hashing.HashingGenerator;

/**
 * The <code>HashingGeneratorImp</code> class is used for generate the hashing
 * of a given value based in java.security
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class HashingGeneratorImp implements HashingGenerator {
	/**
	 * This is a final variable that represents the SHA-1 algorithm for creating
	 * hashing
	 */
	private String algorithm = "SHA1";

	/**
	 * This method generate the hashing number of the specified value, with the
	 * specified bite length
	 * 
	 * @param value
	 * @param lengthBits
	 *            . The bite length
	 * @return Returns the hashing number of the value
	 */
	public BigInteger generateHashing(String value, int lengthBits) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance(this.algorithm);
			algorithm.update(value.getBytes());

			byte[] digest = algorithm.digest();
			BigInteger hashing = new BigInteger(+1, digest);

			if (lengthBits != digest.length * 8) {
				BigInteger length = new BigInteger("2");

				length = length.pow(lengthBits);

				hashing = hashing.mod(length);
			}

			return hashing;
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Error with algorithm", e);
		}
	}

	/**
	 * Gets hashing algorithm
	 * 
	 * @return Algorithm name
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets hashing algorithm
	 * 
	 * @param algorithm
	 *            Algorithm name
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

}
