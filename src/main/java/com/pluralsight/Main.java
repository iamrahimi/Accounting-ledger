package com.pluralsight;

import java.io.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

public class Main {
    public static final Scanner input = new Scanner(System.in);
    static final String RESET = "\u001B[0m"; //
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String BLUE = "\u001B[34m";

    public static ArrayList<Transactions> transactionsList = new ArrayList<>();
    public static final String fileSrc = "uploads/transactions.csv";

    public static void main(String[] args) {
        System.out.println("Welcome to Accounting ledger application");
        homeScreen();

    }

    public static void homeScreen() {
        transactionsList.clear();
        readTransaction(); // Copy all the transaction data to arrayList to operate with
        screenTitle("HOME SCREEN"); // Only for title of page

        System.out.println(GREEN +"    [D] — Add Deposit" + RESET);
        System.out.println(GREEN +"    [P] — Make Payment (Debit)" + RESET);
        System.out.println(GREEN +"    [L] — Open the Ledger" + RESET);
        System.out.println(RED  + "    [X] — Exit" + RESET);
        System.out.println("--------------------------------");
        System.out.print("Choose an option: ");

        try {
            String choice = input.nextLine();
            if (choice.equalsIgnoreCase("D")) {
                loader();
                addTransaction("deposit");
            } else if (choice.equalsIgnoreCase("P")) {
                loader();
                addTransaction("payment");
            } else if (choice.equalsIgnoreCase("L")) {
                loader();
                openLedger();
            } else if (choice.equalsIgnoreCase("X")) {
                System.out.println("Thank you for using Accounting Ledger Application");
                System.out.println("Good bye");
            } else {
                System.out.println("Wrong choice, invalid input");
                homeScreen();
            }
        }catch (InputMismatchException inputMismatchException){
            System.out.println(RED + "Invalid input. Try again." + RESET);
            homeScreen();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void addTransaction(String type) throws IOException {
        Date now = new Date();

        String date = new SimpleDateFormat("yyyy-MM-dd").format(now);
        String time = new SimpleDateFormat("HH:mm:ss").format(now);


        screenTitle("ADD "+ type.toUpperCase() +" TRANSACTION");
        System.out.println("Please enter the description:");
        String description = input.nextLine();

        System.out.println("Please enter the vendor:");
        String vendor = input.nextLine();

        System.out.println("Please enter the amount:");
        double amount = Double.parseDouble(input.nextLine());

        if(type.equalsIgnoreCase("payment")) {
            amount = - amount;
        }

        try {
            if(!description.isEmpty() || !vendor.isEmpty() || amount > 0){
                // bw = BufferedWriter
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileSrc, true));

                bw.write(date + "|" + time + "|" + description + "|" + vendor + "|" + amount);
                bw.newLine();

                System.out.println("Transaction successfully.\n");
                bw.close();
            }else {
                System.out.println("All fields are required, cannot be empty");
            }

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

        homeScreen();
    }

    public static void openLedger() throws IOException, InterruptedException {

        screenTitle("LEDGER SCREEN"); // Title of page

        System.out.println(GREEN    +"    [A] — All" + RESET);
        System.out.println(GREEN    +"    [D] — Deposit" + RESET);
        System.out.println(GREEN    +"    [P] — Payment" + RESET);
        System.out.println(GREEN    +"    [R] — Report" + RESET);
        System.out.println(RED      +"    [H] — Home" + RESET);
        System.out.println("--------------------------------");
        System.out.print("Choose an option: ");

        try {
            String choice = input.nextLine();
            if (choice.equalsIgnoreCase("A")) {
                ledgerDisplay("all");

            } else if (choice.equalsIgnoreCase("D")) {
                ledgerDisplay("deposit");

            } else if (choice.equalsIgnoreCase("P")) {
                ledgerDisplay("payment");

            } else if (choice.equalsIgnoreCase("R")) {
                openReport();

            } else if (choice.equalsIgnoreCase("H")) {
                homeScreen();

            } else  {
                System.out.println(RED + "Wrong choice, invalid input" + RESET);
                openLedger();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        loader();
    }

    public static void ledgerDisplay(String type) throws IOException, InterruptedException {

        tableHeader("LEDGER"); // This will only load the header design for the table
        double netAmount = 0;
        double depositAmount = 0;
        double paymentAmount = 0;
        for(Transactions transaction : transactionsList){
            if(type.equalsIgnoreCase("all")) {
                netAmount += transaction.getAmount();
                depositAmount += transaction.getDepsit();
                paymentAmount += transaction.getPayment();
                System.out.println(transaction.toString());

            } else if(type.equalsIgnoreCase("deposit")) {
                if(transaction.getAmount() > 0){
                    depositAmount += transaction.getDepsit();
                    System.out.println(transaction.toString());
                }

            } else if(type.equalsIgnoreCase("payment")) {
                if(transaction.getAmount() < 0){
                    paymentAmount += transaction.getPayment();
                    System.out.println(transaction.toString());
                }

            }

        }

        System.out.printf("Total deposits: $%,.2f%n", depositAmount);
        System.out.printf("Total payments: $%,.2f%n", paymentAmount);
        System.out.printf("Net change: $%,.2f%n", netAmount);

        tableFooter(); // Will only include the footer for the table.

        openLedger();
    }

    public static void openReport() throws IOException, InterruptedException {
        screenTitle("REPORT SCREEN"); // Title of page
        System.out.println(GREEN    +"    [1] — Month To Date" + RESET);
        System.out.println(GREEN    +"    [2] — Previous Month" + RESET);
        System.out.println(GREEN    +"    [3] — Year To Date" + RESET);
        System.out.println(GREEN    +"    [4] — Previous Year" + RESET);
        System.out.println(GREEN    +"    [5] - Search by Vendor"+ RESET);
        System.out.println(GREEN    +"    [6] - Custom Search"+ RESET);
        System.out.println(RED      +"    [0] — Back" + RESET);
        System.out.println("--------------------------------");
        System.out.print("Choose an option: ");
        boolean isInputInteger = true;
        try {
            int choice = input.nextInt(); input.nextLine();
            if (choice == 1) {
                LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
                LocalDate currentDateOfMonth = LocalDate.now();
               Reports.filterReportByDate(startOfMonth, currentDateOfMonth);

            } else if (choice == 2) {
                YearMonth previousMonth = YearMonth.now().minusMonths(1);
                LocalDate startDate = previousMonth.atDay(1);
                LocalDate endDate = previousMonth.atEndOfMonth();
                Reports.filterReportByDate(startDate, endDate);

            } else if(choice == 3) {
                LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
                LocalDate currentDateOfMonth = LocalDate.now();
                Reports.filterReportByDate(startOfYear, currentDateOfMonth);

            } else if(choice == 4) {
                YearMonth previousYear = YearMonth.now().minusYears(1);
                LocalDate startDate = previousYear.atDay(1);
                LocalDate endDate = Year.now().minusYears(1).atMonth(12).atEndOfMonth();
                Reports.filterReportByDate(startDate, endDate);

            } else if(choice == 5) {
                Reports.filterReportByVendor();

            } else if (choice == 6) {
                Search.main(null);

            } else if(choice == 0) {
                openLedger();
            } else {
                System.out.println(RED + "Wrong choice, invalid input" + RESET);
                openReport();
            }

        } catch (InputMismatchException e) {
            input.nextLine(); // discard the invalid text
            System.out.println(Main.RED + "Please enter a valid option!" + Main.RESET);
            isInputInteger = false;
        }

        if (!isInputInteger) {
            openReport();
        }
    }

    public static void readTransaction() {
        try {
            // br = bufferedReader
            BufferedReader br = new BufferedReader(new FileReader(fileSrc));
            br.readLine();

            String transaction = br.readLine();
            while((transaction = br.readLine()) != null) {
                String[] row = transaction.split("\\|");

                Transactions transactionRow = new Transactions(
                        row[0], // Date
                        row[1], // Time
                        row[2], // Description
                        row[3], // Vendor
                        Double.parseDouble(row[4]) // Amount
                );

                transactionsList.add(transactionRow);
            }

            transactionsList.sort( Comparator.comparing((Transactions transactionsList) ->
                            LocalDateTime.parse(
                                    transactionsList.getDate() + "T" + transactionsList.getTime()
                            ) ).reversed());

            br.close();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loader() throws InterruptedException {

        String loader = "";
        for (int i=0; i<5; i++) {
            loader += ".";
            System.out.print(loader);
            Thread.sleep(200);
        }
        System.out.println("\n");
    }

    public static void   screenTitle(String title) {
        System.out.println("================================");
        System.out.println(BLUE + "       " + title +  RESET);
        System.out.println("================================");
    }

    public static void tableHeader (String title) {
        System.out.println("============================== " + title + " =====================================");
        System.out.printf(
                "%-10s %-10s %-8s %-20s %-14s %14s%n",
                "Type",
                "Date",
                "Time",
                "Description",
                "Vendor",
                "Amount"
        );

    }


    public static void tableFooter () {
        System.out.println("--------------------------------------------------------------------------------------------");
    }



}