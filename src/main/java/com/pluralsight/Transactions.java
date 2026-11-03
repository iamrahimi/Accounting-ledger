package com.pluralsight;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transactions {
    private String date;
    private String time;
    private String description;
    private String vendor;
    private double amount;

    public Transactions(String date, String time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public Transactions() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDepsit() {
        if(this.amount > 0) {
            return amount;
        }
        return 0;
    }

    public double getPayment() {
        if(this.amount < 0) {
            return amount;
        }
        return 0;
    }


    @Override
    public String toString() {
        String type = amount >= 0 ? "Deposit" : "Payment";

        String amountDisplay = amount < 0
                ? String.format("-$%,.2f", Math.abs(amount))
                : String.format("$%,.2f", amount);

        return String.format(
                "%-10s %-10s %-8s %-20.20s %-14.14s %14s",
                type,
                date,
                time,
                description,
                vendor,
                amountDisplay
        );
    }

}
