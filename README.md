# Accounting Ledger

A small Java accounting ledger with two ways to use it: a command-line interface (CLI) and a desktop graphical interface (GUI). Both versions use the same pipe-delimited transaction file.

## Features

### CLI

- View all transactions, deposits, or payments.
- Add deposits and record payments.
- View totals for deposits, payments, and net change.
- Run reports for month to date, previous month, year to date, previous year, and a vendor.
- Search by description, vendor, date range, or amount range.

### GUI

- Add deposits and payments through a form.
- Search descriptions and vendors from the transaction table.
- Filter the table to show all transactions, deposits, or payments.
- Filter transactions by a date range.
- View deposit, payment, and net totals.
- Refresh the table from the transaction file.

## Requirements

- Java 17 or later
- IntelliJ IDEA or another Java IDE (optional)

The GUI uses Swing, which is included with the standard JDK. No additional libraries are required.

## Project files

Place these Java files in the `com.pluralsight` package:

```text
src/main/java/com/pluralsight/
├── Main.java
├── Transactions.java
├── Reports.java
├── Search.java
└── gui/LedgerGUI.java
```

If your IntelliJ project uses `src` instead of `src/main/java`, put the files together in `src/com/pluralsight/`.

## Run the application

## Run the CLI

In IntelliJ, open `Main.java` and run its `main` method. From a terminal, if your files are under `src/main/java`, run these commands from the project root:

```bash
mkdir -p out
javac -d out src/main/java/com/pluralsight/*.java
java -cp out com.pluralsight.Main
```

The home menu offers **All**, **Add Deposit**, **Make Payment**, **Open the Ledger**, and **Exit**. From the ledger menu, open reports and custom searches.

## Run the GUI

In IntelliJ, open `LedgerGUI.java` and run its `main` method. From a terminal, compile as above, then run:

```bash
java -cp out com.pluralsight.gui.LedgerGUI
```

Run the GUI from the project root so it can find the `uploads` folder.

## Transaction file

Both interfaces use this file path, relative to the project root:

```text
uploads/transactions.csv
```

Each transaction has five fields separated by `|`:

```text
Date|Time|Description|Vendor|Amount
2026-11-01|16:20:00|Groceries|Safeway|-63.48
2026-11-02|10:05:00|Refund|Target|18.99
```

The first line is the header. The CLI reader skips the first line, so keep the header in place. The GUI creates the file and header when you add the first transaction. Deposits use positive amounts; payments use negative amounts. When entering an amount in either interface, enter a positive value—the payment action records it as negative.

Descriptions and vendor names should not contain the `|` character because it separates the fields.

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


## Future improvements

- Validate dates, times, and amounts entered by the user.
- Handle malformed transaction rows gracefully.
- Add the optional custom search feature.
- Improve the CLI layout and error messages.

  ## Notes

- The transaction file is plain text and is stored locally in the project folder.
- The CLI and GUI are separate entry points. Run `Main` for the CLI or `gui/LedgerGUI` for the desktop app.
- Both interfaces write to the same file, so use **Refresh** in the GUI to reload changes made while it is open in another program.

