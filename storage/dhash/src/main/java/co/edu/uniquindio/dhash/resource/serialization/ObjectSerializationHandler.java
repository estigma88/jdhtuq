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

package co.edu.uniquindio.dhash.resource.serialization;

import co.edu.uniquindio.storage.resource.Resource;

import java.io.*;

public class ObjectSerializationHandler implements SerializationHandler {
    @Override
    public byte[] encode(Resource resource) {
        try (ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
             ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutput)) {

            objectOutput.writeObject(resource);

            return byteArrayOutput.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error meanwhile serialization", e);
        }
    }

    @Override
    public Resource decode(byte[] bytes) {
        try (ByteArrayInputStream byteArrayOutput = new ByteArrayInputStream(bytes);
             ObjectInputStream objectOutput = new ObjectInputStream(byteArrayOutput)) {

            return (Resource) objectOutput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Error meanwhile deserialization", e);
        }
    }
}
