package com.example.accountingbook;


import java.math.BigDecimal;

public class Record {
    private int id;
    private int userId;
    private BigDecimal amount;
    private String category;
    private String date;
    private String note;

    public Record(int id, int userId, BigDecimal amount, String category, String date, String note) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}
