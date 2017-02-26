Feature: Results page

  Scenario: As a user I would like to be shown the postcode of the middle point so I can make plans in that area
    Given I perform a search for
      | Andi | Gosport     | DRIVING |
      | Alex | Southampton | DRIVING |
    Then I can see the postcode and town on the page

  Scenario:
    Given I perform a search for
      | Cheeseman | Cemetery Junction, Reading | DRIVING |
      | Sam       | RG1 8DE                    | DRIVING |
      | John      | Reading Station            | PUBLIC  |
      | Yao       | Reading Uni                | PUBLIC  |
    When I wait for the results to load
    Then I can see a table with the name of each POI

  Scenario:
    Given I perform a search for
      | Martin | Halesowen     | DRIVING |
      | Jo     | Weoley Castle | DRIVING |
      | Sam    | Moseley       | PUBLIC  |
    When I wait for the results to load
    And I click on POI 1 in the table
    Then I see an info box
    When I click on POI 2 in the table
    Then I see an info box

  Scenario:
    Given I perform a search for
      | Yao | Reading Station | DRIVING |
      | Sam | Reading         | DRIVING |
    When I wait for the results to load
    And I click on the map pin labeled 1
    Then I see an info box
    When I click on the map pin labeled 3
    Then I see an info box