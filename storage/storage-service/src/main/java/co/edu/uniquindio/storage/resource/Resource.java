/*
 *  StorageService project defined all services an storage management
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

package co.edu.uniquindio.storage.resource;

import java.io.Closeable;
import java.io.InputStream;

/**
 * Class to handle a Resource
 */
public interface Resource extends Closeable{
    /**
     * Gets id of resource
     *
     * @return id of resource
     */
    String getId();

    /**
     * Get an input stream that represents the resource content
     *
     * @return resource content
     */
    InputStream getInputStream();

    /**
     * Get the resource size in bytes
     *
     * @return bytes amount
     */
    Long getSize();

    /**
     * Get the checksum of the resource
     *
     * @return checkSum
     */
    String getCheckSum();
}
