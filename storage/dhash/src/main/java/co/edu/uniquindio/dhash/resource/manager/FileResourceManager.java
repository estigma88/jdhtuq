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

package co.edu.uniquindio.dhash.resource.manager;

import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.storage.resource.Resource;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class FileResourceManager implements ResourceManager {
    private final String directory;
    private final String name;
    private final Set<String> keys;

    public FileResourceManager(String directory, String name, Set<String> keys) {
        this.directory = directory;
        this.name = name;
        this.keys = keys;
    }

    @Override
    public void save(Resource resource) {
        FileOutputStream fileOutputStream;
        StringBuilder directoryPath;
        File directoryFile;

        try {
            directoryPath = new StringBuilder(directory);
            directoryPath.append(name);
            directoryPath.append("/");

            directoryFile = new File(directoryPath.toString());
            directoryFile.mkdirs();

            File file = new File(directoryFile, resource.getId());

            fileOutputStream = new FileOutputStream(file);
            InputStream source = resource.getInputStream();

            int count;
            byte[] buffer = new byte[2048];
            while ((count = source.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }

            fileOutputStream.close();

            keys.add(resource.getId());
        } catch (IOException e) {
            throw new IllegalStateException("Error reading file", e);
        }
    }

    @Override
    public void deleteAll() {
        StringBuilder directoryPath = new StringBuilder(directory);
        directoryPath.append(name);
        directoryPath.append("/");

        File directory = new File(directoryPath.toString());

        if (directory.exists()) {
            Arrays.stream(Optional.ofNullable(directory.listFiles()).orElse(new File[0])).forEach(File::delete);

            directory.delete();
        }

        keys.clear();
    }

    @Override
    public boolean hasResource(String key) {
        return keys.contains(key);
    }

    @Override
    public Set<String> getAllKeys() {
        return keys;
    }

    @Override
    public Resource find(String key) {
        if (hasResource(key)) {
            StringBuilder directoryPath = new StringBuilder(directory);
            directoryPath.append(name);
            directoryPath.append("/");
            directoryPath.append(key);

            try {
                return FileResource.withPath()
                        .id(key)
                        .path(directoryPath.toString())
                        .build();
            } catch (IOException e) {
                throw new IllegalStateException("Error reading file", e);
            }
        } else {
            return null;
        }
    }
}
