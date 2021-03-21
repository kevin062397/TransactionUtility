# Transaction Utility

## Usage

- Run `Transaction Utility.jar`. Java Runtime Environment (JRE) is required.

- Click on **Open** on the top and choose the transaction file.

- Enter the names in the **Filter** text field at the bottom if necessary. A comma (`,`) is used to separate multiple names. Only the records involving the names will be calculated and displayed.

- Click on **Calculate** on the top and the result will be displayed in the text area in the center.

## Input Data Schema

The data should be stored in a CSV file.

The column definitions are as follows:

1. `Date`: the date of the transaction
2. `Seller`: the seller of the item
3. `Item`: the name of the item
4. `Unit Price (Local)`: the unit price of the item in the local currency
5. `Quantity`: the quantity of the item
6. `Tax Rate`: the tax rate in decimal
7. `Local Currency`: the code of the local currency in [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217)
8. `Settlement Currency`: the code of the settlement currency in [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217)
9. `Exchange Rate`: the ratio between the local currency and the settlement Currency
10. `Total (Local)`: the total amount in the local currency after tax and fees
11. `Total (Settlement)`: the total amount in the settlement currency after tax and fees
12. `Payer`: the person who actually paid
13. `Payee`: the person who is supposed to pay
14. `Multiplier`: the adjustment value multiplied to the total amount, usually for fees or discounts
15. `Adjustment`: the adjustment value added to the total amount, usually for fees or discounts
16. `Network`: the payment network of the payment method (optional metadata)
17. `Category`: the category of the transaction (optional metadata)
18. `Tag`: the tag of the transaction (optional metadata)
19. `Address`: the street address of the seller (optional metadata)
20. `City`: the city of the seller (optional metadata)
21. `State`: the administrative division of the seller (optional metadata)
22. `Country`: the country of the seller (optional metadata)

The mathematical relations exist on the data:

- `Total (Local)` = `Unit Price (Local)` × `Quantity` × (1 + `Tax Rate`) × `Multiplier` + `Adjustment`

- `Total (Settlement)` = `Unit Price (Local)` × `Quantity` × (1 + `Tax Rate`) × `Exchange Rate` × `Multiplier` + `Adjustment`

Some constraints apply to the data:

- The column names are arbitraty but the order should be exact.

- Columns 14 and after are ignored.

- There should be no commas (`,`) in all fields, including the thousands separators in numbers.

- All currency values should have one-character currency symbol followed by the number.

- One table can only use one settlement currency.

- The payer and payee identifiers are case-sensitive and are limited to three characters.