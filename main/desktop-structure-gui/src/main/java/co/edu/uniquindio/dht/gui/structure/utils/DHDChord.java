/*
 *  desktop-structure-gui  is a example of the peer to peer application with a desktop ui
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

package co.edu.uniquindio.dht.gui.structure.utils;

import co.edu.uniquindio.chord.hashing.HashingGenerator;
import co.edu.uniquindio.overlay.KeyFactory;
import co.edu.uniquindio.storage.StorageNode;

import java.math.BigInteger;

//TODO Documentar
public class DHDChord implements Comparable<DHDChord> {
    //TODO Documentar
    private StorageNode dHashNode;
    //TODO Documentar
    private int numberNode;
    private HashingGenerator hashingGenerator;
    private KeyFactory keyFactory;

    //TODO Documentar
    public DHDChord(StorageNode dHashNode, int numberNode, HashingGenerator hashingGenerator, KeyFactory keyFactory) {
        this.setDHashNode(dHashNode);
        this.setNumberNode(numberNode);
        this.hashingGenerator = hashingGenerator;
        this.keyFactory = keyFactory;
    }

    //TODO Documentar
    @Override
    public int compareTo(DHDChord o) {
        BigInteger hashing1 = hashingGenerator.generateHashing(getDHashNode().getName(), keyFactory.getKeyLength());
        BigInteger hashing2 = hashingGenerator.generateHashing(o.getDHashNode().getName(), keyFactory.getKeyLength());
        return hashing1.compareTo(hashing2);
    }

    //TODO Documentar
    public void setDHashNode(StorageNode dHashNode) {
        this.dHashNode = dHashNode;
    }

    //TODO Documentar
    public StorageNode getDHashNode() {
        return dHashNode;
    }

    //TODO Documentar
    public void setNumberNode(int numberNode) {
        this.numberNode = numberNode;
    }

    //TODO Documentar
    public int getNumberNode() {
        return numberNode;
    }
}
