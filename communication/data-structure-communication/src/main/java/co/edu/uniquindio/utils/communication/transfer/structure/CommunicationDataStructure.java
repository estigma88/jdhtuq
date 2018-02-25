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

package co.edu.uniquindio.utils.communication.transfer.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.uniquindio.utils.communication.Observable;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;

/**
 * The <code>CommunicationDataStructure</code> class management all observer
 * that must to reciever messages from communication
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class CommunicationDataStructure extends Observable<Message> {
	/**
	 * Data struture map
	 */
	private Map<String, Observer<Message>> dataStructure;

	/**
	 * Names observers list
	 */
	private List<String> names;

	/**
	 * Builds a CommunicationDataStructure
	 */
	public CommunicationDataStructure() {
		dataStructure = new HashMap<String, Observer<Message>>();
		this.names = new ArrayList<String>();
	}

	/**
	 * Notify unicast message. Search from data structure of observer with
	 * destination name and send message
	 * 
	 * @param message
	 *            Message to send
	 */
	public void notifyUnicast(Message message) {
		Observer<Message> observer;

		observer = dataStructure.get(message.getMessageDestination());

		if (observer != null) {
			observer.update(message);
		}

		super.notifyMessage(message);
	}

	/**
	 * Notify multicast message. Choose randomly an observer who is not sent and
	 * send message
	 * 
	 * @param message
	 *            Message to send
	 */
	public void notifyMulticast(Message message) {
		String nameSource = message.getMessageSource();
		String nameDestination = nameSource;
		int randomNumber;

		if (dataStructure.size() != 1) {
			while (nameSource.equals(nameDestination)) {
				randomNumber = (int) (Math.random() * dataStructure.size());

				nameDestination = names.get(randomNumber);
			}

			Observer<Message> observer;

			observer = dataStructure.get(nameDestination);

			observer.update(message);
		}

		super.notifyMessage(message);
	}

	/**
	 * Adds observer. If observer name is null, only is adds to list general. If
	 * is not null, is adds to map
	 */
	public void addObserver(Observer<Message> observer) {
		if (observer.getName() != null) {
			names.add(observer.getName());

			dataStructure.put(observer.getName(), observer);
		} else {
			super.addObserver(observer);
		}

	}

	/**
	 * Removes observer. If observer name is null, only is removes to list
	 * general. If is not null, is removes to map
	 */
	public void removeObserver(Observer<Message> observer) {
		if (observer.getName() != null) {
			names.remove(observer.getName());

			dataStructure.remove(observer.getName());
		} else {
			super.removeObserver(observer);
		}

	}
}
