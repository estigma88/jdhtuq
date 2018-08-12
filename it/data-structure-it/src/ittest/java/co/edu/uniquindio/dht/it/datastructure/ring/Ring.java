/*
 *  Data structure it project has a integration tests for data structure communication
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

package co.edu.uniquindio.dht.it.datastructure.ring;

import co.edu.uniquindio.dhash.node.DHashNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Ring {
    private final Map<String, DHashNode> nodes;

    public Ring() {
        this.nodes = new HashMap<>();
    }

    public DHashNode getNode(String name){
        return nodes.get(name);
    }

    public void add(String name, DHashNode dHashNode){
        nodes.put(name, dHashNode);
    }

    public Set<String> getNodeNames() {
        return nodes.keySet();
    }
}

