Feature: Realize a script test automatized (Prueba E2E) simulating a flow of buys at Demoblaze

  @BuyDevicesHP
  Scenario Outline: Doing buys in demoblaze
    #User add two product to cart and visualice it.
    Given Bryan accesses the shopping portal
    When he signup in portal <username> <password>
    Then he login in portal <username> <password>
    And he adds devices to the cart
    And he sees the products in cart


    #User complete the buy's form and finish it.
    When he enters user data <name> <country> <city> <card> <month> <year>

    Then  he returns main page

    Examples:
      | name  | country | city  | card   | month  | year | username  | password |
      | Bryan | Ecuador | Quito | 123546 | Agosto | 2023 | bryanfreire123 | prueba123 |
