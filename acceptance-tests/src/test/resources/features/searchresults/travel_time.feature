Feature: As a user I would like to be told the travelling distance from each attendees' location to the midpoint
  so I will believe it really is the midpoint

  Scenario: The length of time is displayed next to each users name
    Given I perform a search for
      | John | Sketty    | DRIVING |
      | Mike | Pontadawe | PUBLIC  |
    When I wait for the results to load
    Then the length of time is displayed next to each user
