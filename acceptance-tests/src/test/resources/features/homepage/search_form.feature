Feature: As a user I would like to be able to specify the name, location and method of transport of each event attendee
  As a user I would like to be able to select driving or public transport as the methods of transport

  Scenario: A form is displayed with fields for name, location and transport method
  The location method is drop down list containing driving and public transport
  Drop down input for POI type is shown on the homepage
  The types of POI available are relevant to both leisure and business

    Given I go to the home page
    Then I can see the add person form elements
    And I can see the POI selector form element
    And I can see the search button

  Scenario: The add person button is disabled until I have added a name and from
    Given I go to the home page
    Then the add person button is disabled
    When I enter a name of Sam
    Then the add person button is disabled
    And I enter a location of Reading
    Then the add person button is enabled

  Scenario: The add person button is disabled until I have added a name and from
  even if I enter them in reverse order
    Given I go to the home page
    Then the add person button is disabled
    When I enter a location of Woking
    Then the add person button is disabled
    When I enter a name of George
    Then the add person button is enabled

  Scenario: Clicking the add person button results in a new row being added to the table
    Given I go to the home page
    When I enter a name of Yao
    And I enter a location of Reading
    When I click on the add person button
    Then a new row is added for Yao from Reading

  Scenario: Clicking the add person button clears the form so I can add another person
    Given I go to the home page
    When I add Alex from Birmingham
    Then the cursor is focused on the name field
    And the form has been cleared

  Scenario: Clicking on the remove person button removes a person from the list and the search button is disabled
    Given I go to the home page
    When I add Steve from Basingstoke
    And I add James from Bracknell
    When I click on the remove person button for James from Bracknell
    Then I can see James from Bracknell is not in the table
    And the search button is disabled

  Scenario: The search button is disabled until I have added two people
    Given I go to the home page
    Then the search button is disabled
    When I add Peter from Croydon
    Then the search button is disabled
    And I add Evelyn from Hampton Hill
    Then the search button is enabled

  Scenario: When I click search I receive visual feedback something is happening
    Given I go to the home page
    When I add Archie from Twickenham
    And I add Richard from Hampton Hill
    And I click on search
    Then the search button says loading

  Scenario: When I try to add a person residing outside the UK they are rejected
    Given I go to the home page
    When I add Oscar from Place Charles de Gaulle, 75008 Paris, France
    Then the location is rejected because it is outside of the UK