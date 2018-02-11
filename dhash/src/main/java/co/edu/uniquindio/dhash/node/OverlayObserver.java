/*
 *  DHash project implement a storage management 
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

package co.edu.uniquindio.dhash.node;

import co.edu.uniquindio.utils.communication.Observer;
import org.apache.log4j.Logger;

/**
 * The <code>OverlayObserver</code> abstract class have methods for instanciate
 * determined class of type OverlayObserver. The object result is adds in the
 * overlay layer for listener message like leave
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public abstract class OverlayObserver implements Observer<Object> {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger
			.getLogger(OverlayObserver.class);

	/**
	 * DHashNode reference
	 */
	protected DHashNode dHashNode;

	/**
	 * Creates OverlayObserver from quelify class name
	 * 
	 * @param qualifyClass
	 *            class name to instanciate
	 * @return Instance of OverlayObserver
	 */
	public static OverlayObserver getInstance(String qualifyClass) {

		OverlayObserver overlayObserver = null;
		try {
			Class<?> classIn = Class.forName(qualifyClass);
			overlayObserver = (OverlayObserver) classIn.newInstance();
		} catch (ClassNotFoundException e) {
			logger.error("Error to instanciate", e);
		} catch (InstantiationException e) {
			logger.error("Error to instanciate", e);
		} catch (IllegalAccessException e) {
			logger.error("Error to instanciate", e);
		} catch (ClassCastException e) {
			logger.error("Error to instanciate", e);
		}

		return overlayObserver;
	}

	/**
	 * Observer name
	 */
	public String getName() {
		return null;
	}

	/**
	 * Sets dhash node reference
	 * 
	 * @param dHashNode
	 *            Dhash node reference
	 */
	public void setDHashNode(DHashNode dHashNode) {
		this.dHashNode = dHashNode;
	}
}
