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
import co.edu.uniquindio.storage.StorageException;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import co.edu.uniquindio.storage.resource.Resource;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Set;

import static co.edu.uniquindio.dhash.resource.checksum.ChecksumInputStreamCalculator.CHECKSUM_ALGORITHM;

public class FileResourceManager implements ResourceManager {
    private final String directory;
    private final String name;
    private final Set<String> keys;
    private final Integer bufferSize;

    FileResourceManager(String directory, String name, Set<String> keys, Integer bufferSize) {
        this.directory = directory;
        this.name = name;
        this.keys = keys;
        this.bufferSize = bufferSize;
    }

    @Override
    public void save(Resource resource, ProgressStatus progressStatus) throws StorageException {
        try {
            MessageDigest digest = MessageDigest.getInstance(CHECKSUM_ALGORITHM);

            Path directory = Paths.get(this.directory, name, resource.getId());

            Files.createDirectories(directory);

            Path file = directory.resolve(resource.getId());

            InputStream source = resource.getInputStream();
            OutputStream destination = new FileOutputStream(file.toFile());

            int count;
            long sent = 0L;
            byte[] buffer = new byte[bufferSize];

            progressStatus.status("resource-persist", sent, resource.getSize());
            progressStatus.status("digest-persist", sent, resource.getSize());

            while ((count = source.read(buffer)) > 0) {
                sent += count;

                destination.write(buffer, 0, count);

                progressStatus.status("resource-persist", sent, resource.getSize());

                digest.update(buffer, 0, count);

                progressStatus.status("digest-persist", sent, resource.getSize());
            }

            destination.close();

            String checksum = DatatypeConverter.printHexBinary(digest.digest());

            if (!checksum.equals(resource.getCheckSum())) {
                Files.delete(file);
                Files.delete(directory);

                throw new StorageException("Checksum is not valid, " + checksum + " != " + resource.getCheckSum());
            }

            Path checkSumPath = directory.resolve(resource.getId() + "." + CHECKSUM_ALGORITHM);

            Files.write(checkSumPath, checksum.getBytes());

            keys.add(resource.getId());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new StorageException("Error reading file", e);
        }
    }

    @Override
    public void deleteAll() throws StorageException {
        try {
            Path directory = Paths.get(this.directory, name);

            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            keys.clear();
        } catch (IOException e) {
            throw new StorageException("Error reading file", e);
        }
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
    public Resource find(String key) throws StorageException {
        if (hasResource(key)) {
            Path directory = Paths.get(this.directory, name, key);
            Path file = directory.resolve(key);
            Path checkSumFile = directory.resolve(key + "." + CHECKSUM_ALGORITHM);

            try {
                String checkSum = Files.lines(checkSumFile)
                        .findFirst()
                        .orElseThrow(() -> new StorageException("Checksum doesn't find for " + key));

                return FileResource.withInputStream()
                        .id(key)
                        .inputStream(Files.newInputStream(file))
                        .checkSum(checkSum)
                        .size(Files.size(file))
                        .build();
            } catch (IOException e) {
                throw new StorageException("Error reading file", e);
            }
        } else {
            return null;
        }
    }
}
