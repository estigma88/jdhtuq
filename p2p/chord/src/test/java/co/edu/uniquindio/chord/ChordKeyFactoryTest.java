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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChordKeyFactoryTest {
    @Mock
    private HashingGenerator hashingGenerator;
    private int keyLength = 160;
    private ChordKeyFactory chordKeyFactory;

    @Before
    public void before() {
        chordKeyFactory = new ChordKeyFactory(hashingGenerator, keyLength);
    }

    @Test
    public void newKeyByBigInteger() {
        Key key = chordKeyFactory.newKey(new BigInteger("132"));

        assertThat(key.getValue()).isNull();
        assertThat(key.getHashing()).isEqualTo(new BigInteger("132"));
    }

    @Test
    public void newKeyByString() {
        when(hashingGenerator.generateHashing("key", keyLength)).thenReturn(new BigInteger("132"));

        Key key = chordKeyFactory.newKey("key");

        assertThat(key.getValue()).isEqualTo("key");
        assertThat(key.getHashing()).isEqualTo(new BigInteger("132"));
    }
}