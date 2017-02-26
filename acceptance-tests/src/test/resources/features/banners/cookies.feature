Feature: As a website owner I would like the user to be show a message regarding cookies so I am not open to litigation
  Scenario Outline: The cookie banner is displayed on all pages
    Given I delete my cookie
    And I go to the <start> page
    Then I can see the cookie banner

  Examples:
  | start   |
  | home    |
  | contact |
  | terms   |


  Scenario: If the user clicks to dismiss the banner this fact is remembered
    Given I delete my cookie
    And I go to the home page
    And I close the cookie banner
    Then A persistent cookie has been added
    When I refresh the page
    Then I do not see the cookie banner
    When I go to the contact page
    Then I do not see the cookie banner
    When I go to the terms page
    Then I do not see the cookie banner