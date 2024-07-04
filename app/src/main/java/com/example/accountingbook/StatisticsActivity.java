package com.example.accountingbook;



import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private DatabaseHelper dbHelper;
    private Button selectDateButton, selectRangeButton;
    private String selectedDate;
    private String selectedRange = "日"; // 默认选择“日”范围

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);
        pieChart = findViewById(R.id.pieChart);
        pieChart.setNoDataText("请先选择日期和范围");
        pieChart.setNoDataTextColor(Color.BLACK);
        Paint paint = pieChart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(60f);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectRangeButton = findViewById(R.id.selectRangeButton);

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        selectRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRangeDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                loadStatistics();
                updateTotalExpense();
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showRangeDialog() {
        final String[] items = {"日", "月", "年"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择范围");
        builder.setItems(items, (dialog, which) -> {
            selectedRange = items[which];
            loadStatistics();
            updateTotalExpense();
        });
        builder.show();
    }

    private void loadStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        Cursor cursor = null;
        String query = "";
        String[] queryArgs = null;

        if (selectedDate != null) {
            switch (selectedRange) {
                case "日":
                    query = "SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? AND date = ? GROUP BY category";
                    queryArgs = new String[]{userId, selectedDate};
                    break;
                case "月":
                    String monthPrefix = selectedDate.substring(0, 7); // yyyy-MM
                    query = "SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? AND date LIKE ? GROUP BY category";
                    queryArgs = new String[]{userId, monthPrefix + "%"};
                    break;
                case "年":
                    String yearPrefix = selectedDate.substring(0, 4); // yyyy
                    query = "SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? AND date LIKE ? GROUP BY category";
                    queryArgs = new String[]{userId, yearPrefix + "%"};
                    break;
            }

            cursor = db.rawQuery(query, queryArgs);
        }

        if (cursor == null) {
            Toast.makeText(this, "请选择日期", Toast.LENGTH_SHORT).show();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") float total = cursor.getFloat(cursor.getColumnIndex("total"));
            entries.add(new PieEntry(total, category));
            colors.add(ColorTemplate.COLORFUL_COLORS[entries.size() % ColorTemplate.COLORFUL_COLORS.length]);
        }
        cursor.close();

        PieDataSet dataSet = new PieDataSet(entries, "支出类别");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new CustomValueFormatter());
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        Description description = new Description();
        description.setTextSize(15);
        description.setText("支出类别");
        pieChart.setCenterTextSize(20);
        pieChart.setDescription(description);
        pieChart.setCenterText("支出类别");

        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.invalidate();
    }

    private void updateTotalExpense() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        Cursor cursor = null;
        String query = "";
        String[] queryArgs = null;

        if (selectedDate != null) {
            switch (selectedRange) {
                case "日":
                    query = "SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ? AND date = ?";
                    queryArgs = new String[]{userId, selectedDate};
                    break;
                case "月":
                    String monthPrefix = selectedDate.substring(0, 7);
                    query = "SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ? AND date LIKE ?";
                    queryArgs = new String[]{userId, monthPrefix + "%"};
                    break;
                case "年":
                    String yearPrefix = selectedDate.substring(0, 4);
                    query = "SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ? AND date LIKE ?";
                    queryArgs = new String[]{userId, yearPrefix + "%"};
                    break;
            }

            cursor = db.rawQuery(query, queryArgs);
        }

        if (cursor == null) {
            Toast.makeText(this, "请选择日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String totalExpense = cursor.getString(cursor.getColumnIndex("totalExpense"));
            TextView totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
            totalExpenseTextView.setText("总支出: " + totalExpense);
        }
        cursor.close();
    }
}
