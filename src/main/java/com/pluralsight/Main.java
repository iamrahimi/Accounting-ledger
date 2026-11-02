package com.pluralsight;

import java.io.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final Scanner input = new Scanner(System.in);
    static final String RESET = "\u001B[0m"; //
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String BLUE = "\u001B[34m";

    private static ArrayList<Transactions> transactionsList = new ArrayList<>();
    private static final String fileSrc = "uploads/transactions.csv";

    public static void main(String[] args) {
        System.out.println("Welcome to Accounting ledger application");
        homeScreen();

    }

    public static void homeScreen() {
        transactionsList.clear();
        readTransaction();

        System.out.println("================================");
        screenTitle("HOME SCREEN");
        System.out.println("================================");
        System.out.println(GREEN +"    [D] — Add Deposit" + RESET);
        System.out.println(GREEN +"    [P] — Make Payment (Debit)" + RESET);
        System.out.println(GREEN +"    [L] — Open the Ledger" + RESET);
        System.out.println(RED + "    [X] — Exit" + RESET);
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
            System.out.println("Invalid input. Try again.");
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

    public static void openLedger() throws IOException {
        System.out.println("============================== LEDGER ======================================================");
        System.out.printf(
                "%-10s %-10s %-8s %-20s %-14s %14s%n",
                "Type",
                "Date",
                "Time",
                "Description",
                "Vendor",
                "Amount"
        );
        System.out.println("--------------------------------------------------------------------------------------------");
        for(Transactions transaction : transactionsList){
            System.out.println(transaction.toString());
        }
        System.out.println("--------------------------------------------------------------------------------------------");
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



}