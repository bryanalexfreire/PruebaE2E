@checkout
Feature: Checkout and Purchase Flow
  @checkout_success @positive @critical
  Scenario Outline: Complete checkout with valid data
    Given "Buyer" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    And he adds devices to the cart
    When he enters user data "<name>" "<country>" "<city>" "<card>" "<month>" "<year>"
    Then the purchase should be successful
    And confirmation message should appear
    Examples:
      | name | country | city | card | month | year |
      | John Doe | USA | New York | 4111111111111111 | 12 | 2025 |
  @checkout_missing_fields @negative
  Scenario: Checkout fails with missing required fields
    Given "Incomplete Buyer" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    And he adds devices to the cart
    When he attempts checkout with missing name field
    Then he should see validation error "Name is required"
  @checkout_invalid_card @negative
  Scenario: Checkout fails with invalid card number
    Given "Fraud Tester" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    And he adds devices to the cart
    When he enters purchase data with invalid card "1234567890123456"
    Then he should see error "Invalid card number"
  @checkout_invalid_expiry @negative
  Scenario: Checkout fails with expired card
    Given "Expiry Tester" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    And he adds devices to the cart
    When he enters purchase data with expired card "<month>" "<year>"
    Then he should see error "Card expired"
    Examples:
      | month | year |
      | 01 | 2020 |
      | 12 | 2019 |
