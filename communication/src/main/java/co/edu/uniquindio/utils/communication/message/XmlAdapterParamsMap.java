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

package co.edu.uniquindio.utils.communication.message;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The <code>XmlAdapterParamsMap</code> class implement mapped from
 * <code>HashMap<String, String></code> to <code>Params</code> and vice versa
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public final class XmlAdapterParamsMap extends
		XmlAdapter<Params, HashMap<String, String>> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	public HashMap<String, String> unmarshal(Params params) throws Exception {

		HashMap<String, String> hashMap = new HashMap<String, String>();

		for (Param param : params.getParam()) {
			if (param.getValue().equals("")) {
				hashMap.put(param.getName(), null);
			} else {
				hashMap.put(param.getName(), param.getValue());
			}

		}

		return hashMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	public Params marshal(HashMap<String, String> hashMap) throws Exception {
		Params params = new Params();

		for (Entry<String, String> entry : hashMap.entrySet()) {
			Param param = new Param();
			param.setName((String) entry.getKey());

			if (entry.getValue() == null) {
				param.setValue("");
			} else {
				param.setValue((String) entry.getValue());
			}

			params.getParam().add(param);
		}

		return params;
	}

}
