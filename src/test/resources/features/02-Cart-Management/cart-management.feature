@cart_management
Feature: Shopping Cart Management
  @add_products_success @positive
  Scenario: Add multiple products to cart dynamically
    Given "Shopper" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    When he adds products at indices "1,2,3"
    Then the cart should contain "3" products
    And the cart total should be calculated correctly
  @remove_product @positive
  Scenario: Remove product from cart
    Given "Shopper" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    When he adds devices to the cart
    And he removes the first product from cart
    Then the cart should contain "1" product
  @view_empty_cart @negative
  Scenario: View empty cart shows 0 items
    Given "Empty Cart User" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    When he views the cart without adding products
    Then the cart should display "0" items
  @cart_persistence @positive
  Scenario: Cart persists across page navigation
    Given "Persistent Shopper" accesses the shopping portal
    And he login in portal "shelbycooper1990" "Password123!"
    And he adds devices to the cart
    When he navigates back to home
    And he views his cart
    Then the products should still be in cart
