/*
 *  StorageService project defined all services an storage management
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The <code>DigestGeneratorImp</code> class is used for generate the digest of
 * a given value based in java.security
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class DigestGeneratorImp extends DigestGenerator {
	/**
	 * This is a final variable that represents the MD5 algorithm for creating
	 * hashing
	 */
	private String algorithm = "MD5";

	/**
	 * The buffer size of the array that is used in the process of generate the
	 * hashing
	 */
	private static final int BUFFER_SIZE = 8192;

	/**
	 * This method is used for getting the hashing of a specific file
	 * 
	 * @param file
	 * @return Returns a string hashing representation of the file
	 */
	public String getCheckSum(byte[] bytes) {
		InputStream inputStream = null;
		String output = null;

		try {

			MessageDigest digest = MessageDigest.getInstance(algorithm);
			inputStream = new ByteArrayInputStream(bytes);

			byte[] buffer = new byte[BUFFER_SIZE];
			int read = 0;

			while ((read = inputStream.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			output = bigInt.toString(16);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error reading bytes", e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Error with algorithm", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new IllegalArgumentException("Error reading bytes", e);
			}
		}

		return output;
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
