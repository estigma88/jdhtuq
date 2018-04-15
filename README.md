# jDHTUQ for peer-to-peer DHT networking
jDHTUQ is a peer-to-peer DHT system based on Chord algorithm, but built to generalize the implementation of peer-to-peer DHT systems. It have two fundamental services, put and get of a resource.

**jDHTUQ is using:**
- Reusable api for lookup and storage
- One implementation of Chord algorithm
- One implementation of a resources management (DHash)
- Spring Boot Starters for easy setup
- Independent communication module configurable
- Peer to peer communication simulation using a data structure
- Peer to peer network communication using sockets

## Download last versions
[v2.0.0](https://github.com/estigma88/jdhtuq/releases/tag/v2.0.0) 

## How to use
- Clone the repository
- Execute the following command in the root folder
	- For peer to peer simulation: 
	gradlew :main:desktop-structure-gui:bootRun
	- For peer to peer network with sockets: 
	gradlew :main:desktop-network-gui:bootRun

## More info
[More info](Home) 
	
## Licence
DHTUQ is a peer-to-peer DHT system based on Chord algorithm, but built to generalize the implementation of peer-to-peer DHT systems.

Copyright (C) 2010  Daniel Pelaez.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.



