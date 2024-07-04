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

/**
 * StatisticsActivity类用于显示用户支出的统计数据，包括各类别支出的饼图和总支出。
 */
public class StatisticsActivity extends AppCompatActivity {
    // 用于显示支出类别的饼图
    private PieChart pieChart;
    // 数据库帮助类，用于访问数据库
    private DatabaseHelper dbHelper;

    /**
     * 在Activity创建时调用，初始化视图和数据
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);
        // 获取饼图视图
        pieChart = findViewById(R.id.pieChart);

        // 加载统计数据并更新总支出
        loadStatistics();
        updateTotalExpense();
    }

    /**
     * 从数据库加载统计数据，并显示在饼图中
     */
    private void loadStatistics() {
        // 获取可读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 获取当前用户ID
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        // 查询各类别支出的总金额
        Cursor cursor = db.rawQuery("SELECT category, SUM(amount) AS total FROM records WHERE user_id = ? GROUP BY category", new String[]{userId});

        // 创建饼图数据项和颜色列表
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        while (cursor.moveToNext()) {
            // 获取类别和总金额
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") float total = cursor.getFloat(cursor.getColumnIndex("total"));
            // 添加饼图数据项
            entries.add(new PieEntry(total, category));
            // 根据类别设置不同的颜色
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

        // 设置饼图数据和属性
        PieDataSet dataSet = new PieDataSet(entries, "支出类别");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new CustomValueFormatter());
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // 设置饼图描述和中心文本
        Description description = new Description();
        description.setTextSize(15);
        description.setText("支出类别");
        pieChart.setCenterTextSize(20);
        pieChart.setDescription(description);
        pieChart.setCenterText("支出类别");

        // 设置图例的位置
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        // 刷新饼图
        pieChart.invalidate();
    }

    /**
     * 更新总支出的文本视图
     */
    private void updateTotalExpense() {
        // 获取可读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 获取当前用户ID
        String userId = String.valueOf(getIntent().getIntExtra("currentUserId", -1));
        // 查询总支出金额
        Cursor cursor = db.rawQuery("SELECT SUM(amount) AS totalExpense FROM records WHERE user_id = ?", new String[]{userId});
        if (cursor.moveToFirst()) {
            // 获取总支出金额并更新文本视图
            @SuppressLint("Range") String totalExpense = cursor.getString(cursor.getColumnIndex("totalExpense"));
            TextView totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
            totalExpenseTextView.setText("总支出:" + totalExpense);
        }
        cursor.close();
    }
}
