
Feature: A node left the network
  Background: I create a stable ring
    Given I set the key length to 16
    And I have the following node's names and hashings:
      | name | hashing |
      | 172.16.0.43 | 17266 |
      | 172.16.0.4 | 13193 |
      | 172.16.0.5 | 59488 |
      | 172.16.0.6 | 38116 |
      | 172.16.0.7 | 31925 |
      | 172.16.0.8 | 1833 |
      | 172.16.0.9 | 5441 |
      | 172.16.0.10 | 29236 |
      | 172.16.0.11 | 54023 |
      | 172.16.0.12 | 16020 |
      | 172.16.0.13 | 34177 |
      | 172.16.0.14 | 58901 |
      | 172.16.0.15 | 63701 |
      | 172.16.0.16 | 22313 |
      | 172.16.0.17 | 48279 |
      | 172.16.0.18 | 48681 |
      | 172.16.0.19 | 10276 |
      | 172.16.0.20 | 23517 |
      | 172.16.0.21 | 32272 |
      | 172.16.0.22 | 64158 |
    And I create the Chord ring
    And I wait for stabilizing after 60 seconds
    And I have the resources names and values:
      | name | path |
      | resource1.txt | src/ittest/resources/resources/resource1.txt |
      | resource2.txt | src/ittest/resources/resources/resource2.txt |
      | resource3.txt | src/ittest/resources/resources/resource3.txt |
      | resource4.txt | src/ittest/resources/resources/resource4.txt |
      | resource5.txt | src/ittest/resources/resources/resource5.txt |
      | resource6.txt | src/ittest/resources/resources/resource6.txt |
      | resource7.txt | src/ittest/resources/resources/resource7.txt |
      | resource8.txt | src/ittest/resources/resources/resource8.txt |
      | resource9.txt | src/ittest/resources/resources/resource9.txt |
      | resource10.txt | src/ittest/resources/resources/resource10.txt |
    And I use the "172.16.0.43" as a gateway
    And I put resources into the network

  Scenario: The node "172.16.0.10" left the network
    Given The "172.16.0.10" left the network
    When I wait for stabilizing after 10 seconds
    Then Chord ring is stable with the following successors:
      | 172.16.0.43 | 172.16.0.16 |
      | 172.16.0.4 | 172.16.0.12 |
      | 172.16.0.5 | 172.16.0.15 |
      | 172.16.0.6 | 172.16.0.17 |
      | 172.16.0.7 | 172.16.0.21 |
      | 172.16.0.8 | 172.16.0.9 |
      | 172.16.0.9 | 172.16.0.19 |
      | 172.16.0.11 | 172.16.0.14 |
      | 172.16.0.12 | 172.16.0.43 |
      | 172.16.0.13 | 172.16.0.6 |
      | 172.16.0.14 | 172.16.0.5 |
      | 172.16.0.15 | 172.16.0.22 |
      | 172.16.0.16 | 172.16.0.20 |
      | 172.16.0.17 | 172.16.0.18 |
      | 172.16.0.18 | 172.16.0.11 |
      | 172.16.0.19 | 172.16.0.4 |
      | 172.16.0.20 | 172.16.0.7 |
      | 172.16.0.21 | 172.16.0.13 |
      | 172.16.0.22 | 172.16.0.8 |
    Then The resources are put in the following nodes:
      | resource1.txt | 172.16.0.7,172.16.0.21 |
      | resource2.txt | 172.16.0.7 |
      | resource3.txt | 172.16.0.12,172.16.0.43 |
      | resource4.txt | 172.16.0.20,172.16.0.7 |
      | resource5.txt | 172.16.0.17,172.16.0.18 |
      | resource6.txt | 172.16.0.19,172.16.0.4 |
      | resource7.txt | 172.16.0.7 |
      | resource8.txt | 172.16.0.17,172.16.0.18 |
      | resource9.txt | 172.16.0.11,172.16.0.14 |
      | resource10.txt | 172.16.0.7 |