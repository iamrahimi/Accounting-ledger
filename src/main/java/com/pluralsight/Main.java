package com.pluralsight;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static final Scanner input = new Scanner(System.in);
    public static ArrayList<Transactions> transactionsList = new ArrayList<>();
    public static final String fileSrc = "uploads/transactions.csv";

    static final String BLUE = "\u001B[34m";
    static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        welcomeScreen();
        homeScreen();

    }

    public static void homeScreen() {

        transactionsList.clear();
        readTransaction(); // Copy all the transaction.csv data to arrayList to operate with
        screenTitle("HOME SCREEN"); // Only for title of page
        menuTitleGreen("[A] — Show all transactions");
        menuTitleGreen("[D] — Add Deposit");
        menuTitleGreen("[P] — Make Payment (Debit)");
        menuTitleGreen("[L] — Open the Ledger");
        menuTitleRed("[X] — Exit");
        System.out.println("--------------------------------");
        System.out.print("Choose an option: ");

        try {
            String choice = input.nextLine();
            loader();
            if (choice.equalsIgnoreCase("A")) {
                ledgerDisplay("all");

            } else if (choice.equalsIgnoreCase("D")) {
                addTransaction("deposit");

            }else if (choice.equalsIgnoreCase("P")) {
                addTransaction("payment");

            } else if (choice.equalsIgnoreCase("L")) {
                openLedger();

            } else if (choice.equalsIgnoreCase("X")) {
                System.out.println("Thank you for using Accounting Ledger Application");
                System.out.println("Good bye");
                System.exit(0);

            } else {
                menuTitleRed("Invalid input. Try again.");
                homeScreen();
            }
        }catch (InputMismatchException inputMismatchException){
            menuTitleRed("Invalid input. Try again.");
            homeScreen();

        } catch (InterruptedException | IOException e) {
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
                menuTitleRed("All fields are required, cannot be empty");
            }

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

        homeScreen();
    }

    public static void openLedger() throws IOException, InterruptedException {

        screenTitle("LEDGER SCREEN"); // Title of page
        menuTitleGreen("[A] — Show all transactions");
        menuTitleGreen("[D] — Show Deposit");
        menuTitleGreen("[P] — Show Payment");
        menuTitleGreen("[R] — Open Report");
        menuTitleRed("[H] — Home");
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
                Reports.openReport();

            } else if (choice.equalsIgnoreCase("H")) {
                homeScreen();

            } else  {
                menuTitleRed("Wrong choice, invalid input");
                openLedger();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

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

        System.out.printf("\nTotal deposits: $%,.2f%n", depositAmount);
        System.out.printf("Total payments: $%,.2f%n", paymentAmount);
        System.out.printf("Net change: $%,.2f%n", netAmount);

        tableFooter(); // Will only include the footer for the table.

        openLedger();
    }

    public static void readTransaction() {
        try {
            // br = bufferedReader
            BufferedReader br = new BufferedReader(new FileReader(fileSrc));

            String transaction = br.readLine();
            while((transaction = br.readLine()) != null) {
                String[] row = transaction.split("\\|");
                if(row.length == 5) {
                    Transactions transactionRow = new Transactions(
                            row[0], // Date
                            row[1], // Time
                            row[2], // Description
                            row[3], // Vendor
                            Double.parseDouble(row[4]) // Amount
                    );

                    transactionsList.add(transactionRow);
                }

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

    public static void screenTitle(String title) {
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

    public static void menuTitleGreen(String title) {
        final String GREEN = "\u001B[32m";

        System.out.println(GREEN +"    " + title + RESET);
    }

    public static void menuTitleRed(String title) {
        final String RED = "\u001B[31m";

        System.out.println(RED +"    " + title + RESET);
    }

    public static void tableFooter () {
        System.out.println("--------------------------------------------------------------------------------------------");
    }

    private static void welcomeScreen() {
        int width = 58;
        String border = "═".repeat(width);

        System.out.println(BLUE + "╔" + border + "╗" + RESET);
        System.out.println(BLUE + "║" + centerText("CLEAR LEDGER", width) + "║" + RESET);
        System.out.println(BLUE + "║" + centerText("Your money, clearly organized.", width) + "║" + RESET);
        System.out.println(BLUE + "╠" + border + "╣" + RESET);
        System.out.println(BLUE + "║" + centerText("Track deposits, payments, and reports.", width) + "║" + RESET);
        System.out.println(BLUE + "║" + centerText("Welcome! Let's get started.", width) + "║" + RESET);
        System.out.println(BLUE + "╚" + border + "╝" + RESET);
        System.out.println();
    }

    private static String centerText(String text, int width) {
        int leftPadding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - leftPadding;

        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }



}