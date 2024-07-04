package com.example.accountingbook;


import java.math.BigDecimal;

/**
 * Record 类的对象用于表示数据库中的一条记录。
 * 该类包含记录的基本信息，如 ID、用户ID、金额、类别、日期和备注。
 * Record 类的对象由 RecordAdapter 类使用，用于显示记录列表。
 */
public class Record {
    private int id; // 记录ID
    private int userId; // 用户ID
    private BigDecimal amount; // 金额
    private String category; // 类别
    private String date; // 日期
    private String note; // 备注

    /**
     * Record 类的构造函数，用于初始化记录项。
     *
     * @param id       记录ID
     * @param userId   用户ID
     * @param amount   金额
     * @param category 类别
     * @param date     日期
     * @param note     备注
     */
    public Record(int id, int userId, BigDecimal amount, String category, String date, String note) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    /**
     * 获取记录ID。
     *
     * @return 记录ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取用户ID。
     *
     * @return 用户ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 获取金额。
     *
     * @return 金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 获取类别。
     *
     * @return 类别
     */
    public String getCategory() {
        return category;
    }

    /**
     * 获取日期。
     *
     * @return 日期
     */
    public String getDate() {
        return date;
    }

    /**
     * 获取备注。
     *
     * @return 备注
     */
    public String getNote() {
        return note;
    }
}
