Feature:

  Scenario: The contact page contains a form which allows me to enter an email address, name and message to send
    Given I go to the contact page
    Then I can see an email form

  Scenario:
    Given I go to the contact page
    When I try to send an email with out entering a name
    Then the email doesn't send

  Scenario:
    Given I go to the contact page
    When I try to send an email with out entering an email address
    Then the email doesn't send

  Scenario:
    Given I go to the contact page
    When I try to send an email with out entering a message
    Then the email doesn't send

  Scenario:
    Given I go to the contact page
    When I try to send an email with an invalid email address
    Then the email doesn't send

  Scenario:
    Given I go to the contact page
    And I disable form validation
    When I try to send an email with out entering a message
    Then I see an error message for message

  Scenario:
    Given I go to the contact page
    And I disable form validation
    When I try to send an email with out entering an email address
    Then I see an error message for email

  Scenario:
    Given I go to the contact page
    And I disable form validation
    When I try to send an email with an invalid email address
    Then I see an error message for email

  Scenario:
    Given I go to the contact page
    And I disable form validation
    When I try to send an email with out entering a name
    Then I see an error message for name

  Scenario:
    Given I go to the contact page
    When I send a random message
    Then I am on the home page
    And I can see a successfully sent message

  Scenario:
    Given I go to the contact page
    When I send a random message with send copy selected
    Then I am on the home page
    And I can see a successfully sent message
    And I have received a copy of the email