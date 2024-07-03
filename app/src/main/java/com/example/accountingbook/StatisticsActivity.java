package com.example.accountingbook;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);
        pieChart = findViewById(R.id.pieChart);

        loadStatistics();
        updateTotalExpense(); // 更新总支出
    }

    private void loadStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        Cursor cursor = db.rawQuery("SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? GROUP BY category", new String[]{userId});
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") float total = cursor.getFloat(cursor.getColumnIndex("total"));
            entries.add(new PieEntry(total, category));
            // 为不同分类设置不同的颜色
            switch (category) {
                case "餐饮":
                    colors.add(getResources().getColor(R.color.category_food));
                    break;
                case "交通":
                    colors.add(getResources().getColor(R.color.category_transport));
                    break;
                case "购物":
                    colors.add(getResources().getColor(R.color.category_shopping));
                    break;
                case "水电气费":
                    colors.add(getResources().getColor(R.color.category_util));
                    break;
                case "娱乐":
                    colors.add(getResources().getColor(R.color.category_entertainment));
                    break;
                default:
                    colors.add(Color.LTGRAY); // 默认颜色
                    break;
            }
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
        // 设置图例的位置
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChart.invalidate();
    }
    private void updateTotalExpense() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        Cursor cursor = db.rawQuery("SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ?", new String[]{userId});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String totalExpense = cursor.getString(cursor.getColumnIndex("totalExpense"));
            TextView totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
            totalExpenseTextView.setText(("总支出:"+ totalExpense));
        }
        cursor.close();
    }
}
