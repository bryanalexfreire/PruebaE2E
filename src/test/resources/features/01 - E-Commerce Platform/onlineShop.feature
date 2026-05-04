@completeFlow
Feature: End-to-end web automation tests with Serenity BDD - Demoblaze

  @completeFlowBP @BuyDevicesHP_DDT
  Scenario Outline: Full purchase flow using external Excel data
    Given "<name>" accesses the shopping portal
    When he login in portal "<username>" "<password>"
    And he adds devices to the cart
    And he sees the products in cart
    When he enters user data "<name>" "<country>" "<city>" "<card>" "<month>" "<year>"
    Then he closes the session

    Examples:
      | @externaldata@src/test/resources/data/purchase/dataDemoblaze.xlsx..purchase | 
