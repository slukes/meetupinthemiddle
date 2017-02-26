Feature: As a user I would like to be able to share my results on social media
  so that my friends can see where we plan to meet.  As a webiste owner I would like
  to raise awareness of the site (PARTIALLY IMPLEMENTED)

  Scenario: I can see social media buttons on the home page and results page
    Given I go to the home page
    Then I can see social media buttons
    When I perform a search for
      | Matteo | Eldon Road    | DRIVING |
      | Tom    | Silver Street | PUBLIC  |
    Then I can see social media buttons

Scenario: Clicking on the facebook button displays a share window
  Given I go to the home page
  When I click on the facebook button
  Then I can see a facebook share window

  Scenario: Clicking on the twitter button displays a share window
    Given I go to the home page
    When I click on the twitter button
    Then I can see a twitter share window