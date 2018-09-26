/*
 *  DHash project implement a storage management
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;


public class FileResource implements Resource, Serializable {
    private final String id;
    private final String path;

    public FileResource(String id, String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Creates a new input stream from the file in the path
     *
     * @return input stream
     * @throws IOException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(path);
    }
}
