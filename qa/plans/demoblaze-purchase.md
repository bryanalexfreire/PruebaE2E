# Demoblaze purchase — Login, Store, Cart, Place order

Roles: registered customer (test accounts from the feature data) · Requirements: PUR-01..PUR-06

## Elements and actions
| Screen | Elements | Actions |
|---|---|---|
| Home / Login modal | "Log in", Username, Password, "Log in" button, "Welcome <user>" | sign in, sign out |
| Store | categories, product cards (title, price) | open a product |
| Product | title, price, "Add to cart" | add to cart → alert "Product added" |
| Cart | rows (Title, Price), Total, "Place Order" | review, open the order form |
| Place order (modal) | Name, Country, City, Credit card, Month, Year, Purchase | purchase |
| Confirmation | "Thank you for your purchase!", Id, Amount, Card Number, Name, Date, OK | close |

## Test conditions
| ID | Condition | Technique | Priority | Requirement | Automate? | Flow |
|---|---|---|---|---|---|---|
| TC-01 | Customer signs in with a valid test account | Scenario testing | High | PUR-01 | yes | [TC-01](../flows/purchase/TC-01.md) |
| TC-02 | Two products are added and acknowledged | Scenario testing | High | PUR-02 | yes | [TC-02](../flows/purchase/TC-02.md) |
| TC-03 | Cart lists the products and the right total | Scenario testing | High | PUR-03 | yes | [TC-03](../flows/purchase/TC-03.md) |
| TC-04 | Purchase form completed and order confirmed (data-driven) | Equivalence partitioning | High | PUR-04, PUR-05 | yes | [TC-04](../flows/purchase/TC-04.md) |
| TC-05 | Customer signs out | Scenario testing | Medium | PUR-06 | yes | [TC-05](../flows/purchase/TC-05.md) |
| TC-06 | Receipt shows the purchase date (known bug BUG-001: previous month) | Scenario testing | Medium | PUR-05 | yes (`@bug`) | [TC-06](../flows/purchase/TC-06.md) |

Automated in: `src/test/resources/features/04-Full-Flow/onlineShop.feature` (`@BuyDevicesHP_DDT`; TC-06 tagged `@bug @BUG-001`, excluded from the regular run, run with `-Ptags="@bug"`).
