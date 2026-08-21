@authentication
Feature: Authentication Tests
  @login_success @positive
  Scenario: Successful login with valid credentials
    Given "Buyer" accesses the shopping portal
    When he login in portal "shelbycooper1990" "Password123!"
    Then he should be logged in
  @login_invalid_credentials @negative
  Scenario Outline: Login fails with invalid credentials
    Given "Invalid User" accesses the shopping portal
    When he attempts login with invalid credentials "<username>" "<password>"
    Then he should see an error message "<error>"
    Examples:
      | username | password | error |
      | invalid@test.com | wrongpass | User does not exist |
      | "" | anypassword | Please enter username |
      | anyuser | "" | Please enter password |
  @login_locked_account @negative
  Scenario: Login denied for non-existent user
    Given "Blocked User" accesses the shopping portal
    When he attempts login with credentials "nonexistent_user_12345" "SomePassword123"
    Then he should see error "User does not exist."
