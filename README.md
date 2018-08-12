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
[v2.0.1](https://github.com/estigma88/jdhtuq/releases/tag/v2.0.1) 

## How to use
- Clone the repository
- Execute the following command in the root folder
	- For peer to peer simulation: 
	gradlew :main:desktop-structure-gui:bootRun
	- For peer to peer network with sockets: 
	gradlew :main:desktop-network-gui:bootRun

## More info
[More Info](https://github.com/estigma88/jdhtuq/wiki) 

