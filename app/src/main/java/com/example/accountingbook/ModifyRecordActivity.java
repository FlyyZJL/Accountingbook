package com.example.accountingbook;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ModifyRecordActivity类用于修改现有的记录。
 * 它提供用户界面来显示和编辑记录的详细信息，并将更改保存到数据库中。
 */
public class ModifyRecordActivity extends AppCompatActivity {
    // 输入字段和按钮的视图
    private EditText amountEditText, categoryEditText, noteEditText;
    private DatePicker datePicker;
    private Button saveButton;
    // 数据库帮助类，用于访问数据库
    private DatabaseHelper dbHelper;
    // 记录ID
    private int recordId;

    /**
     * 在Activity创建时调用，初始化视图和数据
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_record);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);

        // 获取各视图组件
        amountEditText = findViewById(R.id.amountEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        noteEditText = findViewById(R.id.noteEditText);
        datePicker = findViewById(R.id.datePicker);
        saveButton = findViewById(R.id.saveButton);

        // 获取传递过来的记录ID
        recordId = getIntent().getIntExtra("recordId", -1);

        // 加载记录的详细信息
        loadRecordDetails(recordId);

        // 设置保存按钮的点击事件监听器
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = Double.parseDouble(amountEditText.getText().toString());
                String category = categoryEditText.getText().toString();
                String note = noteEditText.getText().toString();
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();

                // 更新记录并显示结果
                if (updateRecord(recordId, amount, category, date, note)) {
                    Toast.makeText(ModifyRecordActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ModifyRecordActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 加载记录的详细信息并显示在输入字段中
     *
     * @param recordId 要加载的记录ID
     */
    private void loadRecordDetails(int recordId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("records", null, "id = ?", new String[]{String.valueOf(recordId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            @SuppressLint("Range") String note = cursor.getString(cursor.getColumnIndex("note"));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));

            amountEditText.setText(String.valueOf(amount));
            noteEditText.setText(note);
            categoryEditText.setText(category);

            String[] dateParts = date.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // DatePicker月份从0开始
            int day = Integer.parseInt(dateParts[2]);

            datePicker.updateDate(year, month, day);

            cursor.close();
        }
    }

    /**
     * 更新记录的详细信息
     *
     * @param recordId 要更新的记录ID
     * @param amount   新的金额
     * @param category 新的类别
     * @param date     新的日期
     * @param note     新的备注
     * @return 是否更新成功
     */
    private boolean updateRecord(int recordId, double amount, String category, String date, String note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("category", category);
        values.put("date", date);
        values.put("note", note);
        int result = db.update("records", values, "id = ?", new String[]{String.valueOf(recordId)});
        return result > 0;
    }
}

