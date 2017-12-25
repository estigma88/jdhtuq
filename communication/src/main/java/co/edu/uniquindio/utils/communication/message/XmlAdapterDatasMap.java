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
 * The <code>XmlAdapterDatasMap</code> class implement mapped from
 * <code>HashMap<String, byte[]></code> to <code>Params</code> and vice versa
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public final class XmlAdapterDatasMap extends
		XmlAdapter<Datas, HashMap<String, byte[]>> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	public HashMap<String, byte[]> unmarshal(Datas datas) throws Exception {

		HashMap<String, byte[]> hashMap = new HashMap<String, byte[]>();

		for (Data data : datas.getData()) {
			hashMap.put(data.getName(), data.getBytes());
		}

		return hashMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	public Datas marshal(HashMap<String, byte[]> hashMap) throws Exception {
		Datas datas = new Datas();

		for (Entry<String, byte[]> entry : hashMap.entrySet()) {
			Data data = new Data();
			data.setName(entry.getKey());

			if (entry.getValue() == null) {
				data.setBytes(new byte[] {});
			} else {
				data.setBytes(entry.getValue());
			}

			datas.getData().add(data);
		}

		return datas;
	}

}
