package com.example.accountingbook;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;

/**
 * AddRecordActivity类用于添加新的记账记录
 */
public class AddRecordActivity extends AppCompatActivity {
    private EditText amountEditText, noteEditText;
    private Spinner categorySpinner;
    private DatePicker datePicker;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    /**
     * 当活动创建时调用
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        dbHelper = new DatabaseHelper(this);

        // 初始化UI元素
        amountEditText = findViewById(R.id.amountEditText);
        noteEditText = findViewById(R.id.noteEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        datePicker = findViewById(R.id.datePicker);
        saveButton = findViewById(R.id.saveButton);

        // 获取当前用户ID
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        // 设置Spinner的适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // 设置保存按钮的点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal amount = new BigDecimal(amountEditText.getText().toString());
                String note = noteEditText.getText().toString();
                String category = categorySpinner.getSelectedItem().toString();
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                //非空校验
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    Toast.makeText(AddRecordActivity.this, "金额必须大于0", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (saveRecord(currentUserId, amount, category, date, note)) {
                    Toast.makeText(AddRecordActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddRecordActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 保存新的记账记录
     *
     * @param userId   用户ID
     * @param amount   金额
     * @param category 分类
     * @param date     日期
     * @param note     备注
     * @return 如果记录成功插入返回true，否则返回false
     */
    private boolean saveRecord(int userId, BigDecimal amount, String category, String date, String note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId); // 存储用户ID
        values.put("amount", amount.toPlainString());
        values.put("category", category);
        values.put("date", date);
        values.put("note", note);
        long result = db.insert("records", null, values);
        return result != -1;
    }
}

