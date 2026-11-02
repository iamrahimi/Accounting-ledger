package com.pluralsight;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transactions {
    private String date;
    private String Time;
    private String description;
    private String vendor;
    private double amount;
    private String transactionType;

    public Transactions(String date, String time, String description, String vendor, double amount,  String transactionType) {
        this.date = date;
        Time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
        this.transactionType = transactionType;
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
        return Time;
    }

    public void setTime(String time) {
        Time = time;
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
