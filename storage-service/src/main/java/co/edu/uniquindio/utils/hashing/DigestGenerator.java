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

/**
 * The <code>DigestGenerator</code> class is used for generate the digest
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public abstract class DigestGenerator {

	/**
	 * Single DigestGenerator instance
	 */
	private static DigestGenerator digestGenerator;

	/**
	 * Creates a single DigestGenerator instance based in qualifyClass
	 * 
	 * @param qualifyClass
	 *            Class name. Must to be a DigestGenerator
	 */
	public static void load(String qualifyClass) {
		try {
			Class<?> classIn = Class.forName(qualifyClass);
			digestGenerator = (DigestGenerator) classIn.newInstance();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Error instanciate DigestGenerator", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(
					"Error instanciate DigestGenerator", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(
					"Error instanciate DigestGenerator", e);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(
					"Error instanciate DigestGenerator", e);
		}
	}

	/**
	 * Gets current single DigestGenerator instance. If you have not invoke to
	 * <code>load(String qualifyClass)</code>, this return null
	 * 
	 * @return Current single DigestGenerator instance
	 */
	public static DigestGenerator getInstance() {
		return digestGenerator;
	}

	/**
	 * This method is used for getting the digest of a specific bytes
	 * 
	 * @param bytes
	 *            Bytes to get digest
	 * @return Returns a string hashing representation of the file
	 */
	public abstract String getCheckSum(byte[] bytes);
}
