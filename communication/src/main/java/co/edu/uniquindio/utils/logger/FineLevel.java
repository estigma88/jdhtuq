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
 */
package co.edu.uniquindio.utils.logger;

import org.apache.log4j.Level;

/**
 * The <code>FineLevel</code> class defined nivel fine in logger
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public final class FineLevel extends Level {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Number value
	 */
	static public final int FINE_INT = Level.DEBUG_INT - 2500;
	/**
	 * String value
	 */
	private static String FINE_STR = "FINE";
	/**
	 * Fine level
	 */
	public static final FineLevel FINE = new FineLevel(FINE_INT, FINE_STR, 7);

	/**
	 * Creates a FineLevel
	 * 
	 * @param level
	 *            Number level
	 * @param strLevel
	 *            String level
	 * @param syslogEquiv
	 */
	protected FineLevel(int level, String strLevel, int syslogEquiv) {
		super(level, strLevel, syslogEquiv);
	}

	/**
	 * Convert the String argument to a level. If the conversion fails then this
	 * method returns {@link #FINE}.
	 */
	public static Level toLevel(String sArg) {
		return (Level) toLevel(sArg, FineLevel.FINE);
	}

	/**
	 * Convert the String argument to a level. If the conversion fails, return
	 * the level specified by the second argument, i.e. defaultValue.
	 */
	public static Level toLevel(String sArg, Level defaultValue) {
		if (sArg == null) {
			return defaultValue;
		}
		String stringVal = sArg.toUpperCase();
		if (stringVal.equals(FINE_STR)) {
			return FineLevel.FINE;
		}
		return Level.toLevel(sArg, (Level) defaultValue);
	}

	/**
	 * Convert an integer passed as argument to a level. If the conversion
	 * fails, then this method returns {@link #DEBUG}.
	 * */
	public static Level toLevel(int i) throws IllegalArgumentException {
		if (i == FINE_INT) {
			return FineLevel.FINE;
		} else {
			return Level.toLevel(i);
		}
	}

}
