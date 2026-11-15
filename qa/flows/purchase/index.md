# Demoblaze purchase — functional flow diagrams

| TC | Condition | Requirements | Diagram | Automated in |
|---|---|---|---|---|
| — | Purchase journey | PUR-01..06 | [journey.md](journey.md) | — |
| TC-01 | Sign in with a valid test account | PUR-01 | [TC-01.md](TC-01.md) | `04-Full-Flow/onlineShop.feature` — "the customer signs in with the test account" |
| TC-02 | Add two products | PUR-02 | [TC-02.md](TC-02.md) | "the customer adds two products to the cart" |
| TC-03 | Cart lists the products | PUR-03 | [TC-03.md](TC-03.md) | "the cart lists both products and the total is the sum of their prices" |
| TC-04 | Purchase form and confirmation (data-driven) | PUR-04, PUR-05 | [TC-04.md](TC-04.md) | "the customer places the order with …" + "the purchase is confirmed for …" |
| TC-05 | Sign out | PUR-06 | [TC-05.md](TC-05.md) | "the customer can sign out" |
| TC-06 | Receipt date is the purchase date (known bug) | PUR-05 | [TC-06.md](TC-06.md) | "the purchase receipt shows today's date" |
