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
 * The <code>HashingGenerator</code> class is used for generate the hashing of a
 * given value
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public abstract class HashingGenerator {

	/**
	 * Single HashingGenerator instance
	 */
	private static HashingGenerator hashingGenerator;

	/**
	 * Creates a single HashingGenerator instance based in qualifyClass
	 * 
	 * @param qualifyClass
	 *            Class name. Must to be a HashingGenerator
	 */
	public static void load(String qualifyClass) {
		try {
			Class<?> classIn = Class.forName(qualifyClass);
			hashingGenerator = (HashingGenerator) classIn.newInstance();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Error instanciate HashingGenerator", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(
					"Error instanciate HashingGenerator", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(
					"Error instanciate HashingGenerator", e);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(
					"Error instanciate HashingGenerator", e);
		}
	}

	/**
	 * Gets current single HashingGenerator instance. If you have not invoke to
	 * <code>load(String qualifyClass)</code>, this return null
	 * 
	 * @return Current single HashingGenerator instance
	 */
	public static HashingGenerator getInstance() {
		return hashingGenerator;
	}

	/**
	 * This method generate the hashing number of the specified value, with the
	 * specified bite length
	 * 
	 * @param value
	 * @param lengthBits
	 *            . The bite length
	 * @return Returns the hashing number of the value
	 */
	public abstract BigInteger generateHashing(String value, int lengthBits);
}
