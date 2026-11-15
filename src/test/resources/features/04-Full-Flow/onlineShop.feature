@completeFlow @PUR
Feature: Purchase of devices in the Demoblaze store (PUR-01..PUR-06)
  As a customer I want to buy devices online and get a receipt for my order.
  Flow diagrams: qa/flows/purchase/ · Plan: qa/plans/demoblaze-purchase.md
  The test account comes from the environment (see test-accounts.example.properties), never from this file.

  @completeFlowBP @BuyDevicesHP_DDT @positive @critical @TC-01 @TC-02 @TC-03 @TC-04 @TC-05
  Scenario Outline: A signed-in customer buys two devices and signs out
    Given "<name>" opens the store
    When the customer signs in with the test account
    And the customer adds two products to the cart
    Then the cart lists both products and the total is the sum of their prices
    When the customer places the order with "<name>" "<country>" "<city>" "<card>" "<month>" "<year>"
    Then the purchase is confirmed for "<name>"
    And the customer can sign out

    # Rows are read from the Excel file at build time (gradlew generateFeatures)
    Examples:
      | @externaldata@src/test/resources/data/purchase/dataDemoblaze.xlsx..purchase |

  # Known bug BUG-001: the receipt shows the previous month (e.g. 14/10/2026 on 14 Nov 2026).
  # Not part of the regular run (default tags: not @wip and not @bug). Check whether it is fixed with:
  #   gradlew test -Ptags="@bug"   (it fails until the fix, and is reported as a known bug, not a regression)
  @bug @BUG-001 @TC-06 @negative
  Scenario: The purchase receipt shows the date of the purchase
    Given "Ana" opens the store
    When the customer adds two products to the cart
    And the customer places the order with "Ana Test" "Testland" "Sample City" "4111111111111111" "11" "2030"
    Then the purchase receipt shows today's date
