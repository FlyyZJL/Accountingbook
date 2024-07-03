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

public class ModifyRecordActivity extends AppCompatActivity {
    private EditText amountEditText, categoryEditText, noteEditText;
    private DatePicker datePicker;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_record);

        dbHelper = new DatabaseHelper(this);

        amountEditText = findViewById(R.id.amountEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        noteEditText = findViewById(R.id.noteEditText);
        datePicker = findViewById(R.id.datePicker);
        saveButton = findViewById(R.id.saveButton);

        // 获取记录ID
        recordId = getIntent().getIntExtra("recordId", -1);

        loadRecordDetails(recordId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = Double.parseDouble(amountEditText.getText().toString());
                String category = categoryEditText.getText().toString();
                String note = noteEditText.getText().toString();
                String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();

                if (updateRecord(recordId, amount, category, date, note)) {
                    Toast.makeText(ModifyRecordActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ModifyRecordActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
