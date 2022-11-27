# Transaction Utility

## Usage

- Click on **Open** on the top and choose the transaction file.
- Enter the names and their aliases in the **Aliases** text field at the bottom if necessary. All aliases must be
  unique. Multiple aliases can represent the same name. A colon (`:`) is used to separate a pair of alias and name. A
  comma (`,`) is used to separate multiple pairs. Leading and trailing whitespace characters are omitted. An input
  example is `Alias1: Name1, Alias2: Name2`. During processing, aliases with be treated as the names they represent.
- Enter the names in the **Filter names** text field at the bottom if necessary. A comma (`,`) is used to separate
  multiple names. Leading and trailing whitespace characters are omitted. An input example is `Name1, Name2`. Only the
  records involving the names will be displayed.
- Click on **Calculate** on the top and the result will be displayed in the text area in the center.
- Click on **Save** on the top and the result will be saved to a file.

## Input Data Schema

The data should be stored in a CSV file.

The column definitions are as follows:

1. `Date`: \[required\] the date of the transaction in the format `yyyy-MM-dd H:mm` (e.g. 1970-01-01 0:00)
2. `Time Zone`: \[required\] the time zone of the date in several formats (e.g. +00:00, UTC+00:00, GMT+00:00)
3. `Seller`: \[required\] the seller of the item
4. `Item Number`: the item number of the item
5. `Item`: \[required\] the name of the item
6. `Unit Price (Local)`: the unit price of the item in the local currency
7. `Quantity`: the quantity of the item
8. `Tax Rate`: the tax rate in decimal
9. `Local Currency`: \[required\] the code of the local currency in [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217)
10. `Settlement Currency`: \[required\] the code of the settlement currency
    in [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217)
11. `Exchange Rate`: the ratio between the local currency and the settlement Currency
12. `Total (Local)`: \[required\] the total amount in the local currency after tax and fees
13. `Total (Settlement)`: \[required\] the total amount in the settlement currency after tax and fees
14. `Payer`: \[required\] the person who actually paid
15. `Payee`: \[required\] the person who is supposed to pay
16. `Multiplier`: the adjustment value multiplied to the total amount, usually for fees or discounts
17. `Adjustment`: the adjustment value added to the total amount, usually for fees or discounts
18. `Network`: \[optional\] the payment network of the transaction
19. `Card`: \[optional\] the card used for the transaction
20. `Method`: \[optional\] the method used for the transaction
21. `Category`: \[optional\] the category of the transaction
22. `Tag`: \[optional\] the tag of the transaction
23. `Notes`: \[optional\] the notes for the transaction
24. `Address`: \[optional\] the street address of the seller
25. `City`: \[optional\] the city of the seller
26. `State`: \[optional\] the administrative division of the seller
27. `Zip Code`: \[optional\] the zip code of the seller
28. `Country`: \[optional\] the country of the seller

The order of the columns is arbitrary.

The mathematical relations exist on the data:

- `Total (Local)` = `Unit Price (Local)` × `Quantity` × (1 + `Tax Rate`) × `Multiplier` + `Adjustment`
- `Total (Settlement)` = `Total (Local)` × `Exchange Rate`

Some constraints apply to the data:

- The columns marked as \[required\] should have the exact names. Other columns can have any arbitrary names.
- There should be no thousands separators, including commas (`50,000`) and spaces (`50 000`), in all numbers.
- There decimal markers in all numbers should be dots (`50.00`) instead of commas (`50,00`).