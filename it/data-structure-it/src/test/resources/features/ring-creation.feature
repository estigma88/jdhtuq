Feature: I create a stable ring
  Scenario: I create a stable ring
    Given I set the key length to 16
    And I have the following node's names and hashings:
      | name | hashing |
      | node1 | 922 |
      | node2 | 11198 |
      | node3 | 14654 |
      | node4 | 14018 |
      | node5 | 45027 |
      | node6 | 9703 |
      | node7 | 62942 |
      | node8 | 17323 |
      | node9 | 63438 |
      | node10 | 29382 |
      | node11 | 16431 |
      | node12 | 49027 |
      | node13 | 17336 |
      | node14 | 44055 |
      | node15 | 46322 |
      | node16 | 22357 |
      | node17 | 39795 |
      | node18 | 56773 |
      | node19 | 50136 |
      | node20 | 18085 |
    When I create the Chord ring
    And I wait for stabilizing after 60 seconds
    Then Chord ring is stable with the following successors:
      | name | successor |
      | node1 | node6 |
      | node2 | node4 |
      | node3 | node11 |
      | node4 | node3 |
      | node5 | node15 |
      | node6 | node2 |
      | node7 | node9 |
      | node8 | node13 |
      | node9 | node1 |
      | node10 | node17 |
      | node11 | node8 |
      | node12 | node19 |
      | node13 | node20 |
      | node14 | node5 |
      | node15 | node12 |
      | node16 | node10 |
      | node17 | node14 |
      | node18 | node7 |
      | node19 | node18 |
      | node20 | node16 |