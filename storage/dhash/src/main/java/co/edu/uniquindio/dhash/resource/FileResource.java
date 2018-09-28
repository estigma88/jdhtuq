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

import lombok.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileResource extends BasicResource {
    private String path;

    @Builder(builderMethodName = "withPath", builderClassName = "WithPathBuilder")
    public FileResource(String id, String path) throws FileNotFoundException {
        super(id, new FileInputStream(path));
        this.path = path;
    }

    @Builder(builderMethodName = "withInputStream", builderClassName = "WithInputStreamBuilder")
    public FileResource(String id, InputStream inputStream){
        super(id, inputStream);
        this.path = null;
    }

}
