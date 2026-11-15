# Demoblaze — purchase flow requirements

Source: E2E automation exercise on https://www.demoblaze.com/ — "functional automated test (E2E) of a purchase flow".

| ID | Requirement |
|---|---|
| PUR-01 | A registered customer can sign in with a valid username and password; the navigation bar then shows "Welcome <username>". |
| PUR-02 | The customer can add two products to the cart; each addition is acknowledged with the browser alert "Product added". |
| PUR-03 | The cart lists every added product with its title and price, and the total equals the sum of the prices. |
| PUR-04 | The customer completes the purchase form (Name, Country, City, Credit card, Month, Year). Name and Credit card are mandatory: "Please fill out Name and Creditcard." |
| PUR-05 | Purchasing shows "Thank you for your purchase!" with the order Id, Amount (= cart total), Card Number, Name and Date. |
| PUR-06 | The customer can sign out; the navigation bar shows "Log in" again. |
