@enable
Feature: I create a stable ring
  Scenario: I create a stable ring
    Given I set the key length to 16
    And I have the following node's names and hashings:
      | name | hashing |
      | 127.0.0.1 | 18393 |
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
    When I create the Chord ring
    And I wait for stabilizing after 60 seconds
    Then Chord ring is stable with the following successors:
      | name | successor |
      | 127.0.0.1 | 172.16.0.16 |
      | 172.16.0.4 | 172.16.0.12 |
      | 172.16.0.5 | 172.16.0.15 |
      | 172.16.0.6 | 172.16.0.17 |
      | 172.16.0.7 | 172.16.0.21 |
      | 172.16.0.8 | 172.16.0.9 |
      | 172.16.0.9 | 172.16.0.19 |
      | 172.16.0.10 | 172.16.0.7 |
      | 172.16.0.11 | 172.16.0.14 |
      | 172.16.0.12 | 127.0.0.1 |
      | 172.16.0.13 | 172.16.0.6 |
      | 172.16.0.14 | 172.16.0.5 |
      | 172.16.0.15 | 172.16.0.22 |
      | 172.16.0.16 | 172.16.0.20 |
      | 172.16.0.17 | 172.16.0.18 |
      | 172.16.0.18 | 172.16.0.11 |
      | 172.16.0.19 | 172.16.0.4 |
      | 172.16.0.20 | 172.16.0.10 |
      | 172.16.0.21 | 172.16.0.13 |
      | 172.16.0.22 | 172.16.0.8 |