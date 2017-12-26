/*
 *  Communication project implement communication point to point and multicast
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
 *  
 *  This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
 *  See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
 *  Any modifications to this file will be lost upon recompilation of the source schema. 
 *  Generated on: 2010.05.13 at 08:03:13 AM COT 
 */

package co.edu.uniquindio.utils.communication.message;

/**
 * The {@code SequenceGenerator} class is responsible for generating the sequence
 * numbers for the messages. This create a sequence number between 1 and Long.MAX_VALUE.
 * When the sequence number is bigger than Long.MAX_VALUE the sequence number starts at 0
 * again. The sequence number increments in one every time that {@code
 * SequenceGenerator.getSequenceNumber()} is called
 * 
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class SequenceGenerator {
	/**
	 * The next sequence number, star at 0
	 */
	private static long number = 0;

	/**
	 * The max number that SequenceGenerator can generate, its value is Long.MAX_VALUE
	 */
	private static long maxNumber = Long.MAX_VALUE;

	/**
	 * This method return the next sequence number
	 * 
	 * @return Returns the next sequence number
	 */
	public synchronized static long getSequenceNumber() {
		if (number > maxNumber) {
			number = 0;
		}

		return ++number;
	}
}