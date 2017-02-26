Feature: The transport modes of users are taken into account by the algorithm

  Scenario: Searching for the same locations using different modes of transport gives different results
    Given I perform a search for
      | Jess   | Bournmouth | DRIVING |
      | Rachel | Bath       | PUBLIC  |
    When I make a note of the midpoint
    And I perform a search for
      | Jess   | Bournmouth | DRIVING |
      | Rachel | Bath       | DRIVING |
    And I make a note of the midpoint
    Then the midpoints are different