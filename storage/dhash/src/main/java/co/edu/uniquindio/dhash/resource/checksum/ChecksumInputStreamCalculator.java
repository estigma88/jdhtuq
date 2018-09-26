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

package co.edu.uniquindio.dhash.resource.checksum;

import co.edu.uniquindio.storage.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumInputStreamCalculator implements ChecksumCalculator {
    private final String algorithm = "MD5";

    @Override
    public String calculate(Resource resource) {
        try (InputStream source = resource.getInputStream()){
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            int count;
            byte[] buffer = new byte[2048];
            while ((count = source.read(buffer)) > 0)
            {
                digest.update(buffer, 0, count);
            }

            byte[] md5sum = digest.digest();

            BigInteger bigInt = new BigInteger(1, md5sum);

            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Error with algorithm", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading input stream", e);
        }
    }
}
