package com.pluralsight;

public class Transactions {
    private String date;
    private String Time;
    private String description;
    private String vendor;
    private double amount;

    public Transactions(String date, String time, String description, String vendor, double amount) {
        this.date = date;
        Time = time;
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
}
