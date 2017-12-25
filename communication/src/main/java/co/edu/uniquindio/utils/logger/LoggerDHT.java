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

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * The <code>LoggerDHT</code> class is an logger defined for project
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class LoggerDHT extends org.apache.log4j.Logger {

	/**
	 * Logger factory
	 */
	private static LoggerFactory loggerFactory = new LoggerFactory();
	/**
	 * Is load propertis yet
	 */
	private static boolean load;
	/**
	 * Path for properties fiel
	 */
	private static final String PROPERTIES = "resources/communication_properties/logger.xml";
	/**
	 * Class name
	 */
	private static String FQCN = LoggerDHT.class.getName();

	/**
	 * Builds logger by name
	 * 
	 * @param name
	 *            Name
	 */
	protected LoggerDHT(String name) {
		super(name);
	}

	/**
	 * Gets logger by class
	 * 
	 * @param clazz
	 *            Class
	 * @return LoggerDHT
	 */
	public static LoggerDHT getLogger(Class clazz) {
		loadProperties();
		return (LoggerDHT) Logger.getLogger(clazz.getName(), loggerFactory);
	}

	/**
	 * Load properties for logger from logger.xml
	 * 
	 * @throws FactoryConfigurationError
	 */
	private static void loadProperties() throws FactoryConfigurationError {
		if (!load) {
			try {
				CodeSource codeSource = LoggerDHT.class.getProtectionDomain()
						.getCodeSource();
				File jarFile;

				jarFile = new File(codeSource.getLocation().toURI().getPath());

				File jarDir = jarFile.getParentFile();

				File propFile = null;
				if (jarDir != null && jarDir.isDirectory()) {
					propFile = new File(jarDir, PROPERTIES);
				}

				DOMConfigurator.configure(propFile.getPath());

				load = true;

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Defined level fine
	 * 
	 * @param msg
	 *            Message to registred
	 */
	public void fine(Object msg) {
		fine(msg, (Throwable) null);
	}

	/**
	 * Defined level fine with Throwable
	 * 
	 * @param msg
	 *            Message to registred
	 * @param t
	 *            Throwable to registred
	 */
	public void fine(Object msg, Throwable t) {
		log(FQCN, FineLevel.FINE, msg, t);
	}

	/**
	 * Defined level finest
	 * 
	 * @param msg
	 *            Message to registred
	 */
	public void finest(Object msg) {
		finest(msg, null);
	}

	/**
	 * Defined level finest with Throwable
	 * 
	 * @param msg
	 *            Message to registred
	 * @param t
	 *            Throwable to registred
	 */
	public void finest(Object msg, Throwable t) {
		log(FQCN, FinestLevel.FINEST, msg, t);
	}
}
