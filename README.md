# Accounting Ledger

## Project status

In progress.

## Features

- Add deposits to the ledger.
- Record payments as negative amounts.
- View all transactions, deposits only, or payments only.
- Display newest transactions first.
- Run date-based reports:
  - Month to date
  - Previous month
  - Year to date
  - Previous year
- Search transactions by vendor.
- Optional stretch goal: search by date range, description, vendor, and amount.

## Transaction file format

Each transaction is one line with fields separated by the pipe character:

```text
date|time|description|vendor|amount
2023-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50
2023-04-15|11:15:00|Invoice 1001 paid|Joe|1500.00
```

Dates use `yyyy-MM-dd`, times use `HH:mm:ss`, deposits use positive amounts, and payments use negative amounts. Although the file is named `transactions.csv`, its fields are pipe-delimited.

## Requirements

- Java Development Kit (JDK)
- IntelliJ IDEA or another Java IDE, or a terminal

## Run the application

After the Java source files are in the project folder, open a terminal in that folder and compile the files:

```bash
javac *.java
```

Then run the class that contains the `main` method. If that class is named `Main`, use:

```bash
java Main
```

Keep `transactions.csv` in the location expected by the application. The program should create the file if it does not exist.

## Project screens

### Home Screen

- `D` — Add Deposit
- `P` — Make Payment (Debit)
- `L` — Open the Ledger
- `X` — Exit

### Ledger

- `A` — Show all transactions
- `D` — Show deposits
- `P` — Show payments
- `R` — Open Reports
- `H` — Return Home

### Reports

- Month to date, previous month, year to date, and previous year
- Search by vendor
- Optional custom search with any combination of start date, end date, description, vendor, and amount

## Learning goals

This capstone is an opportunity to practice Java classes and objects, constructors, methods, loops, user input, collections, date handling, file reading and writing, and organizing work with Git commits.

## GitHub workflow

Work on one feature at a time and commit each meaningful milestone with a clear message, for example:

```text
Add Transaction class
Read transactions from file
Add deposit and payment menus
Display newest ledger entries first
Add date reports
Document project setup
```

## Future improvements

- Validate dates, times, and amounts entered by the user.
- Handle malformed transaction rows gracefully.
- Add the optional custom search feature.
- Improve the CLI layout and error messages.
