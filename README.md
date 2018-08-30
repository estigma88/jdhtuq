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

## Download last examples versions

[v2.0.1](https://github.com/estigma88/jdhtuq/releases/tag/v2.0.1) 

Data structure and network examples of use.
- Execute with double click or
- Using the console:
	`java -jar <jdhash-version>.jar`
	
To get more information about how to use the examples, please refer to 
[More Info](https://github.com/estigma88/jdhtuq/wiki) page

## How to use
### From source code
- Clone the repository
- Execute the following command in the root folder
	- For peer to peer simulation: 
	`gradlew :main:desktop-structure-gui:bootRun`
	- For peer to peer network with sockets: 
	`gradlew :main:desktop-network-gui:bootRun`

### Using layers as dependencies
Add the following dependencies to your project to use Chord, DHash and Communication layers in your own project.

**Note**: This dependencies are Spring Boot Starters, so, your project must use Spring Boot

#### Gradle example 

- Chord: `compile group: 'com.github.estigma88', name: 'jdhtuq-chord-spring-boot-starter', version: '2.0.1'`
- DHash: `compile group: 'com.github.estigma88', name: 'jdhtuq-dhash-spring-boot-starter', version: '2.0.1'`
- Communication: Use one of the following implementations
    - Data Structure: `compile group: 'com.github.estigma88', name: 'jdhtuq-data-structure-communication-spring-boot-starter', version: '2.0.0'`
    - Sockets: `compile group: 'com.github.estigma88', name: 'jdhtuq-socket-communication-spring-boot-starter', version: '2.1.0'`

## More info
[More Info](https://github.com/estigma88/jdhtuq/wiki) 

