Feature:

  Scenario Outline: I see suggestions for various types of location
    Given I go to the home page
    When I enter a location of <term>
    Then I see a suggestion of <suggestion>

    Examples:
      | term           | suggestion               |
      | Read           | Reading                  |
      | Wok            | Woking                   |
      | rg1 8de        | Reading RG1 8DE          |
      | 7 Lynmouth Roa | 7 Lynmouth Road, Reading |
      | Cavers         | Caversham                |
      | Berk           | Berkshire                |
