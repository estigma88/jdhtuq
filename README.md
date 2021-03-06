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

[![Github Releases (by Asset)](https://img.shields.io/github/downloads/estigma88/jdhtuq/desktop-structure-gui-v2.0.4/desktop-structure-gui-2.0.4.jar.svg)](https://github.com/estigma88/jdhtuq/releases/download/desktop-structure-gui-v2.0.4/desktop-structure-gui-2.0.4.jar)     [![Github Releases (by Asset)](https://img.shields.io/github/downloads/estigma88/jdhtuq/desktop-network-gui-v2.0.6/desktop-network-gui-2.0.6.jar.svg)](https://github.com/estigma88/jdhtuq/releases/download/desktop-network-gui-v2.0.6/desktop-network-gui-2.0.6.jar)     [![Github Releases (by Asset)](https://img.shields.io/github/downloads/estigma88/jdhtuq/standalone-network-v2.0.6/standalone-network-2.0.6.jar.svg)](https://github.com/estigma88/jdhtuq/releases/download/standalone-network-v2.0.6/standalone-network-2.0.6.jar)

Data structure and network applications.
- Execute with double click or
- Using the console:
	`java -jar <jdhash-version>.jar`
	
To get more information about how to use the examples, please refer to 
[More Info](https://github.com/estigma88/jdhtuq/wiki) page

## How to use
### From source code
- Clone the repository
- Execute the following command in the root folder
	- For desktop ui peer to peer simulation: 
	`gradlew :main:desktop-structure-gui:bootRun`
	- For desktop ui peer to peer network with sockets: 
	`gradlew :main:desktop-network-gui:bootRun`
	- For standalone peer to peer network with sockets: 
	`gradlew :main:standalone-network:bootRun`

### Using layers as dependencies
Add the following dependencies to your project to use Chord, DHash and Communication layers in your own project.

**Note**: This dependencies are Spring Boot Starters, so, your project must use Spring Boot

- Chord: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-chord-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-chord-spring-boot-starter)
- DHash: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-dhash-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-dhash-spring-boot-starter)
- Communication: Use one of the following implementations
    - Data Structure: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-data-structure-communication-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-data-structure-communication-spring-boot-starter)
    - Sockets: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-socket-communication-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.estigma88/jdhtuq-socket-communication-spring-boot-starter)


## More info
[More Info](https://github.com/estigma88/jdhtuq/wiki) 

