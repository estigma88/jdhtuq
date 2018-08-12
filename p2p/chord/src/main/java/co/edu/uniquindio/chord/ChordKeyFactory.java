/*
 *  Chord project implement of lookup algorithm Chord
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
 *
 */

package co.edu.uniquindio.chord;

import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.overlay.Key;
import co.edu.uniquindio.overlay.KeyFactory;

import java.math.BigInteger;

public class ChordKeyFactory implements KeyFactory {
    private final HashingGenerator hashingGenerator;
    private int keyLength = 160;

    public ChordKeyFactory(HashingGenerator hashingGenerator, int keyLength) {
        this.hashingGenerator = hashingGenerator;
        this.keyLength = keyLength;
    }

    @Override
    public Key newKey(String value) {
        return new ChordKey(value, hashingGenerator.generateHashing(value, keyLength));
    }

    @Override
    public Key newKey(BigInteger hashing) {
        return new ChordKey(hashing);
    }

    @Override
    public int getKeyLength() {
        return keyLength;
    }

    @Override
    public void updateKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }
}
