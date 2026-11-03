package com.pluralsight;

import java.io.IOException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Search {

    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {

        Main.screenTitle("SEARCH (Custom) "); // Title of page
        System.out.println(Main.GREEN    +"    [1] — By description" + Main.RESET);
        System.out.println(Main.GREEN    +"    [2] — By Vendor" + Main.RESET);
        System.out.println(Main.GREEN    +"    [3] — By date" + Main.RESET);
        System.out.println(Main.GREEN    +"    [4] — By amount" + Main.RESET);
        System.out.println(Main.RED      +"    [0] — Back" + Main.RESET);
        System.out.println("--------------------------------");
        System.out.print("Choose an option: ");

        boolean isInputInteger = true;
        try {
            int chioce = input.nextInt(); input.nextLine();

            switch (chioce) {
                case 0:
                    Main.openReport();
                    break;
                case 1:
                    searchByDescription();
                    break;

                case 2:
                    Reports.filterReportByVendor();
                    break;

                case 3:
                    System.out.println("Please enter the date (e.g 2026-04-10)");
                    String startDate = input.nextLine();
                    System.out.println("Please enter the date (e.g 2026-04-10)");
                    String endDate = input.nextLine();

                    LocalDate startOfDate = LocalDate.parse(startDate);
                    LocalDate endOfDate = LocalDate.parse(endDate);
                    Reports.filterReportByDate(startOfDate, endOfDate);
                    break;

                case 4:
                    searchByAmount();
                    break;

                default:
                    System.out.println("Invalid option");
                    main(null);
                    break;
            }

        }  catch (InputMismatchException e) {
            input.nextLine(); // discard the invalid text
            System.out.println(Main.RED + "Please enter a valid option!" + Main.RESET);
            isInputInteger = false;
        }

        if (!isInputInteger) {
            main(null);
        }


    }


    public static void searchByDescription() {
        System.out.println("Please enter the department name:");
        String keyword = input.nextLine();
        System.out.println(keyword);
        Main.tableHeader("SEARCH BY DESCRIPTION (" + keyword + ")");
        double netAmount = 0;
        double depositAmount = 0;
        double paymentAmount = 0;

        for(Transactions transaction: Main.transactionsList) {
            if (transaction.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(transaction.toString());
                netAmount += transaction.getAmount();
                depositAmount += transaction.getDepsit();
                paymentAmount += transaction.getPayment();
            }
        }
        System.out.printf("\nTotal deposits: $%,.2f%n", depositAmount);
        System.out.printf("Total payments: $%,.2f%n", paymentAmount);
        System.out.printf("Net change: $%,.2f%n", netAmount);

        Main.tableFooter();
    }

    public static void searchByAmount() {
        System.out.println("Please enter start amount:");
        double amountStart = input.nextDouble();input.nextLine();
        System.out.println("Please enter end amount:");
        double amountEnd = input.nextDouble(); input.nextLine();


        Main.tableHeader("SEARCH BY AMOUNT (" + amountStart + " - " + amountEnd + ")");
        double netAmount = 0;
        double depositAmount = 0;
        double paymentAmount = 0;

        for(Transactions transaction: Main.transactionsList) {
            if (transaction.getAmount() >= amountStart && transaction.getAmount() <= amountEnd) {
                System.out.println(transaction.toString());
                netAmount += transaction.getAmount();
                depositAmount += transaction.getDepsit();
                paymentAmount += transaction.getPayment();
            }
        }


        System.out.printf("\nTotal deposits: $%,.2f%n", depositAmount);
        System.out.printf("Total payments: $%,.2f%n", paymentAmount);
        System.out.printf("Net change: $%,.2f%n", netAmount);

        Main.tableFooter();
    }

}
