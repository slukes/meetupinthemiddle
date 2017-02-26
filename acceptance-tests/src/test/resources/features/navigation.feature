Feature: The navigation links work

  Scenario Outline: Bottom of screen links work
    Given I go to the <start> page
    And I click on the terms link
    Then I am on the terms page
    And I click on the contact link
    Then I am on the contact page

    Examples:
      | start   |
      | home    |
      | contact |
      | terms   |

  Scenario Outline: Bottom of screen links work
    Given I go to the <start> page
    And I click on the logo
    Then I am on the home page

    Examples:
  | start   |
  | home    |
  | contact |
  | terms   |