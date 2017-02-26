Feature: As a user I want to receive instructions on the homepage so I don't leave the page if I get stuck
  Scenario: When I hover over the ? on the page I see a tool tip containing some instructions
    Given I go to the home page
    Then I can see a ?
    When I hover over the ?
    Then I can see a tooltip