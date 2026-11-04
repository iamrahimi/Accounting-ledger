package com.pluralsight;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Reports {
    private static Scanner input = new Scanner(System.in);

    public static void openReport() {
        Main.screenTitle("REPORT SCREEN"); // Title of page
        Main.menuTitleGreen("[1] — Month To Date");
        Main.menuTitleGreen("[2] — Previous Month");
        Main.menuTitleGreen("[3] — Year To Date");
        Main.menuTitleGreen("[4] — Previous Year");
        Main.menuTitleGreen("[5] - Search by Vendor");
        Main.menuTitleGreen("[6] - Custom Search");
        Main.menuTitleRed("[0] — Back");

        System.out.println("--------------------------------");
        System.out.print("Choose an option: ");
        boolean isInputInteger = true;
        try {
            int choice = input.nextInt(); input.nextLine();
            if (choice == 1) {
                LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
                LocalDate currentDateOfMonth = LocalDate.now();
                filterReportByDate(startOfMonth, currentDateOfMonth);

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
                YearMonth previousYear = YearMonth.now().minusYears(1).withMonth(1);
                LocalDate startOfPreviousYear = previousYear.atDay(1);
                LocalDate startDate = previousYear.atDay(1);
                LocalDate endDate = Year.now().minusYears(1).atMonth(12).atEndOfMonth();
                Reports.filterReportByDate(startOfPreviousYear, endDate);

            } else if(choice == 5) {
                Reports.filterReportByVendor();

            } else if (choice == 6) {
                Search.main(null);

            } else if(choice == 0) {
                Main.openLedger();
            } else {
                Main.menuTitleRed("Wrong choice, invalid input");
                openReport();
            }

        } catch (InputMismatchException e) {
            input.nextLine(); // discard the invalid text
            Main.menuTitleRed("Wrong choice, invalid input");
            isInputInteger = false;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        if (!isInputInteger) {
            openReport();
        }
    }

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
        openReport();

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
        openReport();
    }

}
