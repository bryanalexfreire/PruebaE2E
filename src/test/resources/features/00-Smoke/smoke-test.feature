@smoke @sanity
Feature: Smoke Test - Basic Access
  Verify the application is accessible and basic navigation works
  @smoke_access
  Scenario: User can access the Demoblaze home page
    Given "Smoke User" accesses the shopping portal
    Then the home page should be visible
