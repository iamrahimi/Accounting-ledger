package com.pluralsight;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;



public class Reports {


    public static void filterReportByDate(LocalDate startDate, LocalDate endDate) throws IOException, InterruptedException {

        Main.tableHeader("REPORT FROM (" + startDate.toString() + " TO (" + endDate.toString() + ") ");

        double netAmount = 0;
        double depositAmount = 0;
        double paymentAmount = 0;

        for( Transactions transaction : Main.transactionsList){
            LocalDate dateToCheck = LocalDate.parse(transaction.getDate());

            if (!dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate)) {
                netAmount += transaction.getAmount();
                depositAmount += transaction.getDepsit();
                paymentAmount += transaction.getPayment();

                System.out.println(transaction.toString());
            }
        }
        System.out.printf("\nTotal deposits: $%,.2f%n", depositAmount);
        System.out.printf("Total payments: $%,.2f%n", paymentAmount);
        System.out.printf("Net change: $%,.2f%n", netAmount);

        Main.tableFooter();
        try {
            Main.openReport();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void filterReportByVendor() throws IOException {
        System.out.println("Please enter the vendor's name:");
        String Vendor = Main.input.nextLine();
        double netAmount = 0;
        double depositAmount = 0;
        double paymentAmount = 0;

        Main.tableHeader("VENDOR REPORT (" +  Vendor + ")" );

        for(Transactions transaction : Main.transactionsList){
            if(Vendor.equalsIgnoreCase(transaction.getVendor())){
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
        try {
            Main.openReport();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
