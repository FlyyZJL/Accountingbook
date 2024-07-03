package com.example.accountingbook;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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
                case "Food":
                    colors.add(getResources().getColor(R.color.category_food));
                    break;
                case "Transport":
                    colors.add(getResources().getColor(R.color.category_transport));
                    break;
                case "Shopping":
                    colors.add(getResources().getColor(R.color.category_shopping));
                    break;
                case "Entertainment":
                    colors.add(getResources().getColor(R.color.category_entertainment));
                    break;
                default:
                    colors.add(Color.GRAY); // 默认颜色
                    break;
            }
        }
        cursor.close();

        PieDataSet dataSet = new PieDataSet(entries, "支出类别");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        Description description = new Description();
        description.setTextSize(15);
        description.setText("支出类别");
        pieChart.setCenterTextSize(20);
        pieChart.setDescription(description);
        pieChart.setCenterText("支出类别");
        pieChart.invalidate();
    }
}
