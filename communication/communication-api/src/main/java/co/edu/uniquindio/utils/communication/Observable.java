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


package co.edu.uniquindio.utils.communication;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code Observable} class defines the basic methods for implementing the
 * observer pattern with the interface {@link Observer}
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see Observer
 */
public class Observable<E> {
	/**
	 * The list of observers that observes this observable
	 */
	private List<Observer<E>> listeners;

	/**
	 * The constructor of the class. Creates an instance of the class
	 * Observable.
	 */
	public Observable() {
		listeners = new LinkedList<Observer<E>>();
	}

	/**
	 * Add an observer to the list of observers
	 * 
	 * @param s
	 *            . The observer that will observes the observable
	 */
	public void addObserver(Observer<E> observer) {
		listeners.add(observer);
	}

	/**
	 * Remove an observer of the list of observers
	 * 
	 * @param s
	 *            . The observer that will be removed
	 */
	public void removeObserver(Observer<E> observer) {
		listeners.remove(observer);
	}

	/**
	 * Remove an observer of the list of observers
	 * 
	 * @param s
	 *            . The name of observer that will be removed
	 */
	public void removeObserver(String name) {
		for (Observer<E> observer : listeners) {
			if (observer.getName() != null && observer.getName().equals(name)) {
				listeners.remove(observer);
				break;
			}
		}
	}

	/**
	 * This method is used for notifying to the observers an event
	 * 
	 * @param e
	 *            . The event that will be notified to the observers
	 */
	public void notifyMessage(E e) {
		for (Observer<E> listener : listeners) {
			listener.update(e);
		}
	}

}