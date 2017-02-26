Feature: As a user without javascript enabled I would like to be shown messaging to
  explain that the site will not work, so I am not confused

  Scenario Outline: A banner is displayed when I visit the site with out having javascript enabled.
    Given I disable JavaScript
    When I go to the <start> page
    Then I can see the no JavaScript banner
    When I reenable JavaScript

    Examples:
      | start   |
      | home    |
      | contact |
      | terms   |