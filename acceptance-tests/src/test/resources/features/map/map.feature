Feature: As a user I would like to see the midpoint and each of the event attendees' location on a map
  so I can visualise the result; As a user I would like the POI results to be displayed on the map as pins so I can visualise where they are in relation to the midpoint

  Scenario: A map is displayed on the homepage
    Given I go to the home page
    Then I can see a map

  Scenario: A map is displayed on the results page
    Given I perform a search for
      | Joyce | GU22 0SH | DRIVING |
      | Sam   | RG1 8DE  | DRIVING |
    And I wait for the results to load
    Then I can see a map

  Scenario: A coloured pin is displayed for each attendee
            I can identify which attendee relates to which pin
    Given I go to the home page
    When I add Brian from Mayford
    And I add Katie from Goldsworth Park, Woking
    Then I can see pins on the map for Brian, Katie

    Scenario: TODO THIS
      Given I perform a search for
        | Padraic | RG1 8DE | DRIVING |
        | John    | Reading  | PUBLIC |
      And I wait for the results to load
      Then I can see pins on the map for 1, 2, 3, 4, 5



