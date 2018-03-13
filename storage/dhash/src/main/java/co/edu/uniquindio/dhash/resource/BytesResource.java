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

package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.storage.resource.Resource;

import java.io.Serializable;

public class BytesResource implements Resource, Serializable {

    private final String id;
    private final byte[] bytes;

    public BytesResource(String id, byte[] bytes) {
        this.id = id;
        this.bytes = bytes;
    }

    @Override
    public String getId() {
        return id;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
